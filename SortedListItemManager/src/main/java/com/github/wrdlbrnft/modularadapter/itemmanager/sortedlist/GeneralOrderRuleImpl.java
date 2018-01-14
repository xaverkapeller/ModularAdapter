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
        if (clazzA.equals(clazzB)) {
            return false;
        }

        boolean clazzAMatch = false;
        boolean clazzBMatch = false;

        for (Class<? extends SortedListItemManager.ViewModel> clazz : mModelClasses) {
            final boolean clazzAIsAssignable = clazz.isAssignableFrom(clazzA);
            final boolean clazzBIsAssignable = clazz.isAssignableFrom(clazzB);
            if (clazzAIsAssignable && clazzBIsAssignable) {
                return false;
            }

            clazzAMatch |= clazzAIsAssignable;
            clazzBMatch |= clazzBIsAssignable;
        }

        return clazzAMatch && clazzBMatch;
    }

    @Override
    public int apply(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b) {
        final Class<? extends SortedListItemManager.ViewModel> clazzA = a.getClass();
        final Class<? extends SortedListItemManager.ViewModel> clazzB = b.getClass();
        return Integer.signum(RuleUtils.getIndexOfClass(mModelClasses, clazzA) - RuleUtils.getIndexOfClass(mModelClasses, clazzB));
    }

    @Priority
    @Override
    public int getPriority() {
        return PRIORITY_LOW;
    }
}
