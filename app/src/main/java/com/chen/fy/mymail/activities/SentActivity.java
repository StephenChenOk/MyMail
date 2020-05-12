package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.adapter.SentAdapter;
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.beans.SentItem;
import com.chen.fy.mymail.interfaces.IItemClickListener;
import com.chen.fy.mymail.utils.DateUtils;
import com.lxj.xpopup.core.BasePopupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 已发送
 */
public class SentActivity extends AppCompatActivity implements View.OnClickListener
        , IItemClickListener {

    private static final int SENT_DETAIL_REQUEST_CODE = 6;
    private RecyclerView mRecyclerView;
    private SentAdapter mAdapter;

    private BasePopupView mLoadingPopup;
    private List<SentItem> listData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sent_layout);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_return_sent).setOnClickListener(this);
        findViewById(R.id.iv_write_email_sent).setOnClickListener(this);

        mRecyclerView = findViewById(R.id.rv_sent);
    }

    private void initData() {

//        mLoadingPopup = new XPopup.Builder(this)
//                .asLoading("正在读取...")
//                .show();
//        mLoadingPopup.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        mRecyclerView.setLayoutManager(layoutManager);

        listData = getData();

        mAdapter = new SentAdapter(this, listData);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    //从Bmob中获取数据
    private List<SentItem> getData() {
        List<SentItem> items = new ArrayList<>();
        BmobQuery<SentItem> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<SentItem>() {
            @Override
            public void done(List<SentItem> object, BmobException e) {
                if (e == null) {
                    items.addAll(object);

                    //排序
                    Collections.sort(items, new Comparator<SentItem>() {
                        @Override
                        public int compare(SentItem o1, SentItem o2) {
                            Date date1 = DateUtils.stringToDate(o1.getCreatedAt());
                            Date date2 = DateUtils.stringToDate(o2.getCreatedAt());
                            if (date1 != null && date2 != null) {
                                return date2.compareTo(date1);
                            }
                            return 1;
                        }
                    });
                    //更新数据
                    mAdapter.notifyDataSetChanged();
                    // mLoadingPopup.dismiss();
                }
            }
        });
        return items;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_sent:
                finish();
                break;
            case R.id.iv_write_email_sent:
                Intent intent = new Intent(this, WriteEmailActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClickItem(String subject, String address, String date, String content) {
        //单击跳转已发送邮件详情页面
        Intent intent = new Intent(this, SentDetailActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("address", address);
        intent.putExtra("date", date);
        intent.putExtra("content", content);
        startActivityForResult(intent, SENT_DETAIL_REQUEST_CODE);
    }

    @Override
    public void onLongClickItem(String address) {

    }
}
