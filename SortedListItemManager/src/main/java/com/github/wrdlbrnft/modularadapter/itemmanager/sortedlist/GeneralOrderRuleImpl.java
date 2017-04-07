package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 27/03/2017
 */
class GeneralOrderRuleImpl implements ComparatorBuilder.ComparatorRule {

    private final Class<? extends SortedListItemManager.ViewModel>[] mModelClasses;

    GeneralOrderRuleImpl(Class<? extends SortedListItemManager.ViewModel>[] modelClasses) {
        mModelClasses = modelClasses;
    }

    @Override
    public boolean isApplicable(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b) {
        final Class<? extends SortedListItemManager.ViewModel> clazzA = a.getClass();
        final Class<? extends SortedListItemManager.ViewModel> clazzB = b.getClass();
        return !clazzA.equals(clazzB)
                && RuleUtils.isClassAssignableToOneOf(mModelClasses, clazzA)
                && RuleUtils.isClassAssignableToOneOf(mModelClasses, clazzB);
    }

    @Override
    public int apply(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b) {
        final Class<? extends SortedListItemManager.ViewModel> clazzA = a.getClass();
        final Class<? extends SortedListItemManager.ViewModel> clazzB = b.getClass();
        return Integer.signum(RuleUtils.getIndexOfClass(mModelClasses, clazzA) - RuleUtils.getIndexOfClass(mModelClasses, clazzB));
    }
}
