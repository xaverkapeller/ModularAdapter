package com.github.wrdlbrnft.modularadapter.itemmanager;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 01/04/2017
 */
public interface ChangeSet {

    interface AdapterInterface {
        void notifyDataSetChanged();
        void notifyMove(int fromPosition, int toPosition);
        void notifyAdd(int index, int count);
        void notifyRemove(int index, int count);
        void notifyChange(int index, int count);
    }

    void applyTo(AdapterInterface adapter);
}
