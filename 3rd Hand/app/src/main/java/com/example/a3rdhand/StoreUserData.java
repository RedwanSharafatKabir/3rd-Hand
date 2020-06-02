package com.example.a3rdhand;

public class StoreUserData {
    String email, username, phone, country, password;

    public StoreUserData() {

    }

    public StoreUserData(String email, String username, String phone, String country, String password) {
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.country = country;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
