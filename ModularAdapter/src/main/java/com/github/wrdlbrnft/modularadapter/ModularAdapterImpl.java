package com.github.wrdlbrnft.modularadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.wrdlbrnft.modularadapter.itemmanager.ItemManager;

import java.util.List;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 27/03/2017
 */
class ModularAdapterImpl<T> extends ModularAdapter<T> {

    static class Module<M, VH extends ViewHolder<M>> {

        private final int mViewType;
        private final Class<M> mItemClass;
        private final ViewHolderFactory<VH> mHolderFactory;

        Module(int viewType, Class<M> itemClass, ViewHolderFactory<VH> holderFactory) {
            mViewType = viewType;
            mItemClass = itemClass;
            mHolderFactory = holderFactory;
        }
    }

    private final List<Module<?, ?>> mModules;

    ModularAdapterImpl(Context context, ItemManager<T> itemManager, List<Module<?, ?>> modules) {
        super(context, itemManager);
        mModules = modules;
    }

    @Override
    @NonNull
    protected ViewHolder<? extends T> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        for (Module<?, ?> module : mModules) {
            if (module.mViewType == viewType) {
                return (ViewHolder<? extends T>) module.mHolderFactory.create(inflater, parent);
            }
        }

        throw new IllegalStateException("No mapping for " + viewType + " exists.");
    }
    
    @Override
    public int getViewTypeOf(Class<? extends T> model) {
        for (Module<?, ?> module : mModules) {
            if (module.mItemClass.isAssignableFrom(model)) {
                return module.mViewType;
            }
        }

        throw new IllegalStateException("No mapping for " + model + " exists.");
    }

    @Override
    public int getItemViewType(int position) {
        final T item = getItem(position);
        final Class<? extends T> itemClass = (Class<? extends T>) item.getClass();
        return getViewTypeOf(itemClass);
    }
}
