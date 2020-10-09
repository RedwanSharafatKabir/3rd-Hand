package com.example.a3rdhandagent;

public class StoreAgentSelfCurrentLatLng {
    String phone, lattitude, longitude;

    public StoreAgentSelfCurrentLatLng() {
    }

    public StoreAgentSelfCurrentLatLng(String phone, String lattitude, String longitude) {
        this.phone = phone;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
