package com.example.a3rdhandagent.ModelClass;

public class StoreAgentSelfCurrentLatLng {
    String phone, username, lattitude, longitude, locationName;

    public StoreAgentSelfCurrentLatLng() {}

    public StoreAgentSelfCurrentLatLng(String phone, String username, String lattitude, String longitude, String locationName) {
        this.phone = phone;
        this.username = username;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.locationName = locationName;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
