package com.chen.fy.mymail.interfaces;

import com.chen.fy.mymail.beans.InboxItem;

import java.util.List;

public interface IReceiveAsyncResponse {

    void onDataReceivedSuccess(List<InboxItem> listData);
    void onDataReceivedFailed();

}
