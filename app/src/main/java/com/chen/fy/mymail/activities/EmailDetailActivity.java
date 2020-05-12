package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.DraftItem;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 邮件Item详情信息
 */
public class EmailDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSubject;
    private TextView tvSender;      //发送者
    private TextView tvRecipient;   //接收者
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvSenderShowDetail;

    private ViewStub vsDetail;
    private TextView tvDetail;

    private String mSubject;
    private String mAddress;
    private String mDate;
    private String mContent;

    private boolean isInitViewStub = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_detail);

        initView();
        initData();
    }

    private void initView() {
        vsDetail = findViewById(R.id.vs_email_detail);
        tvSubject = findViewById(R.id.tv_subject_email_detail);
        tvContent = findViewById(R.id.tv_content_email_detail);
        tvDetail = findViewById(R.id.tv_detail_email_detail);
        tvSenderShowDetail = findViewById(R.id.tv_sender_show_detail);

        tvDetail.setOnClickListener(this);
        findViewById(R.id.iv_return_email_detail).setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            mAddress = getIntent().getStringExtra("address");
            mSubject = getIntent().getStringExtra("subject");
            mDate = getIntent().getStringExtra("date");
            mContent = getIntent().getStringExtra("content");

            tvSenderShowDetail.setText(mAddress);
            tvSubject.setText(mSubject);
            tvContent.setText(mContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_email_detail:
                finish();
                break;
            case R.id.tv_detail_email_detail:
                if(!isInitViewStub){
                    isInitViewStub = true;
                    initDetailViewStub();
                }
                if(tvDetail.getText().toString().equals("详情")) {
                    tvDetail.setText("隐藏");
                    vsDetail.setVisibility(View.VISIBLE);
                }else{
                    tvDetail.setText("详情");
                    vsDetail.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initDetailViewStub(){
        vsDetail.inflate();
        tvSender = findViewById(R.id.tv_sender_email_detail);
        tvDate = findViewById(R.id.tv_date_email_detail);

        tvSender.setText(mAddress);
        tvDate.setText(mDate);
    }

}
