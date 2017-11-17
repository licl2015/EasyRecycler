package com.licl.learn.easyrecycler;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.licl.learn.easyrecyclerview.BaseEasyAdapter;
import com.licl.learn.easyrecyclerview.EasyRecyclerView;
import com.licl.learn.easyrecyclerview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BaseEasyAdapter.OnItemClickListener, EasyRecyclerView.OnRecyclerRefreshListener {

    private EasyRecyclerView erv_recycler;
    private ResultAdapter mResultAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        DataTask task = new DataTask();
        task.execute(erv_recycler.getPageIndex());
    }

    private void initView() {
        erv_recycler = (EasyRecyclerView) findViewById(R.id.erv_recycler);
        erv_recycler.setOnRecyclerRefreshListener(this);
    }

    @Override
    public void onRefreshUp() {
        erv_recycler.refreshUpStop();
    }

    @Override
    public void onRefreshDown() {
        erv_recycler.nextPage();
        DataTask task = new DataTask();
        task.execute(erv_recycler.getPageIndex());
    }

    class DataTask extends AsyncTask<String, Void, Body> {

        @Override
        protected Body doInBackground(String... params) {
            List<DataObj> mData = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                mData.add(new DataObj("书籍 =" + i));
            }
            Body body = new Body();
            body.pageTotal = "20";
            body.pageIndex = params[0];
            body.datas = mData;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return body;
        }

        @Override
        protected void onPostExecute(Body body) {
            initContentItems(body);
        }
    }

    /**
     * 加载当前页面数据
     */
    private void initContentItems(Body body) {
        erv_recycler.setPageInfo(body.pageTotal, body.pageIndex);
        if (mResultAdapter == null) {
            mResultAdapter = new ResultAdapter(mContext, body.datas);
            mResultAdapter.setOnItemClickListener(this);
            erv_recycler.setAdapter(mResultAdapter);
        } else {
            mResultAdapter.refreshItems(body.datas, erv_recycler.isFirstPage());
        }
        erv_recycler.onRefreshComplete();
    }

    @Override
    public void onItemClick(View childView, int position) {

    }

    private static class ResultAdapter extends BaseEasyAdapter<DataObj> {

        public ResultAdapter(Context context, List items) {
            super(context, items);
        }

        @Override
        protected int getNoramlResId() {
            return R.layout.activity_easy_item_layout;
        }

        @Override
        protected void onBindNormalHolder(ViewHolder holder, int position) {
            TextView tv_name = holder.getView(R.id.tv_name);
            tv_name.setText(getContentItem(position).name);
        }
    }

    class DataObj {
        public DataObj(String name) {
            this.name = name;
        }

        public String name;
    }

    class Body {
        public String pageTotal;
        public String pageIndex;
        public List<DataObj> datas;
    }
}
