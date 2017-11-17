package com.licl.learn.easyrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * recyclerView通用适配器
 * Created by licl on 2017/11/4.
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {


    protected Context mContext;
    protected LayoutInflater mInflater;
    private List<T> mItems = new ArrayList<>();
    private OnItemClickListener mListener;
    private int mResId;

    public CommonAdapter(Context context) {
        this(context, null);
    }

    public CommonAdapter(Context context, List<T> items) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        refreshItems(items, true);
    }

    public CommonAdapter(Context context, List<T> items, int resId) {
        this.mContext = context;
        mResId = resId;
        mInflater = LayoutInflater.from(context);
        refreshItems(items, true);
    }

    public void refreshItems(List<T> items, boolean clearData) {
        resetItems(clearData);
        if (items != null && items.size() > 0) {
            this.mItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    private void resetItems(boolean clearData) {
        if (clearData && mItems.size() > 0) {
            mItems.clear();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(getResId(), null);
        view.setLayoutParams(getLayoutParams());

        ViewHolder holder = ViewHolder.createViewHolder(mContext, view);
        holder.getConvertView().setOnClickListener(this);
        return holder;
    }

    protected int getResId() {
        return mResId;
    }

    protected RecyclerView.LayoutParams getLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getConvertView().setTag(position);
        convert(holder, getContentItem(position), position);
    }

    public void convert(ViewHolder holder, T t, int position){

    }


    public List<T> getItems() {
        return mItems;
    }

    public T getContentItem(int position) {
        return position < mItems.size() ? mItems.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            int position = (int) v.getTag();
            mListener.onItemClick(getContentItem(position), v, position);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t, View childView, int position);
    }
}
