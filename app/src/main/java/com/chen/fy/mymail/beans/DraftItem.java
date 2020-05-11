package com.chen.fy.mymail.beans;

import cn.bmob.v3.BmobObject;

/**
 * 草稿Item
 */
public class DraftItem extends BmobObject {

    private String recipientAddress;
    private String subject;
    private String content;

    public DraftItem(String recipientAddress, String subject, String content) {
        this.recipientAddress = recipientAddress;
        this.subject = subject;
        this.content = content;
    }

    public DraftItem(){}

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

    @Override
    public String toString() {
        return "DraftItem{" +
                "recipientAddress='" + recipientAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
