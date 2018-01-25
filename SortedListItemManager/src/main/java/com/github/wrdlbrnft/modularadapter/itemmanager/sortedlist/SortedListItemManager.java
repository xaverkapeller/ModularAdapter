package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;

import com.github.wrdlbrnft.modularadapter.itemmanager.ChangeSet;
import com.github.wrdlbrnft.modularadapter.itemmanager.ItemManager;
import com.github.wrdlbrnft.modularadapter.itemmanager.ModifiableItemManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 01/04/2017
 */
public class SortedListItemManager<T extends SortedListItemManager.ViewModel> implements ModifiableItemManager<T> {

    public interface ViewModel {
        <T> boolean isSameModelAs(@NonNull T model);
        <T> boolean isContentTheSameAs(@NonNull T model);
    }

    private interface Action<T extends ViewModel> {
        void perform(SortedList<T> list);
    }

    interface Facade<T> {
        T getItem(int position);
        int size();
        void setState(List<T> data);
    }

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private final ChangeCache mChangeCache = new ChangeCache();
    private final BlockingDeque<List<Action<T>>> mCommitQueue = new LinkedBlockingDeque<>();
    private final AtomicBoolean mCommitInProgress = new AtomicBoolean(false);
    private final List<ChangeSetCallback> mChangeSetCallbacks = new ArrayList<>();
    private final List<StateCallback> mStateCallbacks = new ArrayList<>();

    private final Class<T> mItemClass;
    private final Comparator<T> mComparator;
    private final SortedList<T> mSortedList;

    public SortedListItemManager(Class<T> itemClass, Comparator<T> comparator) {
        mItemClass = itemClass;
        mComparator = comparator;
        mSortedList = new SortedList<>(mItemClass, mChangeCache);
    }

    @Override
    public T getItem(int position) {
        return mChangeCache.getItem(position);
    }

    @Override
    public int getItemCount() {
        return mChangeCache.getItemCount();
    }

    @Override
    public void addChangeSetCallback(ChangeSetCallback callback) {
        mChangeSetCallbacks.add(callback);
    }

    @Override
    public void removeChangeSetCallback(ChangeSetCallback callback) {
        mChangeSetCallbacks.remove(callback);
    }

    @Override
    public void addStateCallback(StateCallback callback) {
        mStateCallbacks.add(callback);
    }

    @Override
    public void removeStateCallback(StateCallback callback) {
        mStateCallbacks.remove(callback);
    }

    @Override
    public Transaction<T> newTransaction() {
        return new TransactionImpl();
    }

    private class TransactionImpl implements Transaction<T> {

        private final List<Action<T>> mActions = new ArrayList<>();

        @Override
        public Transaction<T> add(@NonNull T item) {
            mActions.add(list -> mSortedList.add(item));
            return this;
        }

        @Override
        public Transaction<T> add(@NonNull Collection<T> items) {
            mActions.add(list -> mSortedList.addAll(items));
            return this;
        }

        @Override
        public Transaction<T> remove(@NonNull T item) {
            mActions.add(list -> mSortedList.remove(item));
            return this;
        }

        @Override
        public Transaction<T> remove(@NonNull Collection<T> items) {
            mActions.add(list -> {
                @SuppressWarnings("unchecked")
                final T[] array = items.toArray((T[]) Array.newInstance(mItemClass, items.size()));
                Arrays.sort(array, mComparator);
                for (T item : array) {
                    mSortedList.remove(item);
                }
            });
            return this;
        }

        @Override
        public Transaction<T> replaceAll(@NonNull Collection<T> items) {
            mActions.add(list -> {
                @SuppressWarnings("unchecked")
                final T[] array = items.toArray((T[]) Array.newInstance(mItemClass, items.size()));
                Arrays.sort(array, mComparator);
                for (int i = mSortedList.size() - 1; i >= 0; i--) {
                    final T currentItem = mSortedList.get(i);
                    final int index = Arrays.binarySearch(array, currentItem, mComparator);
                    if (index < 0) {
                        mSortedList.remove(currentItem);
                    }
                }
                mSortedList.addAll(array, true);
            });
            return this;
        }

        @Override
        public Transaction<T> removeAll() {
            mActions.add(list -> mSortedList.clear());
            return this;
        }

        @Override
        public void commit() {
            final List<Action<T>> actions = new ArrayList<>(mActions);
            mActions.clear();
            MAIN_HANDLER.post(() -> initializeCommit(actions));
        }

        private void initializeCommit(List<Action<T>> actions) {
            mCommitQueue.add(actions);
            if (!mCommitInProgress.getAndSet(true)) {
                startTransaction();
            }
        }

        private void startTransaction() {
            final Thread updateThread = new Thread(this::performTransactions);
            updateThread.start();
            notifyTransactionsStarted();
        }

        private void notifyTransactionsStarted() {
            for (StateCallback callback : mStateCallbacks) {
                callback.onChangesInProgress();
            }
        }

        private void performTransactions() {
            try {
                while (!mCommitQueue.isEmpty()) {
                    final List<Action<T>> actions = mCommitQueue.pollFirst();
                    if (actions == null) {
                        return;
                    }
                    mSortedList.beginBatchedUpdates();
                    for (Action<T> action : actions) {
                        action.perform(mSortedList);
                    }
                    mSortedList.endBatchedUpdates();
                    mChangeCache.applyChanges();
                }
            } finally {
                mCommitInProgress.set(false);
                MAIN_HANDLER.post(this::notifyTransactionsFinished);
            }
        }

        private void notifyTransactionsFinished() {
            for (StateCallback callback : mStateCallbacks) {
                callback.onChangesFinished();
            }
        }
    }

    private interface Change {
        void apply(ChangeSet.AdapterInterface consumer);
    }

    private class ChangeCache extends SortedList.Callback<T> {

        private final List<Change> mCurrentChanges = new ArrayList<>();
        private final Facade<T> mFacade = new FacadeImpl<>();

        void applyChanges() {
            final List<Change> changes = new ArrayList<>(mCurrentChanges);
            mCurrentChanges.clear();

            final List<T> currentState = captureState();

            MAIN_HANDLER.post(() -> {
                mFacade.setState(currentState);
                for (ChangeSetCallback changeSetCallback : mChangeSetCallbacks) {
                    changeSetCallback.onChangeSetAvailable(adapter -> {
                        for (Change change : changes) {
                            change.apply(adapter);
                        }
                    });
                }
            });
        }

        public T getItem(int position) {
            return mFacade.getItem(position);
        }

        public int getItemCount() {
            return mFacade.size();
        }

        @NonNull
        private List<T> captureState() {
            final List<T> currentState = new ArrayList<>();
            for (int i = 0, count = mSortedList.size(); i < count; i++) {
                currentState.add(mSortedList.get(i));
            }
            return currentState;
        }

        @Override
        public int compare(T a, T b) {
            return mComparator.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(T oldItem, T newItem) {
            return oldItem.isContentTheSameAs(newItem);
        }

        @Override
        public boolean areItemsTheSame(T item1, T item2) {
            return item1.isSameModelAs(item2);
        }

        @Override
        public void onInserted(int position, int count) {
            mCurrentChanges.add(consumer -> consumer.notifyAdd(position, count));
        }

        @Override
        public void onRemoved(int position, int count) {
            mCurrentChanges.add(consumer -> consumer.notifyRemove(position, count));
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            mCurrentChanges.add(consumer -> consumer.notifyMove(fromPosition, toPosition));
        }

        @Override
        public void onChanged(int position, int count) {
            mCurrentChanges.add(consumer -> consumer.notifyChange(position, count));
        }
    }
}
