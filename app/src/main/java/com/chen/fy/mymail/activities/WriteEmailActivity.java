package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;
import com.chen.fy.mymail.asyncTasks.SendAsyncTask;
import com.chen.fy.mymail.beans.DraftItem;
import com.chen.fy.mymail.beans.SentItem;
import com.chen.fy.mymail.utils.FileUtils;
import com.chen.fy.mymail.utils.GlideEngine;
import com.chen.fy.mymail.utils.UiUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 写邮件
 */
public class WriteEmailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SEND_SUCCESS_CODE = 1;
    private static final String TAG = "WriteEmailLog";
    private static final int REQUEST_CODE_CHOOSE = 7;
    private boolean isSend = false;

    private EditText etRecipient;
    private EditText etSubject;
    private EditText etContent;

    private ViewStub vsAddAttachment;
    private ImageView ivAttachment;

    private Uri mAttachmentUri;

    private boolean isAttachmentInit = false;
    private boolean isShow = false;

    private BasePopupView mSendPopup;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SUCCESS_CODE:
                    mSendPopup.dismiss();
                    Toast.makeText(WriteEmailActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
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
        findViewById(R.id.iv_add_attachment).setOnClickListener(this);

        etRecipient = findViewById(R.id.et_input_recipient);
        etSubject = findViewById(R.id.et_input_subject);
        etContent = findViewById(R.id.et_input_content);
        vsAddAttachment = findViewById(R.id.vs_add_attachment);
    }

    private void initData() {
        if (getIntent() != null) {
            int type = getIntent().getIntExtra("type", -1);
            switch (type) {
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
                        sendEmail(recipientAddress, subject, content);
                    } else {
                        Toast.makeText(this,
                                "请填写完整各处信息", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_add_attachment: //添加附件
                if (!isAttachmentInit) {
                    openCamera();
                } else {
                    showAttachment();    //判断是否显示添加附件界面
                }
                isShow = !isShow;
                break;
        }
    }

    //初始化添加附件ViewStub
    private void initViewStub(Intent data) {
        vsAddAttachment.inflate();
        ivAttachment = findViewById(R.id.iv_add_attachment_vs);

        List<Uri> mSelected = Matisse.obtainResult(data);
        mAttachmentUri = mSelected.get(0);
        Glide.with(this).load(mAttachmentUri).into(ivAttachment);
    }

    //发送邮件
    private void sendEmail(String address, String subject, String content) {
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
        if (mAttachmentUri != null) {  //带附件
            sendAsyncTask.execute(address, subject, content
                    , FileUtils.getRealPathFromURI(this, mAttachmentUri));
        } else {              //不带附件
            sendAsyncTask.execute(address, subject, content
                    , "");
        }
    }

    //点击左上角取消按钮弹窗
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

    //判断是否输入完整，保存草稿的前提
    private void saveDraft() {
        String recipientAddress = etRecipient.getText().toString();
        String subject = etSubject.getText().toString();
        String content = etContent.getText().toString();
        if (!recipientAddress.isEmpty() && !subject.isEmpty() && !content.isEmpty()) {
            saveDraftItem();
        } else {
            Toast.makeText(this, "请填写完整各处信息", Toast.LENGTH_SHORT).show();
        }

    }

    //保存草稿
    private void saveDraftItem() {
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
        insertObject(sentItem, loadingPopup);
//        if (mAttachmentUri != null) {
//            uploadFile(sentItem, loadingPopup);
//        } else {
//            insertObject(sentItem, loadingPopup);
//        }
    }

    //上传图片到Bmob
    private void uploadFile(SentItem sentItem, BasePopupView loadingPopup) {
        File file = new File(FileUtils.getRealPathFromURI(this, mAttachmentUri));
        BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    sentItem.setFile(bmobFile);
                    insertObject(sentItem, loadingPopup);
                } else {
                    e.printStackTrace();
                    Log.i(TAG, "文件上传失败: " + e.toString());
                }
            }
        });
    }

    //往bmob数据表中插入数据
    private void insertObject(SentItem sentItem, final BasePopupView loadingPopup) {

        sentItem.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(WriteEmailActivity.this,
                            "上传成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.i(TAG, "上传失败：" + e.getMessage());
                }
            }
        });
    }

    //打开相册，且在相册中可以进行拍照选项
    private void openCamera() {

        //获取外部存储位置的uri
        File file = new File(getExternalFilesDir(null), "Attachment.jpg");
        Uri uri = Uri.fromFile(file);
        String imagePath = uri.getPath();

        Matisse.from(this)
                .choose(MimeType.ofAll()) // 选择 mime 的类型
                .countable(true) // 显示选择的数量
                .capture(true)  // 开启相机，和 captureStrategy 一并使用否则报错
                .captureStrategy(new CaptureStrategy(true, imagePath)) // 拍照的图片路径
                .theme(R.style.Matisse_Dracula) // 黑色背景
                .maxSelectable(1) // 图片选择的最多数量
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size)) // 列表中显示的图片大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f) // 缩略图的比例
                .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                .forResult(REQUEST_CODE_CHOOSE); // 设置作为标记的请求码，返回图片时使用
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_OK) {
                    isAttachmentInit = true;
                    initViewStub(data);
                }
                break;
        }
    }

    //判断是否需要显示添加附件界面
    private void showAttachment() {
        if (isShow) {
            vsAddAttachment.setVisibility(View.GONE);
        } else {
            vsAddAttachment.setVisibility(View.VISIBLE);
        }
    }
}

