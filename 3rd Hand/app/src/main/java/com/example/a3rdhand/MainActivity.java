package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    ImageView imageView;
    EditText inputSearch;
    float zoomLevel;
    String username;
    int j = 0, i = 0;
    private GoogleMap mGoogleMap;
    private static final String TAG = "FindLotFragment";
    private boolean locationPermissionGranted = true;
    FusedLocationProviderClient mfusedLocationProviderClient;

    LatLng DevicelatLng;
    ArrayList<LatLng> placelist = new ArrayList<LatLng>();
    LatLng dhanmondi = new LatLng(23.755174, 90.376366);
    LatLng mugdaHospital = new LatLng(23.732418, 90.430201);
    LatLng mugdaBTCL = new LatLng(23.730633, 90.428779);

    ArrayList<String> title = new ArrayList<String>();
    String dhanmondi_shukrabaad = "DIU Parking Lot";
    String mugdaparaHospital = "Mugda Hospital Parking Lot";
    String mugdaparaBTCLoffice = "Mugdapara BTCL Office";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        placelist.add(dhanmondi);
        placelist.add(mugdaHospital);
        placelist.add(mugdaBTCL);

        title.add(dhanmondi_shukrabaad);
        title.add(mugdaparaHospital);
        title.add(mugdaparaBTCLoffice);

        imageView = findViewById(R.id.getDeviceID);
        imageView.setOnClickListener(this);
        inputSearch = findViewById(R.id.searchMapID);
        username = "It's me";

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                .child(user.getDisplayName()).child("phone");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, dataSnapshot.getValue(String.class),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
//        if(user!=null){
//            if(user.getEmail()!=null){
//                Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
//            }
//            if(user.getDisplayName()!=null) {
//                username = user.getDisplayName();
//            }
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        zoomLevel = 15f;

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.map_style);
        mGoogleMap.setMapStyle(mapStyleOptions);

        for(i=0; i<placelist.size(); i++){
            if(j==i){
                mGoogleMap.addMarker(new MarkerOptions().position(placelist.get(i)).title(String.valueOf(title.get(j)))
                        .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.agent_logo)));
            }j++;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placelist.get(i)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placelist.get(i), zoomLevel));
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markertitle = marker.getTitle();
                    if(markertitle.equals(username)){
                        Toast.makeText(MainActivity.this, "It's me", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, markertitle, Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }

        if(locationPermissionGranted){
            getDeviceLocation();
            mGoogleMap.setMyLocationEnabled(true);
            init();
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    private void init(){
        Log.d(TAG, "init: initialization");

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH || actionId==EditorInfo.IME_ACTION_DONE
                        || event.getAction()==KeyEvent.ACTION_DOWN || event.getAction()==KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geoLocating");
        String searchString = inputSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location" + address.toString());

            LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(SearchlatLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: get current device location");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        try {
            if(locationPermissionGranted){
                Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Log.d(TAG, "onComplete: location found");
                            Location currentLocation = (Location) task.getResult();

                            DevicelatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d(TAG, "moveCamera: move camera to: lat: " + DevicelatLng.latitude + ", lng: " + DevicelatLng.longitude);
                            mGoogleMap.addMarker(new MarkerOptions().position(DevicelatLng).title(username)
                                    .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.my_location)));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DevicelatLng));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
                        } else {
                            Log.d(TAG, "onComplete: current location null!");
                            Toast.makeText(MainActivity.this, "Cannot get device location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());}
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int VectorID) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, VectorID);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.getDeviceID){
            getDeviceLocation();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder;

        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("LOGOUT ?");
        alertDialogBuilder.setMessage("Your decision will make you logged out.");
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent it = new Intent(MainActivity.this, StartScreen.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

//
//    EditText inputSearch;
//    ImageButton menuButton;
//    ImageView currentLocationButton;
//    private GoogleMap mMap;
//    float zoomLevel = 5f;
//    String username;
//    private static final String TAG = "MainActivity";
//    private boolean locationPermissionGranted = true;
//    FusedLocationProviderClient mfusedLocationProviderClient;
//    LatLng DevicelatLng;
//    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(MainActivity.this);
//
//        menuButton = findViewById(R.id.menuButtonID);
//        menuButton.setOnClickListener(this);
//        currentLocationButton = findViewById(R.id.getDeviceID);
//        currentLocationButton.setOnClickListener(this);
//        inputSearch = findViewById(R.id.searchMapID);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user!=null){
//            if(user.getDisplayName()!=null) {
//                username = user.getDisplayName();
//            }
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.map_style);
//        mMap.setMapStyle(mapStyleOptions);
//
//        LatLng dhaka = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(dhaka).title(username)
//                .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.my_location)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(dhaka));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka, zoomLevel));
//
////        Log.d(TAG, "onMapReady: Map is ready");
////        if(locationPermissionGranted){
////            getDeviceLocation();
//////            mMap.setMyLocationEnabled(true);
////            init();
////        }
////        mMap.setMyLocationEnabled(true);
//
//    }
//
////    private void init(){
////        Log.d(TAG, "init: initialization");
////
////        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
////            @Override
////            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
////                if(actionId== EditorInfo.IME_ACTION_SEARCH || actionId==EditorInfo.IME_ACTION_DONE
////                        || event.getAction()==KeyEvent.ACTION_DOWN || event.getAction()==KeyEvent.KEYCODE_ENTER){
////                    geoLocate();
////                }
////                return false;
////            }
////        });
////    }
//
////    private void geoLocate(){
////        Log.d(TAG, "geoLocate: geoLocating");
////        String searchString = inputSearch.getText().toString();
////        Geocoder geocoder = new Geocoder(MainActivity.this);
////        List<Address> list = new ArrayList<>();
////        try{
////            list = geocoder.getFromLocationName(searchString, 1);
////        }catch(IOException e){
////            Log.d(TAG, "geoLocate: ioexception" + e.getMessage());
////        }
////        if(list.size()>0){
////            Address address = list.get(0);
////            Log.d(TAG, "geoLocate: found a location" + address.toString());
////
////            LatLng SearchlatLng = new LatLng(address.getLatitude(), address.getLongitude());
////            mMap.moveCamera(CameraUpdateFactory.newLatLng(SearchlatLng));
////            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
////        }
////    }
//
//    @Override
//    public void onClick(View v) {
//        if(v.getId()==R.id.getDeviceID){
//            if(ContextCompat.checkSelfPermission(getApplicationContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        REQUEST_CODE_LOCATION_PERMISSION);
//            } else {
//                getDeviceLocation();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getDeviceLocation();
//            } else {
//                Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private void getDeviceLocation(){
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.getFusedLocationProviderClient(MainActivity.this).
//                removeLocationUpdates(locationRequest, new LocationCallback(){
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        super.onLocationResult(locationResult);
//                    }
//                }, Looper.getMainLooper());
//
////        Log.d(TAG, "getDeviceLocation: get current device location");
////        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
////        try {
////            if(locationPermissionGranted){
////                Task location = mfusedLocationProviderClient.getLastLocation();
////                location.addOnCompleteListener(new OnCompleteListener() {
////                    @Override
////                    public void onComplete(@NonNull Task task) {
////                        if(task.isSuccessful()){
////                            Log.d(TAG, "onComplete: location found");
////                            Location currentLocation = (Location) task.getResult();
////
////                            DevicelatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
////                            Log.d(TAG, "moveCamera: move camera to: lat: " + DevicelatLng.latitude + ", lng: " + DevicelatLng.longitude);
////                            mMap.addMarker(new MarkerOptions().position(DevicelatLng).title(username)
////                                    .icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.my_location)));
////                            mMap.moveCamera(CameraUpdateFactory.newLatLng(DevicelatLng));
////                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
////                        } else {
////                            Log.d(TAG, "onComplete: current location null!");
////                            Toast.makeText(MainActivity.this, "Cannot get device location", Toast.LENGTH_LONG).show();
////                        }
////                    }
////                });
////            }
////        }catch (SecurityException e){Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());}
//    }
//
//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int VectorID) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, VectorID);
//        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),
//                vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
//                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
}
