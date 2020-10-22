package com.example.a3rdhand.ModelClass;

public class StoreUserData {
    String email, username, phone, country, nid;

    public StoreUserData() {

    }

    public StoreUserData(String email, String username, String phone, String country, String nid) {
        this.email = email;
        this.username = username;
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
