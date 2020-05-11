package com.chen.fy.mymail.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.ContactPerson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 添加联系人
 */
public class AddPersonActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etAddress;

    private boolean isAdd = false;

    private BasePopupView loadingPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_person);

        initView();
        initData();
    }

    private void initView() {
        etName = findViewById(R.id.et_name_add_person);
        etAddress = findViewById(R.id.et_adress_add_person);

        findViewById(R.id.iv_return_add_person).setOnClickListener(this);
        findViewById(R.id.tv_complete).setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_add_person:
                finish();
                break;
            case R.id.tv_complete:
                if (!isAdd) {
                    checkRepeat(etAddress.getText().toString());
                }
                break;
        }
    }

    /**
     * 检查是否已经有此地址
     */
    private void checkRepeat(String address) {
        loadingPopup = new XPopup.Builder(this)
                .asLoading("正在添加联系人...")
                .show();
        loadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        BmobQuery<ContactPerson> query = new BmobQuery<>();
        query.addWhereEqualTo("address", address);
        query.findObjects(new FindListener<ContactPerson>() {
            @Override
            public void done(List<ContactPerson> object, BmobException e) {
                loadingPopup.dismiss();
                if (object.isEmpty()) {    //没有找到
                    isAdd = true;
                    addPerson();
                    Log.d("chenyisheng","没有找到");
                }else{
                    isAdd = false;
                    Toast.makeText(AddPersonActivity.this,"添加联系人失败，已保存有此邮箱地址"
                    ,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 添加联系人
     */
    private void addPerson() {

        String name = etName.getText().toString();
        String address = etAddress.getText().toString();

        ContactPerson contactPerson = new ContactPerson(name, address);
        contactPerson.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(AddPersonActivity.this,
                            "添加联系人成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddPersonActivity.this,
                            "添加联系人失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
