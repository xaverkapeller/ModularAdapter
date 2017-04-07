package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 27/03/2017
 */
class FacadeImpl<T> implements SortedListItemManager.Facade<T> {

    private List<T> mCurrentState = null;

    @Override
    public synchronized T getItem(int position) {
        if (mCurrentState != null) {
            return mCurrentState.get(position);
        }
        throw new NoSuchElementException();
    }

    @Override
    public synchronized int size() {
        if (mCurrentState != null) {
            return mCurrentState.size();
        }
        return 0;
    }

    @Override
    public void setState(List<T> data) {
        mCurrentState = data;
    }
}
