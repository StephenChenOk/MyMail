package com.chen.fy.mymail.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.utils.UiUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        UiUtils.changeStatusBarTextImgColor(this,true);
        initView();
        applyPermission();
        Bmob.initialize(this,"13b8b672c0e0fb9a00941c5694375ba4");
    }

    private void initView() {
        findViewById(R.id.iv_add).setOnClickListener(this);
        findViewById(R.id.ll_receive_box).setOnClickListener(this);
        findViewById(R.id.ll_draft_box).setOnClickListener(this);
        findViewById(R.id.ll_sent_box).setOnClickListener(this);
        findViewById(R.id.ll_address_box).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                showAddPopup(v);
                break;
            case R.id.ll_receive_box:
                Intent intent1 = new Intent(this, InboxActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_draft_box:
                Intent intent2 = new Intent(this, DraftBoxActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_sent_box:
                Intent intent3 = new Intent(this, SentActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_address_box:
                Intent intent4 = new Intent(this, AddressBookActivity.class);
                startActivity(intent4);
                break;
        }
    }

    private void showAddPopup(View view) {
        new XPopup.Builder(this)
                .hasShadowBg(false)
                .isCenterHorizontal(true) //是否与目标水平居中对齐
                .offsetX(80)
                .offsetY(-20)
                .atView(view)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"写邮件", "设置"},
                        new int[]{R.drawable.ic_assignment_black_16dp, R.drawable.ic_settings_black_16dp},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (position) {
                                    case 0:
                                        Intent intent = new Intent(MainActivity.this,
                                                WriteEmailActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        Toast.makeText(MainActivity.this, "敬请期待。。。"
                                                , Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        })
                .show();
    }


    /**
     * 动态申请危险权限
     */
    private void applyPermission() {
        //权限集合
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
