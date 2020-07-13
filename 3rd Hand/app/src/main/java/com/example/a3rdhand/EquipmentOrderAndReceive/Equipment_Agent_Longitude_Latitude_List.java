package com.example.a3rdhand.EquipmentOrderAndReceive;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Equipment_Agent_Longitude_Latitude_List {

    ArrayList<LatLng> placelist = new ArrayList<LatLng>();
    LatLng dhanmondi = new LatLng(23.755174, 90.376366);
    LatLng mugdaHospital = new LatLng(23.732418, 90.430201);
    LatLng mugdaBTCL = new LatLng(23.730633, 90.428779);

    ArrayList<String> title = new ArrayList<String>();
    String dhanmondi_shukrabaad = "Abdul Kayeum";
    String mugdaparaHospital = "Himel Junaed";
    String mugdaparaBTCLoffice = "Redowan Islam Palash";

    public Equipment_Agent_Longitude_Latitude_List() {
        placelist.add(dhanmondi);
        placelist.add(mugdaHospital);
        placelist.add(mugdaBTCL);

        title.add(dhanmondi_shukrabaad);
        title.add(mugdaparaHospital);
        title.add(mugdaparaBTCLoffice);
    }

    public ArrayList<LatLng> getPlacelist() {
        return placelist;
    }

    public ArrayList<String> getTitle(){
        return title;
    }

}
