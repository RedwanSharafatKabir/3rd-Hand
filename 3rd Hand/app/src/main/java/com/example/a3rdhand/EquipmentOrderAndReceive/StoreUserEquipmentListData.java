package com.example.a3rdhand.EquipmentOrderAndReceive;

import androidx.annotation.VisibleForTesting;

public class StoreUserEquipmentListData {
    String userPhoneNumber, username, equipment_name_string,
            equipment_type_string, lane_string, building_string,
            floor_string, flat_string, locationThing;

    public StoreUserEquipmentListData() {}

    public StoreUserEquipmentListData(String userPhoneNumber, String username, String equipment_name_string,
                                      String equipment_type_string, String lane_string, String building_string,
                                      String floor_string, String flat_string, String locationThing) {
        this.userPhoneNumber = userPhoneNumber;
        this.username = username;
        this.equipment_name_string = equipment_name_string;
        this.equipment_type_string = equipment_type_string;
        this.lane_string = lane_string;
        this.building_string = building_string;
        this.floor_string = floor_string;
        this.flat_string = flat_string;
        this.locationThing = locationThing;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEquipment_name_string() {
        return equipment_name_string;
    }

    public void setEquipment_name_string(String equipment_name_string) {
        this.equipment_name_string = equipment_name_string;
    }

    public String getEquipment_type_string() {
        return equipment_type_string;
    }

    public void setEquipment_type_string(String equipment_type_string) {
        this.equipment_type_string = equipment_type_string;
    }

    public String getLane_string() {
        return lane_string;
    }

    public void setLane_string(String lane_string) {
        this.lane_string = lane_string;
    }

    public String getBuilding_string() {
        return building_string;
    }

    public void setBuilding_string(String building_string) {
        this.building_string = building_string;
    }

    public String getFloor_string() {
        return floor_string;
    }

    public void setFloor_string(String floor_string) {
        this.floor_string = floor_string;
    }

    public String getFlat_string() {
        return flat_string;
    }

    public void setFlat_string(String flat_string) {
        this.flat_string = flat_string;
    }

    public String getLocationThing() {
        return locationThing;
    }

    public void setLocationThing(String locationThing) {
        this.locationThing = locationThing;
    }
}
