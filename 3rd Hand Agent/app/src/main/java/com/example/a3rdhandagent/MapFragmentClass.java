package com.example.a3rdhandagent;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;
//import com.google.android.libraries.places.api.model.Place;

public class MapFragmentClass extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    //    EditText inputSearch2;
    View v;
    float zoomLevel;
    ImageView imageView;
    NetworkInfo netInfo;
    LatLng DevicelatLng;
    ConnectivityManager cm;
    Location currentLocation;
    boolean connected = false;
    Double agentLat, agentLng;
    String locationArrayString[];
    private GoogleMap mGoogleMap;
    AutoCompleteTextView inputSearch;
    String searchString, agentPhoneNumber;
    SupportMapFragment supportMapFragment;
    DatabaseReference currentAgentLocationRef;
    private static final int REQUEST_CODE = 101;
    private boolean locationPermissionGranted = false;
    private static final String TAG = "FindLotFragment";
    FusedLocationProviderClient mfusedLocationProviderClient;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        imageView = v.findViewById(R.id.getDeviceID);
        imageView.setOnClickListener(this);

//        inputSearch2 = v.findViewById(R.id.searchMapID2);

        inputSearch = v.findViewById(R.id.searchMapID);
        locationArrayString = getResources().getStringArray(R.array.location_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, locationArrayString);
        inputSearch.setThreshold(1);
        inputSearch.setAdapter(adapter);

        currentAgentLocationRef = FirebaseDatabase.getInstance().getReference("Agent Current Location");

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        zoomLevel = 16f;

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
        mGoogleMap.setMapStyle(mapStyleOptions);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng dhakalatLng = new LatLng(23.8103, 90.4125);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dhakalatLng, 10f));
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

// AutoCompletePlacesTextView fragment code is below
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

//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }

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
                            Log.d(TAG, "moveCamera: move camera to: lat: " + DevicelatLng.latitude
                                    + ", lng: " + DevicelatLng.longitude);
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
                            agentLat = currentLocation.getLatitude();
                            agentLng = currentLocation.getLongitude();

                            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                            netInfo = cm.getActiveNetworkInfo();
                            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                                connected = true;
                                FirebaseUser agent = FirebaseAuth.getInstance().getCurrentUser();
                                if (agent != null) {
                                    if (agent.getDisplayName() != null) {
                                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Agent Information")
                                                .child(agent.getDisplayName()).child("phone");
                                        ref1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                agentPhoneNumber = dataSnapshot.getValue(String.class);
                                                storeAgentCurrentLocation(agentPhoneNumber, agentLat.toString(), agentLng.toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                                        });
                                    }
                                }
                            } else {
                                connected = false;
                                Snackbar snackbar = Snackbar.make(v, "Turn on internet connection", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                                snackbar.setDuration(5000).show();
                            }

                        } else {
                            Snackbar snackbar = Snackbar.make(v, "Connection lost: Restart you app", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                            snackbar.setDuration(5000).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());}
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
    }

    public void storeAgentCurrentLocation(String phone, String lattitude, String longitude){
        String Key_User_Info = phone;
        StoreAgentSelfCurrentLatLng storeAgentSelfCurrentLatLng = new StoreAgentSelfCurrentLatLng(phone, lattitude, longitude);
        currentAgentLocationRef.child(Key_User_Info).setValue(storeAgentSelfCurrentLatLng);
    }
}
