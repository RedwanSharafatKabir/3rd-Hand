package com.example.a3rdhand.MedicalServiceOrderAndReceive;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.AppActions.MainActivity;
import com.example.a3rdhand.ModelClass.StoreUserDisease;
import com.example.a3rdhand.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicalServiceSaveDiseaseInfo extends AppCompatDialogFragment implements View.OnClickListener {

    DatabaseReference databaseReference;
    String diseaseName;
    int diseaseImage;
    ImageView imageView;
    Button saveDisease;
    TextView textView;
    NetworkInfo netInfo;
    boolean connected;
    ConnectivityManager cm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_medical_service_saved_disease_info, null);
        builder.setView(view).setTitle("");
        setCancelable(false);
        builder.setNegativeButton("Close", (dialog, which) -> {});

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Request Record of All Users");
        Bundle mArgs = getArguments();
        diseaseName = mArgs.getString("diseaseNameKey");
        diseaseImage = mArgs.getInt("diseaseImageKey");
        imageView = view.findViewById(R.id.imageViewId);
        imageView.setImageResource(diseaseImage);
        saveDisease = view.findViewById(R.id.saveDiseaseId);
        saveDisease.setOnClickListener(this);
        textView = view.findViewById(R.id.textViewId);
        textView.setText("Are you suffering from " + diseaseName + " ?");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if(v.getId()==R.id.saveDiseaseId){
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
                storeUserDiseaseMethod(diseaseName);
            } else {
                connected = false;
                Toast t = Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        }
    }

    public void storeUserDiseaseMethod(String diseaseName){
        String databaseKey = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        StoreUserDisease storeUserDisease = new StoreUserDisease(diseaseName);
        databaseReference.child(databaseKey).setValue(storeUserDisease);

        Toast t = Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
        getDialog().dismiss();
    }
}
