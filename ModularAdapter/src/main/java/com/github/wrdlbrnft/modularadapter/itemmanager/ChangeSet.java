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
public interface ChangeSet {

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface MoveCallback {
        void move(int fromPosition, int toPosition);
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface AddCallback {
        void add(int index, int count);
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface RemoveCallback {
        void remove(int index, int count);
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface ChangeCallback {
        void change(int index, int count);
    }

    void applyTo(
            MoveCallback moveCallback,
            AddCallback addCallback,
            RemoveCallback removeCallback,
            ChangeCallback changeCallback
    );
}
