package com.example.a3rdhand.ModelClass;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Common {
    public static Set<AgentGeoModel> agentsFound = new HashSet<AgentGeoModel>();
    public static HashMap<String, Marker> markerList = new HashMap<>();

    public static String buildName(String username, String employeeid) {
        return new StringBuilder(username).append(" \nID: ").append(employeeid).toString();
    }
}
