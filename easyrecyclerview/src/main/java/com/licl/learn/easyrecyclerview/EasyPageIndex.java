package com.licl.learn.easyrecyclerview;

/**
 * 滑动刷新章节数
 * Created by licl on 2017/11/7.
 */

public class EasyPageIndex {

    /**
     * 当前页数
     */
    private int pageIndex;
    /**
     * 每页的数量
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int pageTotal;
    /**
     * 总数量
     */
    private int total;

    public EasyPageIndex(int pageTotal) {
        pageIndex = 1;
        this.pageTotal = pageTotal;
    }

    public EasyPageIndex() {
        pageIndex = 1;
        this.pageTotal = 10;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * 重置为第一页
     */
    public void resetPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * 重置为第一页
     */
    public void resetPageIndex() {
        this.pageIndex = 1;
    }

    /**
     * 获取下一页
     *
     * @return
     */
    public void nextPage() {
        pageIndex++;
    }

    /**
     * 是否为最后一页
     *
     * @return
     */
    public boolean isLastPage() {
        return pageIndex == pageTotal;
    }

    /**
     * 是否为最后一页
     *
     * @return
     */
    public boolean isFirstPage() {
        return pageIndex == 1;
    }

}
