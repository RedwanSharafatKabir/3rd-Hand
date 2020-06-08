package com.example.a3rdhand;

import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
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

public class MapFragmentClass extends Fragment implements OnMapReadyCallback, View.OnClickListener{

    Button enterLeftEquipmentButton;
    ImageView imageView;
    EditText inputSearch;
    float zoomLevel;
    String username, location_Thing, userPhoneNumber;
    int j = 0, i = 0;
    private GoogleMap mGoogleMap;
    private static final String TAG = "FindLotFragment";
    private boolean locationPermissionGranted = true;
    FusedLocationProviderClient mfusedLocationProviderClient;
    LatLng DevicelatLng;
    private Longitude_Latitude_List longitude_latitude_list;
    ArrayList<LatLng> placelist;
    ArrayList<String> title;

    public MapFragmentClass() {
        longitude_latitude_list = new Longitude_Latitude_List();
        placelist = longitude_latitude_list.getPlacelist();
        title = longitude_latitude_list.getTitle();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapID);
        mapFragment.getMapAsync(MapFragmentClass.this);

        enterLeftEquipmentButton = v.findViewById(R.id.leftEquipmentSearchID);
        enterLeftEquipmentButton.setOnClickListener(this);
        imageView = v.findViewById(R.id.getDeviceID);
        imageView.setOnClickListener(this);
        inputSearch = v.findViewById(R.id.searchMapID);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(user.getEmail()!=null){}
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhoneNumber = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("username");
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        zoomLevel = 15f;

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
        mGoogleMap.setMapStyle(mapStyleOptions);

        for (i = 0; i < placelist.size(); i++) {
            if (j == i) {
                mGoogleMap.addMarker(new MarkerOptions().position(placelist.get(i)).title(String.valueOf(title.get(j)))
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.agent_logo)));
            }
            j++;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(placelist.get(i)));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placelist.get(i), zoomLevel));
        }

        try {
            if (locationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                getDeviceLocation();
                init();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }catch(Exception e){alertDialogMethod();}
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
        String searchString = inputSearch.getText().toString();
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
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(SearchlatLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: get current device location");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (locationPermissionGranted) {
                Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: location found");
                            Location currentLocation = (Location) task.getResult();

                            DevicelatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d(TAG, "moveCamera: move camera to: lat: " + DevicelatLng.latitude + ", lng: " + DevicelatLng.longitude);
                            mGoogleMap.addMarker(new MarkerOptions().position(DevicelatLng).title(username)
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.my_location)));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DevicelatLng));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DevicelatLng, zoomLevel));
                            getCustomerPackageLocation();
                        } else {
                            Log.d(TAG, "onComplete: current location null!");
                            Toast.makeText(getActivity(), "Cannot get device location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
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
    public void onClick(View v) {
        if (v.getId() == R.id.getDeviceID) {
            getDeviceLocation();
        }

        if(v.getId()==R.id.leftEquipmentSearchID){
            LeftEquipmentActivity leftEquipmentActivity = new LeftEquipmentActivity();
            leftEquipmentActivity.show(getFragmentManager(), "Sample dialog");
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
                                            mGoogleMap.addMarker(new MarkerOptions().position(SearchlatLng)
                                                    .title(username + "'s package is somewhere in this area. " +
                                                            "Please find out customer's specified address around here.")
                                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.customer_package_final)));
                                        }

                                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                String markertitle = marker.getTitle();
                                                try {
                                                    if (markertitle.equals(location_Thing)) {
                                                        Toast toast = Toast.makeText(getActivity(), markertitle, Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                    } else {
                                                        Toast toast = Toast.makeText(getActivity(), markertitle, Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
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
                                            String markertitle = marker.getTitle();
                                            try {
                                                if (markertitle.equals(username)) {
                                                    Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), "Not available", Toast.LENGTH_SHORT).show();
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

    public void alertDialogMethod(){
        AlertDialog.Builder alertDialogBuilder;

        alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle("Allow GPS !");
        alertDialogBuilder.setMessage(R.string.allow_location);
        alertDialogBuilder.setIcon(R.drawable.allow_gps);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}