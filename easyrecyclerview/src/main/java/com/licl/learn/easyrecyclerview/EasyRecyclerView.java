package com.licl.learn.easyrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 上拉加载下拉刷新的RecyclerView
 *
 * @author licl
 * @date 2017/11/4
 */

public class EasyRecyclerView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private RecyclerView mRv_recycler;
    private BaseEasyAdapter mAdapter;
    private OnRecyclerRefreshListener mListener;
    private int footerPos;
    private boolean firstMove = false;
    private EasyPageIndex mPageIndex;


    public EasyRecyclerView(Context context) {
        this(context, null);
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPageIndex = new EasyPageIndex();
        initView();
    }

    private void initView() {
        setDistanceToTriggerSync(100);
        setColorSchemeColors(getResources().getColor(R.color.common_color_53c4ff));
        setOnRefreshListener(this);
        initRecyclerView();
    }


    private void initRecyclerView() {
        mRv_recycler = new RecyclerView(mContext);
        mRv_recycler.setLayoutParams(getParams());
        mRv_recycler.setLayoutManager(new LinearLayoutManager(mContext));
        mRv_recycler.addOnScrollListener(mOnScrollListener);
        addView(mRv_recycler);
    }

    @NonNull
    private RecyclerView.LayoutParams getParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
    }

    private LinearLayoutManager getLinearManager() {
        return (LinearLayoutManager) mRv_recycler.getLayoutManager();
    }

    private boolean firstMoveUp(int dy) {
        return !firstMove && dy > 0;
    }

    private boolean isLastPos() {
        return getLinearManager().findLastVisibleItemPosition() == getLinearManager().getItemCount() - 1;
    }

    /**
     * 保证滑动底部获取最终的Footer位置
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (isLastPos() && firstMoveUp(dy) && mListener != null) {
                footerPos = getLinearManager().findLastVisibleItemPosition();
                firstMove = true;
                mListener.onRefreshDown();
            }
        }
    };

    /**
     * 保证下拉在顶部刷新
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getLinearManager().findFirstVisibleItemPosition() == 0) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setAdapter(BaseEasyAdapter adapter) {
        this.mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.setRecyclerView(mRv_recycler);
            mRv_recycler.setAdapter(adapter);
        }
    }

    public void onRefreshComplete() {
        if (isFirstPage()) {
            mAdapter.notifyDataSetChanged();
            mRv_recycler.scrollToPosition(0);
            firstMove = false;
        } else {
            if (!mAdapter.isEmptyForAddItem()) {
                notifyRemoveItem();
                firstMove = false;
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 加载数据之前移除之前的加载View
     */
    private void notifyRemoveItem() {
        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(footerPos);
        }
    }

    @Override
    public void onRefresh() {
        if (mListener != null) {
            mListener.onRefreshUp();
        }
    }

    public void setOnRecyclerRefreshListener(OnRecyclerRefreshListener listener) {
        this.mListener = listener;
    }

    public void refreshUpStop() {
        setRefreshing(false);
    }

    /****翻页 操作 start****/

    public String getPageIndex() {
        return String.valueOf(mPageIndex.getPageIndex());
    }

    public void resetPageIndex() {
        mPageIndex.resetPageIndex();
    }

    public void nextPage() {
        mPageIndex.nextPage();
    }

    public void setPageTotal(String pageTotal) {
        mPageIndex.setPageTotal(Integer.parseInt(pageTotal));
    }

    public void setPageInfo(String pageTotal, String pageIndex) {
        mPageIndex.resetPageIndex(Integer.parseInt(pageIndex));
        mPageIndex.setPageTotal(Integer.parseInt(pageTotal));
    }

    public boolean isFirstPage() {
        return mPageIndex.isFirstPage();
    }

    /****翻页 操作 end****/

    public interface OnRecyclerRefreshListener {
        void onRefreshUp();

        void onRefreshDown();
    }
}
