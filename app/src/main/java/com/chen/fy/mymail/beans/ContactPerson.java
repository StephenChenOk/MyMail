package com.chen.fy.mymail.beans;

import cn.bmob.v3.BmobObject;

/**
 * 草稿Item
 */
public class ContactPerson extends BmobObject {

    private String name;
    private String address;

    public ContactPerson(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public ContactPerson(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ContactPerson{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
