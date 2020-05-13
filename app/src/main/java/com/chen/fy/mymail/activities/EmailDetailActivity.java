package com.chen.fy.mymail.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;

import java.io.File;


/**
 * 邮件Item详情信息
 */
public class EmailDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSubject;
    private TextView tvRecipient;   //接收者
    private TextView tvContent;
    private TextView tvSenderShowDetail;

    private ViewStub vsDetail;
    private TextView tvDetail;

    private ViewStub vsAttachmentView;

    private String mSubject;
    private String mAddress;
    private String mDate;
    private String mContent;
    private File mFile;

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
        vsAttachmentView = findViewById(R.id.vs_attachment_email_detail);

        tvDetail.setOnClickListener(this);
        findViewById(R.id.iv_return_email_detail).setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            mAddress = getIntent().getStringExtra("address");
            mSubject = getIntent().getStringExtra("subject");
            mDate = getIntent().getStringExtra("date");
            mContent = getIntent().getStringExtra("content");
            mFile = (File) getIntent().getSerializableExtra("file");

            tvSenderShowDetail.setText(mAddress);
            tvSubject.setText(mSubject);
            tvContent.setText(mContent);
            if (mFile != null) {
                initAttachmentViewStub();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_email_detail:
                finish();
                break;
            case R.id.tv_detail_email_detail:
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

    //初始化详情ViewStub
    private void initDetailViewStub() {
        vsDetail.inflate();
        //发送者
        TextView tvSender = findViewById(R.id.tv_sender_email_detail);
        TextView tvDate = findViewById(R.id.tv_date_email_detail);

        tvSender.setText(mAddress);
        tvDate.setText(mDate);
    }

    //初始化附件ViewStub
    private void initAttachmentViewStub() {
        vsAttachmentView.inflate();
        ImageView ivAttachmentLogo = findViewById(R.id.iv_logo_attachment_email_detail);
        TextView ivAttachmentName = findViewById(R.id.tv_name_attachment_email_detail);
        TextView ivAttachmentSize = findViewById(R.id.tv_size_attachment_email_detail);

        vsAttachmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (mFile != null) {
            Glide.with(this).load(mFile).into(ivAttachmentLogo);
            ivAttachmentName.setText(mFile.getName());
            ivAttachmentSize.setText((((int) mFile.length()) / 1024)+"k");
        }

    }
}
