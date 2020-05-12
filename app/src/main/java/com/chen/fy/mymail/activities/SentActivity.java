package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        mRecyclerView.setLayoutManager(layoutManager);
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
        //长按删除草稿
        showDeletePopup(address);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SENT_DETAIL_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    initData();
                }
                break;
        }
    }

    /**
     * 显示删除的弹出框
     */
    private void showDeletePopup(String address) {

        new XPopup.Builder(this)
                .asConfirm("是否选择删除", "删除后此联系人的信息将彻底消失",
                        "取消", "确定",
                        () -> querySentItemId(address),
                        null,
                        false)
                .bindLayout(R.layout.delete_contact_person_popup)
                .show();
    }

    /**
     * 根据邮件地址查找草稿箱Item对应的Id
     */
    private void querySentItemId(String address) {
        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("正在删除中...")
                .show();
        loadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //根据邮件地址查询Id
        BmobQuery<SentItem> query = new BmobQuery<>();
        query.addWhereEqualTo("recipientAddress", address);
        query.findObjects(new FindListener<SentItem>() {
            @Override
            public void done(List<SentItem> object, BmobException e) {
                if (e == null) {
                    for (SentItem sentItem : object) {
                        deleteSentItem(loadingPopup, sentItem.getObjectId());
                        break;
                    }
                } else {
                    loadingPopup.dismiss();
                    Log.d("chenyisheng", "查询失败");
                }
            }
        });
    }

    /**
     * 删除相应的草稿
     */
    private void deleteSentItem(BasePopupView loadingPopup, String objectId) {
        SentItem sentItem = new SentItem();
        sentItem.setObjectId(objectId);

        sentItem.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(SentActivity.this,
                            "删除成功", Toast.LENGTH_LONG).show();
                    initData();
                } else {
                    Toast.makeText(SentActivity.this,
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
