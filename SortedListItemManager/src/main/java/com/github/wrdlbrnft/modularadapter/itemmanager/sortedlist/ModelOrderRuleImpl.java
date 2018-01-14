package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import java.util.Comparator;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 27/03/2017
 */
class ModelOrderRuleImpl<M extends SortedListItemManager.ViewModel> implements ComparatorBuilder.ComparatorRule {

    private final Class<M> mModelClass;
    private final Comparator<M> mComparator;

    ModelOrderRuleImpl(Class<M> modelClass, Comparator<M> comparator) {
        mModelClass = modelClass;
        mComparator = comparator;
    }

    @Override
    public boolean isApplicable(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b) {
        final Class<? extends SortedListItemManager.ViewModel> clazzA = a.getClass();
        final Class<? extends SortedListItemManager.ViewModel> clazzB = b.getClass();
        return mModelClass.isAssignableFrom(clazzA)
                && mModelClass.isAssignableFrom(clazzB);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int apply(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b) {
        return mComparator.compare((M) a, (M) b);
    }

    @Priority
    @Override
    public int getPriority() {
        return PRIORITY_HIGH;
    }
}
