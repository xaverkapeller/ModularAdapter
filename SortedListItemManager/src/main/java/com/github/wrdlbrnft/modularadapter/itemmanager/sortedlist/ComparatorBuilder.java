package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorBuilder<T extends SortedListItemManager.ViewModel> {

    interface ComparatorRule {

        int PRIORITY_HIGH = 0x04;
        int PRIORITY_LOW = 0x02;
        int PRIORITY_NONE = 0x01;

        @IntDef({PRIORITY_HIGH, PRIORITY_LOW, PRIORITY_NONE})
        @interface Priority {
        }

        boolean isApplicable(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b);
        int apply(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b);
        @Priority
        int getPriority();
    }

    private final List<ComparatorRule> mComparatorRules = new ArrayList<>();

    @SafeVarargs
    public final ComparatorBuilder<T> setGeneralOrder(@NonNull Class<? extends T>... modelClasses) {
        if (modelClasses.length > 1) {
            mComparatorRules.add(new GeneralOrderRuleImpl(modelClasses));
        }
        return this;
    }

    public final <M extends T> ComparatorBuilder<T> setOrderForModel(@NonNull Class<M> modelClass, @NonNull Comparator<M> comparator) {
        mComparatorRules.add(new ModelOrderRuleImpl<>(modelClass, comparator));
        return this;
    }

    public final Comparator<T> build() {
        final List<ComparatorRule> rules = new ArrayList<>(mComparatorRules);
        Collections.sort(rules, (a, b) -> Integer.signum(a.getPriority() - b.getPriority()));
        return (a, b) -> {
            for (ComparatorRule comparatorRule : mComparatorRules) {
                if (comparatorRule.isApplicable(a, b)) {
                    return comparatorRule.apply(a, b);
                }
            }
            return 0;
        };
    }
}