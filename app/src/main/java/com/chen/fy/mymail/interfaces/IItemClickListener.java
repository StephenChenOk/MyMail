package com.chen.fy.mymail.interfaces;

/**
 * 收件箱、草稿箱的点击事件
 */
public interface IItemClickListener {

    void onClickItem(String subject,String address,String date,String content);

    void onLongClickItem(String address);

}
