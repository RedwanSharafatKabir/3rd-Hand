package com.example.a3rdhand.GoogleMap;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.a3rdhand.AppActions.AboutActivity;
import com.example.a3rdhand.AppActions.HelpActivity;
import com.example.a3rdhand.AppActions.MainActivity;
import com.example.a3rdhand.AppActions.ProfileActivity;
import com.example.a3rdhand.CallBack.IFirebaseAgentInfoListener;
import com.example.a3rdhand.CallBack.IFirebaseFailedListener;
import com.example.a3rdhand.FCM.MySingleton;
import com.example.a3rdhand.MedicalServiceOrderAndReceive.MedicalServiceActivity;
import com.example.a3rdhand.ModelClass.AgentGeoModel;
import com.example.a3rdhand.ModelClass.AgentInfoModel;
import com.example.a3rdhand.ModelClass.AnimationModel;
import com.example.a3rdhand.ModelClass.Common;
import com.example.a3rdhand.PackageOrderAndReceive.LeftEquipmentSavedRecord;
import com.example.a3rdhand.ModelClass.GeoQueryModel;
import com.example.a3rdhand.FCM.TokenAPIClient;
import com.example.a3rdhand.R;
import com.example.a3rdhand.Remote.IGoogleApi;
import com.example.a3rdhand.Remote.RetrofitClient;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapFragmentClass extends Fragment implements
        OnMapReadyCallback, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener,
        IFirebaseFailedListener, IFirebaseAgentInfoListener {

    View views;
    Button findPackage, confrmDelivery, confrmMedical, confrmShopping;
    LatLng DevicelatLng;
    float zoomLevel = 16f;
    TokenAPIClient tokenAPIClient;
    String location_Thing, userPhoneNumber, tempPackage, agentLocationName, agentImageAvatarUrl, userName;
    private GoogleMap mGoogleMap;
    private static final String TAG = "FindLotFragment";
    FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    BottomNavigationView bottomNavigation;
    DatabaseReference databaseReference;
    SupportMapFragment supportMapFragment;
    private Double distance = 1.0;
    private static final double LIMIT_RANGE = 20.0;
    private Location previousLocation, currentLocation;
    IFirebaseAgentInfoListener iFirebaseAgentInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;
    private boolean firstTime = true;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleApi iGoogleApi;
    private BottomSheetDialog bottomSheetDialog;
    private String baseUrl = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAALNRxdp4:APA91bFuADHP9HsCUHabY3T4Bi5T9j3k3AgpVE-MEFg1lpdQTJJiKeU_55zRa-H2TqN6roythzKstAyIJ65bdrhM7H-jNqHINx7IJvccF5dH3pXunrQasU_pVreMIKC8JtKRyeUQqEV9";
    final private String contentType = "application/json";
    final String Tag = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mfusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCustomerPackageLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        findPackage = views.findViewById(R.id.findPackageID);
        findPackage.setOnClickListener(this);
        findPackage.setVisibility(views.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference("Left Equipment List Record of All Users");

        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference ref = databaseReference.child(userPhoneNumber).child("locationThing");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location_Thing = dataSnapshot.getValue(String.class);
                try {
                    if (!location_Thing.isEmpty()) {
                        findPackage.setVisibility(views.VISIBLE);
                    }
                } catch (Exception e) {
                    findPackage.setVisibility(views.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        bottomNavigation = views.findViewById(R.id.bottomNavigationID);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().setGroupCheckable(0, false, true);
        init();

        return views;
    }

    private void init() {
        iGoogleApi = RetrofitClient.getInstance().create(IGoogleApi.class);

        iFirebaseFailedListener = this;
        iFirebaseAgentInfoListener = this;

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

                // When user change location it counts distance from previous location
                if (firstTime) {
                    previousLocation = currentLocation = locationResult.getLastLocation();
                    firstTime = false;
                } else {
                    previousLocation = currentLocation;
                    currentLocation = locationResult.getLastLocation();
                }

                if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) {
                    loadAvailableAgents();
                } else {
                    // Do nothing
                }
            }
        };

        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mfusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        loadAvailableAgents();
    }

    private void loadAvailableAgents() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(supportMapFragment.getView(), "Location permission required", Snackbar.LENGTH_LONG).show();
            return;
        }
        mfusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(supportMapFragment.getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Load all agents in the city
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    agentLocationName = addressList.get(0).getLocality();
                    DatabaseReference agentsLocationRef = FirebaseDatabase.getInstance()
                            .getReference("Agent Current Location").child(agentLocationName);

                    GeoFire gf = new GeoFire(agentsLocationRef);
                    GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), distance);
                    geoQuery.removeAllListeners();
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            Common.agentsFound.add(new AgentGeoModel(key, location));
                        }

                        @Override
                        public void onKeyExited(String key) {}

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {}

                        @Override
                        public void onGeoQueryReady() {
                            if(distance <= LIMIT_RANGE){
                                distance++;
                                loadAvailableAgents(); // continue search in new distance
                            } else {
                                distance = 1.0; // reset distance
                                addAgentMarker();
                            }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Snackbar.make(getView(), "Agents aren't available", Snackbar.LENGTH_LONG).show();
                        }
                    });

                    agentsLocationRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                            GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0),
                                    geoQueryModel.getL().get(1));
                            AgentGeoModel agentGeoModel = new AgentGeoModel(snapshot.getKey(), geoLocation);
                            Location newAgentLocation = new Location("");
                            newAgentLocation.setLatitude(geoLocation.latitude);
                            newAgentLocation.setLongitude(geoLocation.longitude);
                            float newDistance = location.distanceTo(newAgentLocation)/1000;
                            if(newDistance<=LIMIT_RANGE){
                                findAgentByKey(agentGeoModel);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                } catch (IOException e) {
//                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(getView(), "Agents aren't available", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addAgentMarker() {
        if(Common.agentsFound.size() > 0) {
            Observable.fromIterable(Common.agentsFound).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(agentGeoModel -> {
                        findAgentByKey(agentGeoModel);
                    }, throwable -> {
                        loadAvailableAgents();
                    }, ()->{

                    });
        }
    }

    private void findAgentByKey(AgentGeoModel agentGeoModel) {
        FirebaseDatabase.getInstance().getReference("Online Available Agents").child(agentGeoModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            agentGeoModel.setAgentInfoModel(snapshot.getValue(AgentInfoModel.class));
                            iFirebaseAgentInfoListener.onAgentInfoLoadSuccess(agentGeoModel);
                        } else {
                            iFirebaseFailedListener.onFirebaseLoadFailure(getString(R.string.not_found_agent));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iFirebaseFailedListener.onFirebaseLoadFailure(error.getMessage());
                    }
                });
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
                                        Snackbar snackbar = Snackbar.make(views, "Location permission denied !", Snackbar.LENGTH_LONG);
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
                                        loadAvailableAgents();
                                    }
                                });

                                return true;
                            }
                        });

                        // Set device location button layout right bottom
                        View locationButton = ((View)supportMapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent()).findViewById(Integer.parseInt("2"));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        params.setMargins(0, 0, 0, 500);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Snackbar snackbar = Snackbar.make(views, "Location permission denied !", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                        snackbar.setDuration(5000).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {}
                }).check();
    }

    @Override
    public void onClick(final View v) {
        if(v.getId()==R.id.findPackageID){
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
                                        }}catch (Exception e){findPackage.setVisibility(v.GONE);}
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
                                            tempPackage = "Locate customer's specified address around here.";
                                            mGoogleMap.addMarker(new MarkerOptions().position(SearchlatLng)
                                                    .title(tempPackage).icon(bitmapDescriptorFromVector(getActivity(),
                                                            R.drawable.package_icon_customer_and_agent_app)));

                                            getRemoveMarkerRequest(SearchlatLng);
                                        }

                                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                String markertitle = marker.getTitle();
                                                try {
                                                    if (markertitle.equals(tempPackage)) {
                                                        LeftEquipmentSavedRecord leftEquipmentSavedRecord = new LeftEquipmentSavedRecord();
                                                        leftEquipmentSavedRecord.show(getFragmentManager(), "Sample dialog");
                                                    } else if(!markertitle.equals(tempPackage)){
                                                        showBottomSheetDialog(markertitle);
                                                    }
                                                } catch (Exception e) {}
                                                return false;
                                            }
                                        });
                                    }
                                } catch(Exception e){
//                                    mGoogleMap.clear();
                                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            String markerTitle = marker.getTitle();
                                            try {
                                                if (markerTitle.equals(tempPackage)) {
                                                    Snackbar.make(getView(), "This package is no longer available", Snackbar.LENGTH_LONG).show();
                                                } else if(!markerTitle.equals(tempPackage)){
                                                    showBottomSheetDialog(markerTitle);
                                                }
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

    @Override
    public void onFirebaseLoadFailure(String message) {
        Snackbar.make(getView(), "Database failure", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAgentInfoLoadSuccess(AgentGeoModel agentGeoModel) {
        if(!Common.markerList.containsKey(agentGeoModel.getKey())){
            Common.markerList.put(agentGeoModel.getKey(),
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(agentGeoModel.getGeoLocation().latitude, agentGeoModel.getGeoLocation().longitude))
                            .flat(true).title(Common.buildName(agentGeoModel.getAgentInfoModel().getUsername(),
                                    agentGeoModel.getAgentInfoModel().getEmployeeid()))
                            .snippet(agentGeoModel.getAgentInfoModel().getPhone())
                            .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.agent_with_utility))));

            agentImageAvatarUrl = agentGeoModel.getAgentInfoModel().getAvatar();
        }

        if(!TextUtils.isEmpty(agentLocationName)){
            DatabaseReference agentLocation = FirebaseDatabase.getInstance().getReference("Agent Current Location")
                    .child(agentLocationName).child(agentGeoModel.getKey());
            agentLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChildren()){
                        if(Common.markerList.get(agentGeoModel.getKey()) != null){
                            Common.markerList.get(agentGeoModel.getKey()).remove(); // Remove marker
                            Common.markerList.remove(agentGeoModel.getKey()); // Remove info from HashMap
                            Common.agentLocationSubscribe.remove(agentGeoModel.getKey()); // Remove agent information
                            agentLocation.removeEventListener(this); // Remove event listener
                        }
                    }
                    /* // Below code is for updating agent location in customer app (Needs Google Map Billing account)
                    else {
                        if(Common.markerList.get(agentGeoModel.getKey()) != null){
                            GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                            AnimationModel animationModel = new AnimationModel(false, geoQueryModel);
                            if(Common.agentLocationSubscribe.get(agentGeoModel.getKey()) != null){
                                Marker currentMarker = Common.markerList.get(agentGeoModel.getKey());
                                AnimationModel oldPosition = Common.agentLocationSubscribe.get(agentGeoModel.getKey());
                                String from = new StringBuilder()
                                        .append(oldPosition.getGeoQueryModel().getL().get(0))
                                        .append(", ")
                                        .append(oldPosition.getGeoQueryModel().getL().get(1)).toString();
                                String to = new StringBuilder()
                                        .append(animationModel.getGeoQueryModel().getL().get(0))
                                        .append(", ")
                                        .append(animationModel.getGeoQueryModel().getL().get(1)).toString();
                                moveMarkerAnimation(agentGeoModel.getKey(), animationModel, currentMarker, from, to);
                            } else {
                                Common.agentLocationSubscribe.put(agentGeoModel.getKey(), animationModel);
                            }
                        }
                    }*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /* Below code is for updating agent location in customer app (Needs Google Map Billing account) */
    private void moveMarkerAnimation(String key, AnimationModel animationModel, Marker currentMarker, String from, String to) {
        if(!animationModel.isRun()){
            compositeDisposable.add(iGoogleApi.getDirections("onService", "offService",
                     from, to, getString(R.string.google_map_API_key))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(returnResult -> {
                Log.d("API_RETURN", returnResult);
                try{
                    JSONObject jsonObject = new JSONObject(returnResult);
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject route = jsonArray.getJSONObject(i);
                        JSONObject poly = route.getJSONObject("overview_polyline");
                        String polyline = poly.getString("points");

                        animationModel.setPolylineList(Common.decodePoly(polyline));
                        animationModel.setIndex(-1);
                        animationModel.setNext(1);

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if(animationModel.getPolylineList()!=null && animationModel.getPolylineList().size()>1){
                                    if(animationModel.getIndex() < animationModel.getPolylineList().size()-2){
                                        animationModel.setIndex(animationModel.getIndex()+1);
                                        animationModel.setNext(animationModel.getIndex()+1);
                                        animationModel.setStart(animationModel.getPolylineList().get(animationModel.getIndex()));
                                        animationModel.setEnd(animationModel.getPolylineList().get(animationModel.getNext()));
                                    }
                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                    valueAnimator.setDuration(3000);
                                    valueAnimator.setInterpolator(new LinearInterpolator());
                                    valueAnimator.addUpdateListener(value -> {
                                        animationModel.setV(value.getAnimatedFraction());
                                        animationModel.setLat(animationModel.getV()*animationModel.getEnd().latitude +
                                                (1-animationModel.getV())*animationModel.getStart().latitude);
                                        animationModel.setLng(animationModel.getV()*animationModel.getEnd().longitude +
                                                (1-animationModel.getV())*animationModel.getStart().longitude);
                                        LatLng newPos = new LatLng(animationModel.getLat(), animationModel.getLng());
                                        currentMarker.setPosition(newPos);
                                        currentMarker.setAnchor(0.5f, 0.5f);
                                        currentMarker.setRotation(Common.getBearing(animationModel.getStart(), newPos));
                                    });

                                    valueAnimator.start();
                                    if(animationModel.getIndex()<animationModel.getPolylineList().size() - 2){
                                        animationModel.getHandler().postDelayed(this, 1500);
                                    } else if(animationModel.getIndex()<animationModel.getPolylineList().size() - 1){
                                        animationModel.setRun(false);
                                        Common.agentLocationSubscribe.put(key, animationModel);
                                    }
                                }
                            }
                        };

                        animationModel.getHandler().postDelayed(runnable, 1500);
                    }
                } catch (Exception e){
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            })
            );
        }
    }

    public void getRemoveMarkerRequest(LatLng SearchlatLng){
        try {
            FileInputStream fileInputStream1 = getActivity().openFileInput("remove_old_marker.txt");
            InputStreamReader inputStreamReader1 = new InputStreamReader(fileInputStream1);
            BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
            String recievedMessage1;
            StringBuffer stringBuffer1 = new StringBuffer();
            while((recievedMessage1=bufferedReader1.readLine())!=null){
                stringBuffer1.append(recievedMessage1);
            }

            String removeOldMarkerString = stringBuffer1.toString();
            if(!removeOldMarkerString.isEmpty()){
//                mGoogleMap.clear();
                setRemoveMarkerRequestNull();
                getCustomerPackageLocation();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRemoveMarkerRequestNull(){
        String oldMarkerRemoveStringNull = "";
        try {
            FileOutputStream fileOutputStream = getContext()
                    .openFileOutput("remove_old_marker.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(oldMarkerRemoveStringNull.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheetDialog(String markertitle){
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetTheme);
        View bottomSheetView = LayoutInflater.from(getActivity())
                .inflate(R.layout.call_agent_botomsheet_dialog,
                        views.findViewById(R.id.bottomSheetContainerId));

        confrmDelivery = bottomSheetView.findViewById(R.id.confirmPackageAgentID);
        confrmDelivery.setVisibility(View.INVISIBLE);
        confrmMedical = bottomSheetView.findViewById(R.id.confirmMedicalAgentID);
        confrmMedical.setVisibility(View.INVISIBLE);
        confrmShopping = bottomSheetView.findViewById(R.id.confirmShoppingAgentID);
        confrmShopping.setVisibility(View.INVISIBLE);

        TextView textView = bottomSheetView.findViewById(R.id.callingPackageAgentNameID);
        textView.setText(markertitle);
        ImageView imageView = bottomSheetView.findViewById(R.id.agentImageId);
        Glide.with(getActivity()).load(agentImageAvatarUrl).into(imageView);
        checkOrderConfirmation();

        bottomSheetView.findViewById(R.id.rejectPackageAgentID)
                .setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    private void checkOrderConfirmation (){
        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        // Confirm delivery request
        DatabaseReference ref1 = databaseReference.child(userPhoneNumber).child("locationThing");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location_Thing = dataSnapshot.getValue(String.class);
                try {
                    if (!location_Thing.isEmpty()) {
                        confrmDelivery.setVisibility(views.VISIBLE);
                        confrmDelivery.setOnClickListener(v -> {
                            Toast.makeText(getActivity(), "Delivery order confirmed", Toast.LENGTH_SHORT).show();
                            SendNotificationToAgent();
                        });
                    }
                } catch (Exception e) {
                    confrmDelivery.setVisibility(views.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                confrmDelivery.setVisibility(views.INVISIBLE);
            }
        });

        // Confirm shopping request
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Shopping List Record of All Users").child(userPhoneNumber);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.exists()){
                        confrmShopping.setVisibility(views.VISIBLE);
                        confrmShopping.setOnClickListener(v -> {
                            Toast.makeText(getActivity(), "Shopping order confirmed", Toast.LENGTH_SHORT).show();
                            SendNotificationToAgent();
                        });
                    } else {
                        confrmShopping.setVisibility(views.INVISIBLE);
                    }

                } catch (Exception e) {
                    confrmShopping.setVisibility(views.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                confrmShopping.setVisibility(views.INVISIBLE);
            }
        });

        // Confirm medical request
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Medical Request Record of All Users").child(userPhoneNumber);
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.exists()){
                        confrmMedical.setVisibility(View.VISIBLE);
                        confrmMedical.setOnClickListener(v -> {
                            Toast.makeText(getActivity(), "Emergency medical transport order confirmed", Toast.LENGTH_SHORT).show();
                            SendNotificationToAgent();
                        });
                    } else {
                        confrmMedical.setVisibility(views.INVISIBLE);
                    }

                } catch (Exception e) {
                    confrmMedical.setVisibility(views.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                confrmMedical.setVisibility(views.INVISIBLE);
            }
        });
    }

    private void SendNotificationToAgent() {
//        Retrofit retrofit = new Retrofit
//                .Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        tokenAPIClient = retrofit.create(TokenAPIClient.class);
//
//        Call<TokenResponse> call =

        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                .child(userPhoneNumber).child("username");
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.agent_with_utility);
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        TOPIC = "/topics/" + userName; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "Receive request from: " + userName;
        NOTIFICATION_MESSAGE = "Phone: " + userPhoneNumber;

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(Tag, "onCreate: " + e.getMessage());
        }

        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(baseUrl, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
