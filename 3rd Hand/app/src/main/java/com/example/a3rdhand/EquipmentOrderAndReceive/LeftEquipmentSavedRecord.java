package com.example.a3rdhand.EquipmentOrderAndReceive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeftEquipmentSavedRecord extends DialogFragment implements View.OnClickListener {

    TextView eqpName, building, locationName;
    Button closeButton, editRecord, deleteRecord;
    String userPhone_Number, eqpNameStr, laneStr, buildingStr, floorStr, flatStr, locationNameStr, eqpTypeStr;
    String passed_String = "2580";
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_left_equipment_saved_record, null);

        getDialog().setCanceledOnTouchOutside(false);

        eqpName = v.findViewById(R.id.equipmentNameAndTypeTextID);
        building = v.findViewById(R.id.buildingNumberTextID);
        locationName = v.findViewById(R.id.customerAddressTextID);

        closeButton = v.findViewById(R.id.closeID);
        closeButton.setOnClickListener(this);
        editRecord = v.findViewById(R.id.editRecordID);
        editRecord.setOnClickListener(this);
        deleteRecord =v.findViewById(R.id.deleteRecordID);
        deleteRecord.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Left Equipment List Record of All Users");
        checkMethod();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void checkMethod(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhone_Number = dataSnapshot.getValue(String.class);
                        DatabaseReference ref2 = databaseReference.child(userPhone_Number).child("locationThing");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                locationNameStr = dataSnapshot.getValue(String.class);}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref3 = databaseReference.child(userPhone_Number).child("building_string");
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                buildingStr = dataSnapshot.getValue(String.class);}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref4 = databaseReference.child(userPhone_Number).child("equipment_name_string");
                        ref4.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                eqpNameStr = dataSnapshot.getValue(String.class);}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref5 = databaseReference.child(userPhone_Number).child("equipment_type_string");
                        ref5.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                eqpTypeStr = dataSnapshot.getValue(String.class);
                                eqpName.setText(eqpNameStr + " is a " + eqpTypeStr + " type equipment.");}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref6 = databaseReference.child(userPhone_Number).child("flat_string");
                        ref6.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flatStr = dataSnapshot.getValue(String.class);}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref7 = databaseReference.child(userPhone_Number).child("floor_string");
                        ref7.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                floorStr = dataSnapshot.getValue(String.class);
                                building.setText("Apartment: " + buildingStr
                                        + ", Floor: " + floorStr  + ", Flat: " + flatStr);}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref8 = databaseReference.child(userPhone_Number).child("lane_string");
                        ref8.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                laneStr = dataSnapshot.getValue(String.class);
                                locationName.setText("Located in " + locationNameStr + ", Block / Road: " + laneStr);}
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
    public void onClick(View v) {
        if(v.getId()==R.id.closeID){
            getDialog().dismiss();
        }

        if(v.getId()==R.id.editRecordID){
            try {
                FileOutputStream fileOutputStream = getContext()
                        .openFileOutput("random_Info.txt", Context.MODE_PRIVATE);
                fileOutputStream.write(passed_String.getBytes());
                fileOutputStream.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            LeftEquipmentActivity leftEquipmentActivity = new LeftEquipmentActivity();
            leftEquipmentActivity.show(getFragmentManager(), "Sample Dialog");

            getDialog().dismiss();
        }

        if(v.getId()==R.id.deleteRecordID){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("Are you sure you want to delete your package courier delivery record ?");
            alertDialog.setIcon(R.drawable.exit);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    if (user1 != null) {
                        if (user1.getDisplayName() != null) {
                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                                    .child(user1.getDisplayName()).child("phone");
                            ref1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    userPhone_Number = dataSnapshot.getValue(String.class);
                                    try {
                                        databaseReference.child(userPhone_Number).removeValue();
                                        Toast t = Toast.makeText(getActivity(), "Record deleted", Toast.LENGTH_LONG);
                                        t.setGravity(Gravity.CENTER, 0, 0);
                                        t.show();
                                        getDialog().dismiss();

                                    } catch(Exception e){getDialog().dismiss();}
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                    }
                }
            });

            alertDialog.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialogBuilder = alertDialog.create();
            alertDialogBuilder.show();
        }
    }
}
