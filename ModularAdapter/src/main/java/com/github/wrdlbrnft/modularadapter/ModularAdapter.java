package com.github.wrdlbrnft.modularadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.wrdlbrnft.modularadapter.itemmanager.ChangeSet;
import com.github.wrdlbrnft.modularadapter.itemmanager.ItemManager;
import com.github.wrdlbrnft.proguardannotations.KeepClass;
import com.github.wrdlbrnft.proguardannotations.KeepClassMembers;
import com.github.wrdlbrnft.proguardannotations.KeepMember;
import com.github.wrdlbrnft.proguardannotations.KeepSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 13/08/16
 */
@KeepClass
@KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
public abstract class ModularAdapter<T> extends RecyclerView.Adapter<ModularAdapter.ViewHolder<? extends T>> {

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    public abstract static class ViewHolder<T> extends RecyclerView.ViewHolder {

        private T mCurrentItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public final void bind(T item) {
            mCurrentItem = item;
            performBind(item);
        }

        @KeepMember
        protected abstract void performBind(@NonNull T item);

        public final T getCurrentItem() {
            return mCurrentItem;
        }
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    public interface ViewHolderFactory<VH extends ViewHolder<?>> {
        VH create(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);
    }

    @KeepClass
    @KeepClassMembers(KeepSetting.PUBLIC_MEMBERS)
    public static class Builder<T> {

        private final List<ModularAdapterImpl.Module<?, ?>> mModules = new ArrayList<>();

        private final Context mContext;
        private final ItemManager<T> mItemManager;

        public Builder(@NonNull Context context, @NonNull ItemManager<T> itemManager) {
            mContext = context;
            mItemManager = itemManager;
        }

        public <M extends T, VH extends ViewHolder<M>> Builder<T> add(@NonNull Class<M> modelClass, @NonNull ViewHolderFactory<VH> holderFactory) {
            mModules.add(new ModularAdapterImpl.Module<M, VH>(
                    mModules.size(),
                    modelClass,
                    holderFactory
            ));
            return this;
        }

        public ModularAdapter<T> build() {
            return new ModularAdapterImpl<>(mContext, mItemManager, mModules);
        }
    }

    private final ItemManager<T> mItemManager;
    private final LayoutInflater mInflater;

    public ModularAdapter(@NonNull Context context, @NonNull ItemManager<T> itemManager) {
        mInflater = LayoutInflater.from(context);
        mItemManager = itemManager;

        itemManager.addChangeSetCallback(changeSet -> changeSet.applyTo(
                this::notifyItemMoved,
                this::notifyItemRangeInserted,
                this::notifyItemRangeRemoved,
                this::notifyItemRangeChanged
        ));
    }

    public ItemManager<T> getItemManager() {
        return mItemManager;
    }

    @Override
    public final ViewHolder<? extends T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(mInflater, parent, viewType);
    }

    @KeepMember
    @NonNull
    protected abstract ViewHolder<? extends T> onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(ViewHolder<? extends T> holder, int position) {
        final T item = getItem(position);
        ((ViewHolder<T>) holder).bind(item);
    }

    @Override
    public final int getItemCount() {
        return mItemManager.getItemCount();
    }

    @NonNull
    public final T getItem(int position) {
        return mItemManager.getItem(position);
    }
}