package com.chen.fy.mymail.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 已发送Item
 */
public class SentItem extends BmobObject {

    private String recipientAddress;
    private String subject;
    private String content;
    private BmobFile file;     //附件

    public SentItem(String recipientAddress, String subject, String content) {
        this.recipientAddress = recipientAddress;
        this.subject = subject;
        this.content = content;
    }

    public SentItem() {
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "SentItem{" +
                "recipientAddress='" + recipientAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", file=" + file +
                '}';
    }
}
