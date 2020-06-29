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

import com.bumptech.glide.Glide;
import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.ContactPerson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 联系人信息
 */
public class ContactPersonInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvName;
    private CircleImageView civHeadIcon;
    private TextView tvAddress;

    private String mAddress;
    private String mName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_person_info);

        initView();
        initData();
    }

    private void initView() {
        tvName = findViewById(R.id.tv_name_contact_person_info);
        civHeadIcon = findViewById(R.id.civ_head_icon_contact_person_info);
        tvAddress = findViewById(R.id.tv_address_contact_person_info);
        findViewById(R.id.iv_return_contact_person_info).setOnClickListener(this);
        findViewById(R.id.iv_delete_contact_person_info).setOnClickListener(this);
        findViewById(R.id.btn_write_email).setOnClickListener(this);
    }

    private void initData() {
        if (getIntent() != null) {
            mName = getIntent().getStringExtra("name");
            mAddress = getIntent().getStringExtra("address");
        }

        Glide.with(this).load(R.drawable.user_1).into(civHeadIcon);
        tvName.setText(mName);
        tvAddress.setText(mAddress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_contact_person_info:
                finish();
                break;
            case R.id.iv_delete_contact_person_info:
                showDeletePopup();
                break;
            case R.id.btn_write_email:
                Intent intent = new Intent(ContactPersonInfoActivity.this,
                        WriteEmailActivity.class);
                intent.putExtra("recipient", mAddress);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
        }
    }

    private void showDeletePopup() {

        new XPopup.Builder(this)
                .asConfirm("是否选择删除", "删除后此联系人的信息将彻底消失",
                        "取消", "确定",
                        this::delete,
                        null,
                        false)
                .bindLayout(R.layout.delete_contact_person_popup)
                .show();
    }

    private void delete() {
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
        BmobQuery<ContactPerson> query = new BmobQuery<>();
        query.addWhereEqualTo("address", mAddress);
        query.findObjects(new FindListener<ContactPerson>() {
            @Override
            public void done(List<ContactPerson> object, BmobException e) {
                if (e == null) {
                    for (ContactPerson contactPerson : object) {
                        deleteContactPerson(loadingPopup, contactPerson.getObjectId());
                        break;
                    }
                } else {
                    Log.d("chenyisheng", "查询失败");
                }
            }
        });
    }

    private void deleteContactPerson(BasePopupView loadingPopup, String objectId) {
        //删除联系人
        ContactPerson contactPerson = new ContactPerson();
        contactPerson.setObjectId(objectId);
        Log.d("chenyisheng", objectId);

        contactPerson.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(ContactPersonInfoActivity.this,
                            "删除成功", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ContactPersonInfoActivity.this,
                            "删除失败", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
