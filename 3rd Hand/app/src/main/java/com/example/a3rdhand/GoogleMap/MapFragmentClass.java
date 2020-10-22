package com.example.a3rdhand.GoogleMap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.AppActions.AboutActivity;
import com.example.a3rdhand.AppActions.HelpActivity;
import com.example.a3rdhand.AppActions.ProfileActivity;
import com.example.a3rdhand.ModelClass.Equipment_Agent_Longitude_Latitude_List;
import com.example.a3rdhand.EquipmentOrderAndReceive.LeftEquipmentSavedRecord;
import com.example.a3rdhand.EquipmentOrderAndReceive.Call_Package_Agent_Dialog;
import com.example.a3rdhand.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragmentClass extends Fragment implements
        OnMapReadyCallback, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    View v;
    Button findAgent;
    float zoomLevel = 16f;
    String location_Thing, userPhoneNumber, tempPackage;
    int j = 0, i = 0;
    private GoogleMap mGoogleMap;
    private static final String TAG = "FindLotFragment";
    FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LatLng DevicelatLng;
    private Equipment_Agent_Longitude_Latitude_List equipmentAgentLongitude_latitude_list;
    ArrayList<LatLng> placelist;
    ArrayList<String> title;
    BottomNavigationView bottomNavigation;
    DatabaseReference databaseReference;
    SupportMapFragment supportMapFragment;

    public MapFragmentClass() {
        equipmentAgentLongitude_latitude_list = new Equipment_Agent_Longitude_Latitude_List();
        placelist = equipmentAgentLongitude_latitude_list.getPlacelist();
        title = equipmentAgentLongitude_latitude_list.getTitle();
    }

    @Override
    public void onDestroy() {
        mfusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        findAgent = v.findViewById(R.id.findPackageServiceAgentID);
        findAgent.setOnClickListener(this);
        findAgent.setVisibility(v.GONE);

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
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }

        bottomNavigation = v.findViewById(R.id.bottomNavigationID);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().setGroupCheckable(0, false, true);

        init();

        return v;
    }

    private void init() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, zoomLevel));
            }
        };

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mfusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
        mGoogleMap.setMapStyle(mapStyleOptions);

        // Check permission
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).
                withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

                        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    return false;
                                }
                                mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar snackbar = Snackbar.make(v, "Location permission denied !", Snackbar.LENGTH_LONG);
                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                                        snackbar.setDuration(5000).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        DevicelatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
                                        getCustomerPackageLocation();
                                    }
                                });

                                return true;
                            }
                        });

                        // Set device location button right bottom
                        View locationButton = ((View)supportMapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        params.setMargins(0, 0, 0, 500);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Snackbar snackbar = Snackbar.make(v, "Location permission denied !", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                        snackbar.setDuration(5000).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {}
                }).check();

        for (i = 0; i < placelist.size(); i++) {
            if (j == i) {
                mGoogleMap.addMarker(new MarkerOptions().position(placelist.get(i)).title(String.valueOf(title.get(j)))
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_agent_location_on_24)));
            }
            j++;
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placelist.get(i), 10f));
        }
    }

    @Override
    public void onClick(final View v) {
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
}
