package com.github.wrdlbrnft.modularadapter.itemmanager.sortedlist;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.proguardannotations.KeepClass;
import com.github.wrdlbrnft.proguardannotations.KeepClassMembers;
import com.github.wrdlbrnft.proguardannotations.KeepSetting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@KeepClass
@KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
public class ComparatorBuilder<T extends SortedListItemManager.ViewModel> {

    interface ComparatorRule {
        boolean isApplicable(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b);
        int apply(SortedListItemManager.ViewModel a, SortedListItemManager.ViewModel b);
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