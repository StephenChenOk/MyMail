package com.chen.fy.mymail.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.interfaces.IItemClickListener;

/**
 * 邮件Item详情信息
 */
public class SentDetailActivity extends AppCompatActivity implements View.OnClickListener{

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
        setContentView(R.layout.sent_detail);

        initView();
        initData();
    }

    private void initView() {
        vsDetail = findViewById(R.id.vs_sent_detail);
        tvSubject = findViewById(R.id.tv_subject_sent_detail);
        tvContent = findViewById(R.id.tv_content_sent_detail);
        tvDetail = findViewById(R.id.tv_detail_sent_detail);
        tvSenderShowDetail = findViewById(R.id.tv_sender_show_detail_sent);

        tvDetail.setOnClickListener(this);
        findViewById(R.id.iv_return_sent_detail).setOnClickListener(this);
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
            case R.id.iv_return_sent_detail:
                finish();
                break;
            case R.id.tv_detail_sent_detail:
                if (!isInitViewStub) {
                    isInitViewStub = true;
                    initDetailViewStub();
                }
                if (tvDetail.getText().toString().equals("详情")) {
                    tvDetail.setText("隐藏");
                    vsDetail.setVisibility(View.VISIBLE);
                } else {
                    tvDetail.setText("详情");
                    vsDetail.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initDetailViewStub() {
        vsDetail.inflate();
        tvSender = findViewById(R.id.tv_sender_sent_detail);
        tvDate = findViewById(R.id.tv_date_sent_detail);

        tvSender.setText(mAddress);
        tvDate.setText(mDate);
    }
}
