package com.github.wrdlbrnft.modularadapter.itemmanager.list;

import com.github.wrdlbrnft.modularadapter.itemmanager.ItemManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 04/04/2017
 */
public class StaticListItemManager<T> implements ItemManager<T> {

    private final List<T> mList;

    public StaticListItemManager(List<T> list) {
        mList = new ArrayList<>(list);
    }

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

    }

    @Override
    public void removeChangeSetCallback(ChangeSetCallback callback) {

    }

    @Override
    public void addStateCallback(StateCallback callback) {

    }

    @Override
    public void removeStateCallback(StateCallback callback) {

    }
}
