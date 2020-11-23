package com.example.a3rdhand.ModelClass;

import com.firebase.geofire.GeoLocation;

public class AgentGeoModel {
    private String key;
    private GeoLocation geoLocation;
    private AgentInfoModel agentInfoModel;

    public AgentGeoModel() {}

    public AgentGeoModel(String key, GeoLocation geoLocation) {
        this.key = key;
        this.geoLocation = geoLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public AgentInfoModel getAgentInfoModel() {
        return agentInfoModel;
    }

    public void setAgentInfoModel(AgentInfoModel agentInfoModel) {
        this.agentInfoModel = agentInfoModel;
    }
}
