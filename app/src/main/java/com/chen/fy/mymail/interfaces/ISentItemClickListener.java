package com.chen.fy.mymail.interfaces;

import com.chen.fy.mymail.beans.SentItem;

public interface ISentItemClickListener {

    void onClickItem(SentItem sentItem);

    void onLongClickItem(String recipientAddress);

}
