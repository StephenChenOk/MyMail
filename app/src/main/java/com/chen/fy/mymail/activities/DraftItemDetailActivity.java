package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
 * 草稿Item信息
 */
public class DraftItemDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvSubject;
    private TextView tvSender;
    private TextView tvRecipient;
    private TextView tvDate;
    private TextView tvContent;

    private String mSubject;
    private String mAddress;
    private String mDate;
    private String mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draft_item_detail);

        initView();
        initData();
    }

    private void initView() {
        tvSubject = findViewById(R.id.tv_subject_draft_detail);
        tvSender = findViewById(R.id.tv_sender_draft_detail);
        tvRecipient = findViewById(R.id.tv_recipient_draft_detail);
        tvDate = findViewById(R.id.tv_date_draft_detail);
        tvContent = findViewById(R.id.tv_content_draft_detail);
        findViewById(R.id.iv_return_draft_detail).setOnClickListener(this);
        findViewById(R.id.iv_delete_draft_detail).setOnClickListener(this);
        findViewById(R.id.iv_write_email_draft_detail).setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            mAddress = getIntent().getStringExtra("address");
            mSubject = getIntent().getStringExtra("subject");
            mDate = getIntent().getStringExtra("date");
            mContent = getIntent().getStringExtra("content");

            tvRecipient.setText(mAddress);
            tvSubject.setText(mSubject);
            tvDate.setText(mDate);
            tvContent.setText(mContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_draft_detail:
                finish();
                break;
            case R.id.iv_delete_draft_detail:
                showDeletePopup(mAddress);
                break;
            case R.id.iv_write_email_draft_detail:
                Intent intent = new Intent(this, WriteEmailActivity.class);
                intent.putExtra("recipient", mAddress);
                intent.putExtra("subject", mSubject);
                intent.putExtra("content", mContent);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示删除的弹出框
     */
    private void showDeletePopup(String address) {

        new XPopup.Builder(this)
                .asConfirm("是否选择删除", "删除后此草稿的信息将彻底消失",
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
        query.addWhereEqualTo("recipientAddress", address);
        query.findObjects(new FindListener<DraftItem>() {
            @Override
            public void done(List<DraftItem> object, BmobException e) {
                if (e == null) {
                    for (DraftItem draftItem : object) {
                        deleteDraftItem(loadingPopup, draftItem.getObjectId());
                        break;
                    }
                } else {
                    loadingPopup.dismiss();
                    Toast.makeText(DraftItemDetailActivity.this,
                            "查询失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DraftItemDetailActivity.this,
                            "删除成功", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(DraftItemDetailActivity.this,
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
