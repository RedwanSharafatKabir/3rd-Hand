package com.example.a3rdhandagent;

public class StoreAgentData {
    String email, username, employeeid, phone, country, nid;

    public StoreAgentData() {

    }

    public StoreAgentData(String email, String username, String employeeid, String phone, String country, String nid) {
        this.email = email;
        this.username = username;
        this.employeeid = employeeid;
        this.phone = phone;
        this.country = country;
        this.nid = nid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getnid() {
        return nid;
    }

    public void setnid(String nid) {
        this.nid = nid;
    }
}
