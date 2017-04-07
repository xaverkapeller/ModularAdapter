package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import com.github.wrdlbrnft.modularadapter.itemmanager.ChangeSet;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 01/04/2017
 */
interface ChangeConsumer extends ChangeSet.MoveCallback, ChangeSet.AddCallback, ChangeSet.RemoveCallback, ChangeSet.ChangeCallback {

}
