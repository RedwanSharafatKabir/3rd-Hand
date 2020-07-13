package com.example.a3rdhand.EquipmentOrderAndReceive;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.a3rdhand.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LeftEquipmentActivity extends DialogFragment implements View.OnClickListener{

    EditText eqpName, lane, building, floor, flat;
    AutoCompleteTextView autoCompleteTextView;
    String locationArrayString[];
    String locationThing, locationThingString;
    Button save, cancelButton;
    Spinner eqpType;
    DatabaseReference databaseReference;
    String userPhoneNumber, username, location_Thing, passed_String;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_left_equipment, null);

        getDialog().setCanceledOnTouchOutside(false);

        eqpName = v.findViewById(R.id.equipmentNameID);
        lane = v.findViewById(R.id.laneNumberID);
        building = v.findViewById(R.id.buildingNumberID);
        floor = v.findViewById(R.id.floorID);
        flat = v.findViewById(R.id.flatID);

        cancelButton = v.findViewById(R.id.cancelID);
        cancelButton.setOnClickListener(this);

        autoCompleteTextView = v.findViewById(R.id.customerAddressID);
        locationArrayString = getResources().getStringArray(R.array.location_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, locationArrayString);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        save = v.findViewById(R.id.leftEquipmentSaveButtonID);
        save.setOnClickListener(this);
        eqpType = v.findViewById(R.id.equipmentTypeID);
        eqpType.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, EquipmentType_List.equipmentTypeList));

        databaseReference = FirebaseDatabase.getInstance().getReference("Left Equipment List Record of All Users");
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

//        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId== EditorInfo.IME_ACTION_SEARCH
//                    || actionId==EditorInfo.IME_ACTION_DONE
//                    || event.getAction()==KeyEvent.ACTION_DOWN
//                    || event.getAction()==KeyEvent.KEYCODE_ENTER){
//                }
//                return false;
//            }
//        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }

        try {
            FileInputStream fileInputStream = getActivity().openFileInput("random_Info.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String recievedMessage;
            StringBuffer stringBuffer = new StringBuffer();

            while((recievedMessage=bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            }

            passed_String = stringBuffer.toString();
            if(!passed_String.isEmpty()){
                testRecord();
            }
        } catch (FileNotFoundException e) {e.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}
    }

    public void testRecord(){
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

                                autoCompleteTextView.setText(dataSnapshot.getValue(String.class));
                                DatabaseReference ref3 = databaseReference.child(userPhoneNumber).child("building_string");
                                ref3.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        building.setText(dataSnapshot.getValue(String.class));}
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                DatabaseReference ref4 = databaseReference.child(userPhoneNumber).child("equipment_name_string");
                                ref4.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        eqpName.setText(dataSnapshot.getValue(String.class));}
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                DatabaseReference ref6 = databaseReference.child(userPhoneNumber).child("flat_string");
                                ref6.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        flat.setText(dataSnapshot.getValue(String.class));}
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                DatabaseReference ref7 = databaseReference.child(userPhoneNumber).child("floor_string");
                                ref7.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        floor.setText(dataSnapshot.getValue(String.class));}
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                                DatabaseReference ref8 = databaseReference.child(userPhoneNumber).child("lane_string");
                                ref8.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        lane.setText(dataSnapshot.getValue(String.class));}
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
        }
    }

    @Override
    public void onClick(View v) {
        final String equipment_type_string = EquipmentType_List.equipmentTypeList[eqpType.getSelectedItemPosition()];
        final String equipment_name_string = eqpName.getText().toString();
        final String lane_string = lane.getText().toString();
        final String building_string = building.getText().toString();
        final String floor_string = floor.getText().toString();
        final String flat_string = flat.getText().toString();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                locationThingString = autoCompleteTextView.getText().toString();
                Toast.makeText(getActivity(), locationThingString, Toast.LENGTH_SHORT).show();

                try {
                    FileOutputStream fileOutputStream = getContext()
                            .openFileOutput("pass_location_Name_Info.txt", Context.MODE_PRIVATE);
                    fileOutputStream.write(locationThingString.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {e.printStackTrace();
                } catch (IOException e) {e.printStackTrace();}
            }
        });

        if(v.getId()==R.id.cancelID){
            getDialog().dismiss();
        }

        if(v.getId()==R.id.leftEquipmentSaveButtonID) {
            try {
                FileInputStream fileInputStream = getActivity().openFileInput("pass_location_Name_Info.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String recievedMessage;
                StringBuffer stringBuffer = new StringBuffer();

                while((recievedMessage=bufferedReader.readLine())!=null){
                    stringBuffer.append(recievedMessage);
                }

                locationThing = stringBuffer.toString();
                if(!locationThing.isEmpty()){
                    if (equipment_name_string.isEmpty()) {
                        eqpName.setError("enter equipment name");
                        return;
                    }

                    if (lane_string.isEmpty()) {
                        lane.setError("enter lane no.");
                        return;
                    }

                    if (building_string.isEmpty()) {
                        building.setError("enter apartment no.");
                        return;
                    }

                    if (floor_string.isEmpty()) {
                        floor.setError("enter floor no.");
                        return;
                    }

                    if (flat_string.isEmpty()) {
                        flat.setError("enter flat no.");
                        return;
                    }

                    if (equipment_type_string.equals("Equipment type")) {
                        Toast t = Toast.makeText(getActivity(), "What type of equipment did you lose ?",
                                Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    }

                    else {
                        storeAllUsersEquipmentList(userPhoneNumber, username, equipment_name_string, equipment_type_string,
                                lane_string, building_string, floor_string, flat_string, locationThing);

                        Toast t = Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                        getDialog().dismiss();
                    }
                }

                if(locationThing.isEmpty()){
                    autoCompleteTextView.setError("Set your left equipment's location");
                    return;
                }

            } catch (FileNotFoundException e) {e.printStackTrace();
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    public void storeAllUsersEquipmentList(String userPhoneNumber, String username, String equipment_name_string,
                String equipment_type_string, String lane_string, String building_string,
                String floor_string, String flat_string, String locationThing){

        String Key_User_Info = userPhoneNumber;
        StoreUserEquipmentListData storeUserEquipmentListData =
                new StoreUserEquipmentListData(userPhoneNumber, username, equipment_name_string,
                        equipment_type_string, lane_string, building_string,
                        floor_string, flat_string, locationThing);
        databaseReference.child(Key_User_Info).setValue(storeUserEquipmentListData);
    }
}
