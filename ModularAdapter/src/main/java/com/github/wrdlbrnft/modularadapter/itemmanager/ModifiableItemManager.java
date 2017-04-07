package com.github.wrdlbrnft.modularadapter.itemmanager;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.proguardannotations.KeepClass;
import com.github.wrdlbrnft.proguardannotations.KeepClassMembers;
import com.github.wrdlbrnft.proguardannotations.KeepSetting;

import java.util.Collection;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 04/04/2017
 */
@KeepClass
@KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
public interface ModifiableItemManager<T> extends ItemManager<T> {

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    interface Transaction<T> {
        Transaction<T> add(@NonNull T item);
        Transaction<T> add(@NonNull Collection<T> items);
        Transaction<T> remove(@NonNull T item);
        Transaction<T> remove(@NonNull Collection<T> items);
        Transaction<T> replaceAll(@NonNull Collection<T> items);
        Transaction<T> removeAll();
        void commit();
    }

    Transaction<T> newTransaction();
}
