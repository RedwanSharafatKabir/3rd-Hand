package com.example.a3rdhandagent.ModelClass;

public class StoreCurrentOnlineAgentsInfo {
    String phone, username, employeeid, avatar;

    public StoreCurrentOnlineAgentsInfo() {}

    public StoreCurrentOnlineAgentsInfo(String phone, String username, String employeeid, String avatar) {
        this.phone = phone;
        this.username = username;
        this.employeeid = employeeid;
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmployeeid() {
        return employeeid;
    }

    public void setEmployeeid(String employeeid) {
        this.employeeid = employeeid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
