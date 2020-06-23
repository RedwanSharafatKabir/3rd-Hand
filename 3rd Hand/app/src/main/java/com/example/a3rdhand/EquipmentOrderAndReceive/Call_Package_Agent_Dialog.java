package com.example.a3rdhand.EquipmentOrderAndReceive;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.a3rdhand.R;

public class Call_Package_Agent_Dialog extends DialogFragment implements View.OnClickListener{

    TextView agentName;
    Button call, reject;
    String markertitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.call_package_agent_dialog, container, false);
        setCancelable(false);

        call = view.findViewById(R.id.callPackageAgentID);
        call.setOnClickListener(this);
        reject = view.findViewById(R.id.rejectPackageAgentID);
        reject.setOnClickListener(this);

        agentName = view.findViewById(R.id.callingPackageAgentNameID);
        Bundle mArgs = getArguments();
        markertitle = mArgs.getString("markertitle_key");
        agentName.setText(markertitle);

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
                Bundle armgs = new Bundle();
                armgs.putString("markertitlePass_key", markertitle);
                Requesting_agent_loading_tab requesting_agent_loading_tab = new Requesting_agent_loading_tab();
                requesting_agent_loading_tab.setArguments(armgs);
                requesting_agent_loading_tab.show(getActivity().getSupportFragmentManager(), "Custom Sheet");

                getDialog().dismiss();

                return;

            case R.id.rejectPackageAgentID:
                getDialog().dismiss();
                return;
        }
    }
}
