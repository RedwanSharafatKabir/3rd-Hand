package com.example.a3rdhand.EquipmentOrderAndReceive;

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
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.a3rdhand.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeftEquipmentActivity extends DialogFragment implements View.OnClickListener{

    EditText eqpName, lane, building, floor, flat, autoCompleteTextView;
    Button save, cancelButton;
    Spinner eqpType;
    DatabaseReference databaseReference;
    String userPhoneNumber, username;

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
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
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
        final String locationThing = autoCompleteTextView.getText().toString();

        if(v.getId()==R.id.cancelID){
            getDialog().dismiss();
        }

        if(v.getId()==R.id.leftEquipmentSaveButtonID) {
            if (equipment_name_string.isEmpty()) {
                eqpName.setError("enter equipment name");
                return;
            }

            if(locationThing.isEmpty()){
                autoCompleteTextView.setError("Set your left equipment's place");
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
