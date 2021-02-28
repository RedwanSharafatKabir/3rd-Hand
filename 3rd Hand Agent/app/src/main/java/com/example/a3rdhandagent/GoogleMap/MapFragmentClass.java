package com.example.a3rdhandagent.GoogleMap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.a3rdhandagent.AppActions.StartScreen;
import com.example.a3rdhandagent.ModelClass.StoreCurrentOnlineAgentsInfo;
import com.example.a3rdhandagent.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;
import java.util.Locale;
//import com.google.android.libraries.places.api.model.Place;

public class MapFragmentClass extends Fragment implements OnMapReadyCallback {

    View v;
    float zoomLevel = 16f;
    LatLng DevicelatLng;
    GeoFire geoFire;
    String agentLocationName, agentPhoneNumberID, agentUsername, agentEmployeeId, agentAvatar;
    private GoogleMap mGoogleMap;
    SupportMapFragment supportMapFragment;
    DatabaseReference onlineRef, currentAgentRef, agentsLocationRef, onlineAvailableAgents;
    FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isFirstTime = true;
    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentAgentRef!=null) {
                currentAgentRef.onDisconnect().removeValue();
                isFirstTime = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(supportMapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroy() {
        mfusedLocationProviderClient.removeLocationUpdates(locationCallback);
        try {
            geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch(Exception e){
            Toast.makeText(getActivity(), "You're offline now", Toast.LENGTH_SHORT).show();
        }
        onlineRef.removeEventListener(onlineValueEventListener);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerOnlineSystem();
    }

    private void registerOnlineSystem() {
        onlineRef.addValueEventListener(onlineValueEventListener);
        storeAgentTemporaryInfoMethod();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        onlineAvailableAgents = FirebaseDatabase.getInstance().getReference("Online Available Agents");
        init();

        return v;
    }

    private void init() {
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(50f);
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition, zoomLevel));

                // get address name
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(), 1);
                    agentLocationName = addressList.get(0).getLocality();
                    agentsLocationRef = FirebaseDatabase.getInstance().getReference("Agent Current Location").child(agentLocationName);
                    currentAgentRef = agentsLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    geoFire = new GeoFire(agentsLocationRef);
                } catch (IOException e) {
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }

                // update location
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        new GeoLocation(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude()),
                        (key, error) -> {
                            if (error != null) {
                                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                            } else {
                                if(isFirstTime){
                                    isFirstTime = false;
                                }
                            }
                        });
                registerOnlineSystem();
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

    public void storeAgentTemporaryInfoMethod(){
        agentPhoneNumberID = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference agentInfoRef1 = FirebaseDatabase.getInstance().getReference("Agent Information")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("username");
        agentInfoRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                agentUsername = dataSnapshot.getValue(String.class);

                DatabaseReference agentInfoRef2 = FirebaseDatabase.getInstance().getReference("Agent Information")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("employeeid");
                agentInfoRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        agentEmployeeId = dataSnapshot.getValue(String.class);

                        DatabaseReference agentInfoRef3 = FirebaseDatabase.getInstance().getReference("Agent Image URL")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("avatar");
                        agentInfoRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                agentAvatar = dataSnapshot.getValue(String.class);

                                StoreCurrentOnlineAgentsInfo storeCurrentOnlineAgentsInfo =
                                        new StoreCurrentOnlineAgentsInfo(agentPhoneNumberID, agentUsername, agentEmployeeId, agentAvatar);
                                onlineAvailableAgents.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(storeCurrentOnlineAgentsInfo);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
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
                                        init();
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

        LatLng dhakalatLng = new LatLng(23.8103, 90.4125);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dhakalatLng, 10f));
    }
}
