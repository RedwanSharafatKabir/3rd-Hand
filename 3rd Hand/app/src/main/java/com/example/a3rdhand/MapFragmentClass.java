package com.example.a3rdhand;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.EquipmentOrderAndReceive.Equipment_Agent_Longitude_Latitude_List;
import com.example.a3rdhand.EquipmentOrderAndReceive.LeftEquipmentSavedRecord;
import com.example.a3rdhand.EquipmentOrderAndReceive.Call_Package_Agent_Dialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapFragmentClass extends Fragment implements
        OnMapReadyCallback, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    //    EditText inputSearch2;
    AutoCompleteTextView inputSearch;
    Button findAgent;
    ImageView imageView;
    float zoomLevel;
    String location_Thing, userPhoneNumber, tempPackage, searchString;
    String locationArrayString[];
    int j = 0, i = 0;
    private GoogleMap mGoogleMap;
    private static final String TAG = "FindLotFragment";
    private boolean locationPermissionGranted = false;
    FusedLocationProviderClient mfusedLocationProviderClient;
    LatLng DevicelatLng;
    private Equipment_Agent_Longitude_Latitude_List equipmentAgentLongitude_latitude_list;
    ArrayList<LatLng> placelist;
    ArrayList<String> title;
    BottomNavigationView bottomNavigation;
    View v;
    DatabaseReference databaseReference;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;

    public MapFragmentClass() {
        equipmentAgentLongitude_latitude_list = new Equipment_Agent_Longitude_Latitude_List();
        placelist = equipmentAgentLongitude_latitude_list.getPlacelist();
        title = equipmentAgentLongitude_latitude_list.getTitle();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        imageView = v.findViewById(R.id.getDeviceID);
        imageView.setOnClickListener(this);
        findAgent = v.findViewById(R.id.findPackageServiceAgentID);
        findAgent.setOnClickListener(this);
        findAgent.setVisibility(v.GONE);

//        inputSearch2 = v.findViewById(R.id.searchMapID2);

        inputSearch = v.findViewById(R.id.searchMapID);
        locationArrayString = getResources().getStringArray(R.array.location_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, locationArrayString);
        inputSearch.setThreshold(1);
        inputSearch.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Left Equipment List Record of All Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhoneNumber = dataSnapshot.getValue(String.class);
                        DatabaseReference ref2 = databaseReference.child(userPhoneNumber).child("locationThing");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                location_Thing = dataSnapshot.getValue(String.class);
                                try {
                                    if (!location_Thing.isEmpty()) {
                                        findAgent.setVisibility(v.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    findAgent.setVisibility(v.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }

        bottomNavigation = v.findViewById(R.id.bottomNavigationID);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().setGroupCheckable(0, false, true);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        zoomLevel = 16f;

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
        mGoogleMap.setMapStyle(mapStyleOptions);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        for (i = 0; i < placelist.size(); i++) {
            if (j == i) {
                mGoogleMap.addMarker(new MarkerOptions().position(placelist.get(i)).title(String.valueOf(title.get(j)))
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_agent_location_on_24)));
            }
            j++;
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelist.get(i), 10f));
        }

        setupAutoCompleteFragment();

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationPermissionGranted = true;
                mGoogleMap.setMyLocationEnabled(true);
                getDeviceLocation();
                init();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);

                locationPermissionGranted = true;
                mGoogleMap.setMyLocationEnabled(true);
                getDeviceLocation();
                init();
            }
        } catch(Exception e){getDeviceLocation();}

    }

/*  searchMapMethod() Method for autocomplete editText is below
    public void searchMapMethod(){
        inputSearch2.setFocusable(false);
        inputSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(getActivity());
                startActivityForResult(intent, 100);
            }
        });
    }
*/

/*  onActivityResult() Method is below
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && requestCode==RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            inputSearch2.setText(place.getAddress());
            searchString = place.getAddress();
            setupAutoCompleteFragment(searchString);
        }
    }
*/

    private void setupAutoCompleteFragment() {
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragmentID);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Geocoder geocoder = new Geocoder(getActivity());
//                List<Address> list = new ArrayList<>();
//                try {
//                    list = geocoder.getFromLocationName(place.getAddress().toString(), 1);
//                } catch (IOException e) {
//                    Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
//                }
//                if (list.size() > 0) {
//                    Address address = list.get(0);
//                    Log.d(TAG, "geoLocate: found a location" + address.toString());
//
//                    LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(SearchlatLng));
//                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
//                }
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.e("Error", status.getStatusMessage());
//            }
//        });

//  geoCoder for searchMapMethod() Method

//        Geocoder geocoder = new Geocoder(getActivity());
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
//        }
//        if (list.size() > 0) {
//            Address address = list.get(0);
//            Log.d(TAG, "geoLocate: found a location" + address.toString());
//
//            LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(SearchlatLng));
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
//        }
    }

    private void init() {
        Log.d(TAG, "init: initialization");
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geoLocating");
        inputSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchString = inputSearch.getText().toString();
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(searchString, 1);
                } catch (IOException e) {
                    Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
                }
                if (list.size() > 0) {
                    Address address = list.get(0);
                    Log.d(TAG, "geoLocate: found a location" + address.toString());

                    LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
                }
            }
        });
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get current device location");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if(locationPermissionGranted) {
                Task task = mfusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(@NonNull Location location) {
                        if (location != null) {
                            Log.d(TAG, "onComplete: location found");
                            currentLocation = location;

                            DevicelatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d(TAG, "moveCamera: move camera to: lat: " + DevicelatLng.latitude + ", lng: " + DevicelatLng.longitude);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
                            getCustomerPackageLocation();
                        } else {
                            Snackbar snackbar = Snackbar.make(v, "Connection lost: Restart you app", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                            snackbar.setDuration(2000).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            locationPermissionGranted = false;
                        }
                    }
                    locationPermissionGranted = true;
                    getDeviceLocation();
                    init();
                }
                break;
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int VectorID) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, VectorID);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.getDeviceID) {
            getDeviceLocation();
        }

        if(v.getId()==R.id.findPackageServiceAgentID){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() != null) {
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                            .child(user.getDisplayName()).child("phone");
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userPhoneNumber = dataSnapshot.getValue(String.class);
                            DatabaseReference ref2 = databaseReference.child(userPhoneNumber).child("locationThing");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    location_Thing = dataSnapshot.getValue(String.class);
                                    try{
                                        if(!location_Thing.isEmpty()) {
                                            Geocoder geocoder1 = new Geocoder(getActivity());
                                            List<Address> list1 = new ArrayList<>();
                                            try {
                                                list1 = geocoder1.getFromLocationName(location_Thing, 1);
                                            } catch (IOException e) {
                                                Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
                                            }
                                            if (list1.size() > 0) {
                                                Address address = list1.get(0);
                                                Log.d(TAG, "geoLocate: found a location" + address.toString());

                                                LatLng SearchlatLng1 = new LatLng(address.getLatitude(), address.getLongitude());
                                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng1, zoomLevel));
                                            }
                                        }}catch (Exception e){findAgent.setVisibility(v.GONE);}
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
        }
    }

    public void getCustomerPackageLocation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhoneNumber = dataSnapshot.getValue(String.class);
                        DatabaseReference ref3 = FirebaseDatabase.getInstance()
                                .getReference("Left Equipment List Record of All Users")
                                .child(userPhoneNumber).child("locationThing");
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                location_Thing = dataSnapshot.getValue(String.class);
                                try {
                                    if (!location_Thing.isEmpty()) {
                                        Geocoder geocoder = new Geocoder(getActivity());
                                        List<Address> list = new ArrayList<>();
                                        try {
                                            list = geocoder.getFromLocationName(location_Thing, 1);
                                        } catch (IOException e) {
                                            Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
                                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                        }
                                        if (list.size() > 0) {
                                            Address address = list.get(0);
                                            Log.d(TAG, "geoLocate: found a location" + address.toString());
                                            LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                            tempPackage = "Customer's package is somewhere in this area. " +
                                                    "Please find out customer's specified address around here.";
                                            mGoogleMap.addMarker(new MarkerOptions().position(SearchlatLng)
                                                    .title(tempPackage).icon(bitmapDescriptorFromVector(getActivity(),
                                                            R.drawable.package_location_two)));
                                        }

                                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                String markertitle = marker.getTitle();
                                                try {
                                                    if (markertitle.equals(tempPackage)) {
                                                        LeftEquipmentSavedRecord leftEquipmentSavedRecord = new LeftEquipmentSavedRecord();
                                                        leftEquipmentSavedRecord.show(getFragmentManager(), "Sample dialog");
                                                    }
                                                    else if(!markertitle.equals(tempPackage)){
                                                        Bundle args = new Bundle();
                                                        args.putString("markertitle_key", markertitle);
                                                        Call_Package_Agent_Dialog call_package_agent_dialog = new Call_Package_Agent_Dialog();
                                                        call_package_agent_dialog.setArguments(args);
                                                        call_package_agent_dialog.show(getFragmentManager(), "Custom Sheet");
                                                    }
                                                } catch (Exception e) {}
                                                return false;
                                            }
                                        });
                                    }
                                }catch(Exception e){
                                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            try {
                                                Toast t = Toast.makeText(getActivity(), R.string.not_available, Toast.LENGTH_SHORT);
                                                t.setGravity(Gravity.CENTER, 0,0);
                                                t.show();
                                            } catch (Exception e) {}
                                            return false;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch(id){
            case R.id.profileID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                ProfileActivity profileActivity = new ProfileActivity();
                profileActivity.show(getFragmentManager(), "Sample dialog");
                return true;

            case R.id.helpID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                HelpActivity helpActivity = new HelpActivity();
                helpActivity.show(getFragmentManager(), "Sample dialog");
                return true;

            case R.id.aboutID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                AboutActivity aboutActivity = new AboutActivity();
                aboutActivity.show(getFragmentManager(), "Sample dialog");
                return true;
        }

        return false;
    }
}
