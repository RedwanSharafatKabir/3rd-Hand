package com.example.a3rdhand.ModelClass;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Equipment_Agent_Longitude_Latitude_List {

    ArrayList<LatLng> placelist = new ArrayList<LatLng>();
    LatLng mugdaHospital = new LatLng(23.732418, 90.430201);
    LatLng mugdaBTCL = new LatLng(23.730633, 90.428779);
    LatLng dhanmondi = new LatLng(23.755174, 90.376366);

    ArrayList<String> title = new ArrayList<String>();
    String mugdaparaHospital = "Himel Junaed";
    String mugdaparaBTCLoffice = "Redowan Islam Palash";
    String dhanmondi_shukrabaad = "Abdullah Al Mamun";

    public Equipment_Agent_Longitude_Latitude_List() {
        placelist.add(mugdaHospital);
        placelist.add(mugdaBTCL);
        placelist.add(dhanmondi);

        title.add(mugdaparaHospital);
        title.add(mugdaparaBTCLoffice);
        title.add(dhanmondi_shukrabaad);
    }

    public ArrayList<LatLng> getPlacelist() {
        return placelist;
    }

    public ArrayList<String> getTitle(){
        return title;
    }
}
