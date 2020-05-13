package com.chen.fy.mymail.beans;


import java.io.File;
import java.util.Date;

public class InboxItem {
    private int headIcon;
    private String name;
    private String subject;
    private String content;
    private Date date;
    private File file;

    public InboxItem(int headIcon, String name, String subject
            , String content, Date date) {
        this.headIcon = headIcon;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.date = date;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "InboxItem{" +
                "headIcon=" + headIcon +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", file=" + file +
                '}';
    }
}
