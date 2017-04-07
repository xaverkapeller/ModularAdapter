package com.github.wrdlbrnft.modularadapter.itemmanager;

import com.github.wrdlbrnft.proguardannotations.KeepClass;
import com.github.wrdlbrnft.proguardannotations.KeepClassMembers;
import com.github.wrdlbrnft.proguardannotations.KeepSetting;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 01/04/2017
 */
@KeepClass
@KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
public interface ItemManager<T> {

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface ChangeSetCallback {
        void onChangeSetAvailable(ChangeSet changeSet);
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface StateCallback {
        void onChangesInProgress();
        void onChangesFinished();
    }

    T getItem(int position);
    int getItemCount();

    void addChangeSetCallback(ChangeSetCallback callback);
    void removeChangeSetCallback(ChangeSetCallback callback);

    void addStateCallback(StateCallback callback);
    void removeStateCallback(StateCallback callback);
}
