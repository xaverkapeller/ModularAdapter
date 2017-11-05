package com.github.wrdlbrnft.modularadapter.itemmanager.firestore;

import android.app.Activity;

import com.github.wrdlbrnft.modularadapter.itemmanager.ItemManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 22/10/2017
 */

public class FireStoreItemManager<T> implements ItemManager<T> {

    public interface ItemMapper<T> {
        T apply(DocumentSnapshot document);
    }

    private final ItemMapper<T> mMapper;
    private final List<ChangeSetCallback> mChangeSetCallbacks = new ArrayList<>();

    private List<DocumentSnapshot> mDocuments = new ArrayList<>();

    public FireStoreItemManager(Activity activity, CollectionReference collectionReference, ItemMapper<T> mapper) {
        mMapper = mapper;

        collectionReference.addSnapshotListener(activity, (documentSnapshots, e) -> {
            mDocuments = documentSnapshots.getDocuments();
            for (ChangeSetCallback changeSetCallback : mChangeSetCallbacks) {
                changeSetCallback.onChangeSetAvailable((moveCallback, addCallback, removeCallback, changeCallback) -> {
                    for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                addCallback.add(change.getNewIndex(), 1);
                                break;
                            case MODIFIED:
                                if (change.getNewIndex() == change.getOldIndex()) {
                                    changeCallback.change(change.getNewIndex(), 1);
                                } else {
                                    moveCallback.move(change.getOldIndex(), change.getNewIndex());
                                }
                                break;
                            case REMOVED:
                                removeCallback.remove(change.getOldIndex(), 1);
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public T getItem(int position) {
        final DocumentSnapshot document = mDocuments.get(position);
        return mMapper.apply(document);
    }

    @Override
    public int getItemCount() {
        if (mDocuments == null) {
            return 0;
        }
        return mDocuments.size();
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

    }

    @Override
    public void removeStateCallback(StateCallback callback) {

    }
}
