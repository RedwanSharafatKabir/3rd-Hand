package com.example.a3rdhand.EquipmentOrderAndReceive;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.a3rdhand.R;

@SuppressLint("ValidFragment")
public class Call_Package_Agent_Dialog extends DialogFragment implements View.OnClickListener{

    TextView agentName;
    Button call, reject;

    public static Call_Package_Agent_Dialog getInstance() {
        return new Call_Package_Agent_Dialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        call = view.findViewById(R.id.callPackageAgentID);
        call.setOnClickListener(this);
        reject = view.findViewById(R.id.rejectPackageAgentID);
        reject.setOnClickListener(this);

        agentName = view.findViewById(R.id.callingPackageAgentNameID);

        return view;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.callPackageAgentID:
                Toast.makeText(getActivity(), "Called", Toast.LENGTH_SHORT).show();
                return;

            case R.id.rejectPackageAgentID:
                getDialog().dismiss();
                return;
        }
    }
}
