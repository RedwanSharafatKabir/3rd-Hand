package com.example.a3rdhand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LeftEquipmentActivity extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "MyCustomDialog";
    EditText eqpName, lane, building, floor, flat;
    AutoCompleteTextView autoCompleteTextView;
    Button save, cancelButton;
    Spinner eqpType;

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

        return v;
    }

    @Override
    public void onClick(View v) {
        final String equipment_type_string = EquipmentType_List.equipmentTypeList[eqpType.getSelectedItemPosition()];
        final String equipment_name_string = eqpName.getText().toString();
        final String lane_string = lane.getText().toString();
        final String building_string = building.getText().toString();
        final String floor_string = floor.getText().toString();
        final String flat_string = flat.getText().toString();

        if(v.getId()==R.id.cancelID){
            getDialog().dismiss();
        }

        if(v.getId()==R.id.leftEquipmentSaveButtonID) {
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
                Toast t = Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                getDialog().dismiss();
            }
        }
    }
}
