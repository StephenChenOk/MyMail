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
import com.chen.fy.mymail.adapter.DraftAdapter;
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.interfaces.IItemClickListener;
import com.chen.fy.mymail.utils.DateUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * 草稿箱
 */
public class DraftBoxActivity extends AppCompatActivity implements View.OnClickListener
        , IItemClickListener {

    final static int DRAFT_DETAIL_REQUEST_CODE = 4;

    private RecyclerView mRecyclerView;
    private DraftAdapter mAdapter;

    private List<DraftItem> listData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_box_layout);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_return_draft).setOnClickListener(this);
        findViewById(R.id.iv_write_email_draft).setOnClickListener(this);

        mRecyclerView = findViewById(R.id.rv_draft);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {

        listData = getData();

        mAdapter = new DraftAdapter(this, listData);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    //从Bmob中获取数据
    private List<DraftItem> getData() {
        List<DraftItem> items = new ArrayList<>();
        BmobQuery<DraftItem> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<DraftItem>() {
            @Override
            public void done(List<DraftItem> object, BmobException e) {
                if (e == null) {
                    items.addAll(object);

                    //排序
                    Collections.sort(items, (o1, o2) -> {
                        Date date1 = DateUtils.stringToDate(o1.getCreatedAt());
                        Date date2 = DateUtils.stringToDate(o2.getCreatedAt());
                        if (date1 != null && date2 != null) {
                            return date2.compareTo(date1);
                        }
                        return 1;
                    });

                    //更新数据
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        return items;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_draft:
                finish();
                break;
            case R.id.iv_write_email_draft:
                Intent intent = new Intent(this, WriteEmailActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClickItem(String subject, String address, String date, String content) {
        //单击跳转草稿详情页面
        Intent intent = new Intent(this, DraftItemDetailActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("address", address);
        intent.putExtra("date", date);
        intent.putExtra("content", content);
        startActivityForResult(intent, DRAFT_DETAIL_REQUEST_CODE);
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
            case DRAFT_DETAIL_REQUEST_CODE:
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
                        () -> queryDraftItemId(address),
                        null,
                        false)
                .bindLayout(R.layout.delete_contact_person_popup)
                .show();
    }

    /**
     * 根据邮件地址查找草稿箱Item对应的Id
     */
    private void queryDraftItemId(String address) {
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
        BmobQuery<DraftItem> query = new BmobQuery<>();
        query.addWhereEqualTo("address", address);
        query.findObjects(new FindListener<DraftItem>() {
            @Override
            public void done(List<DraftItem> object, BmobException e) {
                if (e == null) {
                    for (DraftItem draftItem : object) {
                        deleteDraftItem(loadingPopup, draftItem.getObjectId());
                        break;
                    }
                } else {
                    Log.d("chenyisheng", "查询失败");
                }
            }
        });
    }

    /**
     * 删除相应的草稿
     */
    private void deleteDraftItem(BasePopupView loadingPopup, String objectId) {
        DraftItem draftItem = new DraftItem();
        draftItem.setObjectId(objectId);

        draftItem.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(DraftBoxActivity.this,
                            "删除成功", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(DraftBoxActivity.this,
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
