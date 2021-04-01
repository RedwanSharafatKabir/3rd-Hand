package com.example.a3rdhand.ModelClass;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Common {
    public static Set<AgentGeoModel> agentsFound = new HashSet<>();
    public static HashMap<String, Marker> markerList = new HashMap<>();
    public static HashMap<String, AnimationModel> agentLocationSubscribe = new HashMap<String, AnimationModel>();
    private static final String BaseUrl = "https://maps.googleapis.com/";
    
//    public static IGoogleApi getGoogleAPI(){
//        return RetrofitClient.getInstance(BaseUrl).create(IGoogleApi.class);
//    }

    public static String buildName(String username, String employeeid) {
        return new StringBuilder(username).append(" \nID: ").append(employeeid).toString();
    }

    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat=0, lng=0;
        while(index < len){
            int b, shift=0, result=0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            } while(b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~ (result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            } while(b >= 0x20);

            int dlng = ((result & 1)!=0 ? ~ (result >> 1): (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double)lat / 1E5)), (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);

        return -1;
    }
}
