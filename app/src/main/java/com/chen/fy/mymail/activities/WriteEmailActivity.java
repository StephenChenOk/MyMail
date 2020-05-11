package com.chen.fy.mymail.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.asyncTasks.SendAsyncTask;
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.beans.SentItem;
import com.chen.fy.mymail.utils.UiUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 写邮件
 */
public class WriteEmailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SEND_SUCCESS_CODE = 1;
    private static final String TAG = "WriteEmailLog";
    private boolean isSend = false;

    private EditText etRecipient;
    private EditText etSubject;
    private EditText etContent;

    private BasePopupView mSendPopup;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SUCCESS_CODE:
                    mSendPopup.dismiss();
                    Toast.makeText(WriteEmailActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    saveSent();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_email_layout);
        UiUtils.changeStatusBarTextImgColor(this, false);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.tv_cancel_write_email).setOnClickListener(this);
        findViewById(R.id.iv_send).setOnClickListener(this);
        etRecipient = findViewById(R.id.et_input_recipient);
        etSubject = findViewById(R.id.et_input_subject);
        etContent = findViewById(R.id.et_input_content);
    }

    private void initData() {
        if (getIntent() != null) {
            int type = getIntent().getIntExtra("type",-1);
            switch (type){
                case 1:     //从联系人界面跳转过来
                    String address = getIntent().getStringExtra("recipient");
                    etRecipient.setText(address);
                    break;
                case 2:     //从草稿箱界面跳转过来
                    String address_ = getIntent().getStringExtra("recipient");
                    String subject = getIntent().getStringExtra("subject");
                    String content = getIntent().getStringExtra("content");
                    etRecipient.setText(address_);
                    etSubject.setText(subject);
                    etContent.setText(content);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_write_email:
                showCancelPopup();
                break;
            case R.id.iv_send:
                if (!isSend) {
                    String recipientAddress = etRecipient.getText().toString();
                    String subject = etSubject.getText().toString();
                    String content = etContent.getText().toString();
                    if (!recipientAddress.isEmpty() && !subject.isEmpty() && !content.isEmpty()) {
                        sendEmail(recipientAddress,subject,content);
                    } else {
                        Toast.makeText(this,
                                "请填写完整各处信息", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void sendEmail(String address,String subject,String content) {
        isSend = true;
        mSendPopup = new XPopup.Builder(this)
                .asLoading("正在发送中...")
                .show();
        mSendPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        SendAsyncTask sendAsyncTask = new SendAsyncTask();
        sendAsyncTask.setHandler(handler);
        sendAsyncTask.execute(address, subject, content);
    }

    public void showCancelPopup() {
        new XPopup.Builder(this)
                .asConfirm("离开写邮件",
                        "已填写的邮件内容将丢失，或保存至草稿箱",
                        "取消", "保存草稿",
                        this::saveDraft,
                        this::finish,
                        false)
                .bindLayout(R.layout.leave_popup)
                .show();
    }

    //保存草稿
    private void saveDraft() {

        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("保存草稿中")
                .show();
        loadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        String recipient = etRecipient.getText().toString();
        String subject = etSubject.getText().toString();
        String content = etContent.getText().toString();

        DraftItem draftItem = new DraftItem(recipient, subject, content);
        draftItem.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(WriteEmailActivity.this,
                            "保存草稿成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(WriteEmailActivity.this,
                            "保存草稿失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //保存已发送
    private void saveSent() {

        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("保存中...")
                .show();
        loadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        String recipient = etRecipient.getText().toString();
        String subject = etSubject.getText().toString();
        String content = etContent.getText().toString();

        SentItem sentItem = new SentItem(recipient, subject, content);
        sentItem.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    loadingPopup.dismiss();
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
