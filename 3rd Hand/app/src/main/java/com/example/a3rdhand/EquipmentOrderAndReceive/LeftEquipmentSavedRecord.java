package com.example.a3rdhand.EquipmentOrderAndReceive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeftEquipmentSavedRecord extends DialogFragment implements View.OnClickListener {

    TextView eqpName, lane, building, floor, flat, locationName, eqpType;
    Button closeButton;
    String userPhoneNumber;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_left_equipment_saved_record, null);

        getDialog().setCanceledOnTouchOutside(false);

        eqpName = v.findViewById(R.id.equipmentNameTextID);
        lane = v.findViewById(R.id.laneNumberTextID);
        building = v.findViewById(R.id.buildingNumberTextID);
        floor = v.findViewById(R.id.floorTextID);
        flat = v.findViewById(R.id.flatTextID);
        locationName = v.findViewById(R.id.customerAddressTextID);
        eqpType = v.findViewById(R.id.equipmentTypeTextID);

        closeButton = v.findViewById(R.id.closeID);
        closeButton.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Left Equipment List Record of All Users");
        checkMethod();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void checkMethod(){
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
                        DatabaseReference ref2 = databaseReference.child(userPhoneNumber).child("locationThing");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                locationName.setText(" Location: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref3 = databaseReference.child(userPhoneNumber).child("building_string");
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                building.setText(" Apartment: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref4 = databaseReference.child(userPhoneNumber).child("equipment_name_string");
                        ref4.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                eqpName.setText(" Name: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref5 = databaseReference.child(userPhoneNumber).child("equipment_type_string");
                        ref5.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                eqpType.setText(" Type: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref6 = databaseReference.child(userPhoneNumber).child("flat_string");
                        ref6.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flat.setText(" Flat: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref7 = databaseReference.child(userPhoneNumber).child("floor_string");
                        ref7.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                floor.setText(" Floor: " + dataSnapshot.getValue(String.class));}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        DatabaseReference ref8 = databaseReference.child(userPhoneNumber).child("lane_string");
                        ref8.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                lane.setText(" Road / Lane: " + dataSnapshot.getValue(String.class));}
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
    }
}
