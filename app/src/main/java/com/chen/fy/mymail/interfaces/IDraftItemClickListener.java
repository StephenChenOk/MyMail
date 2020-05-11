package com.chen.fy.mymail.interfaces;

public interface IDraftItemClickListener {

    void onClickDraftItem(String subject,String address,String date,String content);

    void onLongClickDraftItem(String address);

}
