package com.licl.learn.easyrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Recycler下拉加载基类
 * Created by licl on 2017/11/4.
 */
public abstract class BaseEasyAdapter<T> extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {

    private static final int RECYCLER_NORMAL = 0;
    private static final int RECYCLER_FOOTER = 1;
    private static final int RECYCLER_HEADER = 2;

    public enum FooterState {
        LOADING_STATE, END_STATE;
    }


    protected Context mContext;
    protected LayoutInflater mInflater;
    private RecyclerView mRecycler;
    private List<T> mItems = new ArrayList<>();
    private OnItemClickListener mListener;
    private FooterState mState = FooterState.LOADING_STATE;
    private View mHeaderView;
    private List<T> mAddItems;

    public BaseEasyAdapter(Context context) {
        this(context, null);
    }

    public BaseEasyAdapter(Context context, List<T> items) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        refreshItems(items, true);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecycler = recyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) mRecycler.getLayoutManager();
    }

    public void refreshItems(List<T> items, boolean isFirstPage) {
        this.mAddItems = items;
        setLastPageState(items, isFirstPage);
        resetItems(isFirstPage);
        if (items != null && items.size() > 0) {
            this.mItems.addAll(items);
        }
    }

    private void setLastPageState(List<T> items, boolean isFirstPage) {
        if (!isFirstPage && (items == null || items.isEmpty())) {
            setFooterState(FooterState.END_STATE);
        }
    }

    public boolean isEmptyForAddItem() {
        return mAddItems == null || mAddItems.size() == 0;
    }

    private void resetItems(boolean clearData) {
        if (clearData && mItems.size() > 0) {
            mItems.clear();
        }
    }

    public List<T> getItems() {
        return mItems;
    }

    public void setHeaderView(View view) {
        this.mHeaderView = view;
    }

    public void setHeaderView(int resId) {
        this.mHeaderView = mInflater.inflate(resId, null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == RECYCLER_NORMAL) {
            view = mInflater.inflate(getNoramlResId(), null);
        } else if (viewType == RECYCLER_FOOTER) {
            view = mInflater.inflate(R.layout.easy_recycler_footer_layout, null);
        } else if (viewType == RECYCLER_HEADER) {
            view = mHeaderView;
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        view.setOnClickListener(this);
        return ViewHolder.createViewHolder(mContext, view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getConvertView().setTag(position);
        int viewType = getItemViewType(position);
        if (viewType == RECYCLER_NORMAL) {
            onBindNormalHolder(holder, position);
        } else if (viewType == RECYCLER_FOOTER) {
            onBindFooterHolder(holder, position);
        } else if (viewType == RECYCLER_HEADER) {
            onBindHeaderHolder(holder, position);
        }
    }

    protected abstract int getNoramlResId();

    protected abstract void onBindNormalHolder(ViewHolder holder, int position);

    protected void onBindHeaderHolder(ViewHolder holder, int position) {

    }

    private void onBindFooterHolder(ViewHolder holder, int position) {
        holder.getView(R.id.cp_cicle_progress).setVisibility(hasStateEnd() ? View.GONE : View.VISIBLE);
        ((TextView) holder.getView(R.id.tv_load_more_text)).setText(hasStateEnd() ? "已经到底了～" : "正在加载...");
    }


    @Override
    public int getItemViewType(int position) {
        if (hasHeaderView() && position == 0) {
            return RECYCLER_HEADER;
        }
        if (hasFooterView() && position == getItemCount() - 1) {
            return RECYCLER_FOOTER;
        }
        return RECYCLER_NORMAL;
    }


    @Override
    public final int getItemCount() {
        return getNormalItemCount() + getOtherItemCount();
    }

    private int getOtherItemCount() {
        return (hasFooterView() ? 1 : 0) + (hasHeaderView() ? 1 : 0);
    }

    public T getContentItem(int position) {
        return position < mItems.size() ? mItems.get(position) : null;
    }

    private int getNormalItemCount() {
        return mItems.size();
    }

    private boolean hasHeaderView() {
        return mHeaderView != null;
    }

    public boolean hasFooterView() {
        if (getLayoutManager() != null) {
            int firstCompPos = getLayoutManager().findFirstCompletelyVisibleItemPosition();
            if (firstCompPos == 0) {//第一屏
                int firstVis = getLayoutManager().findFirstVisibleItemPosition();
                int lastVis = getLayoutManager().findLastVisibleItemPosition();
                if (firstVis == -1 || lastVis == -1) {
                    return false;
                } else {
                    int count = lastVis - firstVis + 1;
                    return count != getNormalItemCount() + (hasHeaderView() ? 1 : 0);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void setFooterState(FooterState state) {
        this.mState = state;
    }

    public boolean hasStateEnd() {
        return mState == FooterState.END_STATE;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (mListener != null && getItemViewType(position) == RECYCLER_NORMAL) {
            mListener.onItemClick(getLayoutManager().findViewByPosition(position), position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View childView, int position);
    }
}
