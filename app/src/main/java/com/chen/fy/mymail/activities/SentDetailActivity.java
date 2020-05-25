package com.chen.fy.mymail.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.beans.SentItem;
import com.chen.fy.mymail.utils.ZoomImageLoader;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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

    private ViewStub vsDetail;  //显示详情
    private TextView tvDetail;

    private ViewStub vsAttachment;  //显示附件信息

    private String mSubject;
    private String mAddress;
    private String mDate;
    private String mContent;

    private boolean isInitViewStub = false;
    private File mFile;

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
        vsAttachment = findViewById(R.id.vs_attachment_sent_detail);

        tvDetail.setOnClickListener(this);
        findViewById(R.id.iv_return_sent_detail).setOnClickListener(this);
        findViewById(R.id.iv_delete_sent_detail).setOnClickListener(this);
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

    //初始化附件信息ViewStub
    private void initAttachmentViewStub() {
        vsAttachment.inflate();
        ImageView ivAttachmentLogo = findViewById(R.id.iv_logo_attachment_info);
        TextView ivAttachmentName = findViewById(R.id.tv_name_attachment_info);
        TextView ivAttachmentSize = findViewById(R.id.tv_size_attachment_info);
        findViewById(R.id.ll_attachment_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new XPopup.Builder(ivAttachmentLogo.getContext())
                        .asImageViewer(
                                ivAttachmentLogo
                                , Uri.fromFile(mFile)
                                , new ZoomImageLoader())
                        .show();
            }
        });

        if (mFile != null) {
            Glide.with(this).load(mFile).into(ivAttachmentLogo);
            ivAttachmentName.setText(mFile.getName());
            ivAttachmentSize.setText((((int) mFile.length()) / 1024) + "k");
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
            case R.id.iv_delete_sent_detail:
                showDeletePopup(mAddress);
                break;
        }
    }

    private void initDetailViewStub() {
        vsDetail.inflate();
        tvRecipient = findViewById(R.id.tv_recipient_sent_detail_vs);
        tvDate = findViewById(R.id.tv_date_sent_detail);

        tvRecipient.setText(mAddress);
        tvDate.setText(mDate);
    }

    /**
     * 显示删除的弹出框
     */
    private void showDeletePopup(String address) {

        new XPopup.Builder(this)
                .asConfirm("是否选择删除", "删除后此已发送邮件的信息将彻底消失",
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
                    Toast.makeText(SentDetailActivity.this,
                            "查询失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SentDetailActivity.this,
                            "删除成功", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(SentDetailActivity.this,
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
