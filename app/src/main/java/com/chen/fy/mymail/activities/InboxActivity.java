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
import com.chen.fy.mymail.adapter.InboxAdapter;
import com.chen.fy.mymail.asyncTasks.ReceiveAsyncTask;
import com.chen.fy.mymail.beans.InboxItem;
import com.chen.fy.mymail.interfaces.IReceiveAsyncResponse;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.List;

/**
 * 收件箱
 */
public class InboxActivity extends AppCompatActivity implements View.OnClickListener, IReceiveAsyncResponse {

    private RecyclerView mRecyclerView;
    private InboxAdapter mAdapter;

    private BasePopupView mLoadingPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_layout);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        findViewById(R.id.iv_write_email).setOnClickListener(this);

        mRecyclerView = findViewById(R.id.rv_inbox);
    }

    private void initData() {

        mLoadingPopup = new XPopup.Builder(this)
                .asLoading("正在获取邮件")
                .show();
        mLoadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        mRecyclerView.setLayoutManager(layoutManager);

        ReceiveAsyncTask receiveAsyncTask = new ReceiveAsyncTask();
        receiveAsyncTask.setOnAsyncResponse(this);
        receiveAsyncTask.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_write_email:
                Intent intent = new Intent(this, WriteEmailActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDataReceivedSuccess(List<InboxItem> listData) {

        mAdapter = new InboxAdapter(this, listData);
        mRecyclerView.setAdapter(mAdapter);

        mLoadingPopup.dismiss();
    }

    @Override
    public void onDataReceivedFailed() {
        Toast.makeText(this,"获取邮件失败",Toast.LENGTH_LONG).show();
        mLoadingPopup.dismiss();
    }
}
