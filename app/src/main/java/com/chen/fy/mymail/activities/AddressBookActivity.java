package com.chen.fy.mymail.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.adapter.AddressBookAdapter;
import com.chen.fy.mymail.beans.ContactPerson;
import com.chen.fy.mymail.interfaces.IJumpActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 通讯录
 */
public class AddressBookActivity extends AppCompatActivity implements View.OnClickListener,
        IJumpActivity {

    public static final int ADD_PERSON_RESULT_CODE = 1;
    public static final int CONTACT_PERSON_ITEM_CODE = 2;

    private RecyclerView mRecyclerView;
    private AddressBookAdapter mAdapter;

    private List<ContactPerson> listData;

    private TextView tvNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_book_layout);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_return_address_box).setOnClickListener(this);
        findViewById(R.id.iv_person_add).setOnClickListener(this);
        mRecyclerView = findViewById(R.id.rv_address_book);
        tvNumber = findViewById(R.id.tv_number_address_book);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {

        listData = getData();

        mAdapter = new AddressBookAdapter(this, listData);
        mAdapter.setJumpActivity(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    //从Bmob中获取数据
    private List<ContactPerson> getData() {

        List<ContactPerson> items = new ArrayList<>();
        BmobQuery<ContactPerson> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<ContactPerson>() {
            @Override
            public void done(List<ContactPerson> object, BmobException e) {
                if (e == null) {
                    items.addAll(object);
                    //更新数据
                    mAdapter.notifyDataSetChanged();
                    tvNumber.setText(object.size() + "位联系人");
                }
            }
        });
        return items;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_address_box:
                finish();
                break;
            case R.id.iv_person_add:
                Intent intent = new Intent(
                        AddressBookActivity.this, AddPersonActivity.class);
                startActivityForResult(intent, ADD_PERSON_RESULT_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_PERSON_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    initData();
                }
                break;
            case CONTACT_PERSON_ITEM_CODE:
                if (resultCode == RESULT_OK) {
                    initData();
                }
                break;
        }
    }

    @Override
    public void jumpActivity(String name, String address) {
        Intent intent = new Intent(this, ContactPersonInfoActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        startActivityForResult(intent, CONTACT_PERSON_ITEM_CODE);
    }
}
