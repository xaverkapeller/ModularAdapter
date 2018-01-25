package com.github.wrdlbrnft.modularadapter.itemmanager.list;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.modularadapter.itemmanager.ChangeSet;
import com.github.wrdlbrnft.modularadapter.itemmanager.ModifiableItemManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 04/04/2017
 */
public class BasicItemManager<T> implements ModifiableItemManager<T> {

    private final List<ChangeSetCallback> mChangeSetCallbacks = new ArrayList<>();
    private final List<StateCallback> mStateCallbacks = new ArrayList<>();

    private final List<T> mList = new ArrayList<>();

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
        return new TransactionImpl<>(mList, mChangeSetCallbacks, mStateCallbacks);
    }

    private static class TransactionImpl<T> implements Transaction<T> {

        public interface Action<T> {
            void applyTo(List<T> input);
        }

        private final List<Action<T>> mActions = new ArrayList<>();

        private final List<T> mList;
        private final List<ChangeSetCallback> mChangeSetCallbacks;
        private final List<StateCallback> mStateCallbacks;


        private TransactionImpl(List<T> list, List<ChangeSetCallback> changeSetCallbacks, List<StateCallback> stateCallbacks) {
            mChangeSetCallbacks = changeSetCallbacks;
            mList = list;
            mStateCallbacks = stateCallbacks;
        }

        @Override
        public Transaction<T> add(@NonNull T item) {
            mActions.add(list -> list.add(item));
            return this;
        }

        @Override
        public Transaction<T> add(@NonNull Collection<T> items) {
            mActions.add(list -> list.addAll(items));
            return this;
        }

        @Override
        public Transaction<T> remove(@NonNull T item) {
            mActions.add(list -> list.remove(item));
            return this;
        }

        @Override
        public Transaction<T> remove(@NonNull Collection<T> items) {
            mActions.add(list -> list.removeAll(items));
            return this;
        }

        @Override
        public Transaction<T> replaceAll(@NonNull Collection<T> items) {
            mActions.add(list -> {
                list.clear();
                list.addAll(items);
            });
            return this;
        }

        @Override
        public Transaction<T> removeAll() {
            mActions.add(List::clear);
            return this;
        }

        @Override
        public void commit() {
            for (StateCallback callback : mStateCallbacks) {
                callback.onChangesInProgress();
            }
            for (Action<T> action : mActions) {
                action.applyTo(mList);
            }
            for (ChangeSetCallback callback : mChangeSetCallbacks) {
                callback.onChangeSetAvailable(ChangeSet.AdapterInterface::notifyDataSetChanged);
            }
            for (StateCallback callback : mStateCallbacks) {
                callback.onChangesFinished();
            }
        }
    }
}
