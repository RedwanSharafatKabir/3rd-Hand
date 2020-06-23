package com.example.a3rdhand.EquipmentOrderAndReceive;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a3rdhand.R;

public class Requesting_agent_loading_tab extends AppCompatDialogFragment implements View.OnClickListener {

    TextView requestingAgentNameText;
    Button cancelRequestBtn;
    String markertitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_requesting_agent_loading_tab, container, false);
        setCancelable(false);

        requestingAgentNameText = view.findViewById(R.id.requestingAgentNameID);

        cancelRequestBtn = view.findViewById(R.id.cancelRequestID);
        cancelRequestBtn.setOnClickListener(this);

        Bundle mArgs = getArguments();
        markertitle = mArgs.getString("markertitlePass_key");
        requestingAgentNameText.setText("Requesting agent: " + markertitle + " .........");

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.cancelRequestID){
            getDialog().dismiss();
        }
    }
}
