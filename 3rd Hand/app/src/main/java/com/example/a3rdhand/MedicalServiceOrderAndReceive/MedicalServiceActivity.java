package com.example.a3rdhand.MedicalServiceOrderAndReceive;

import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.a3rdhand.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicalServiceActivity extends AppCompatDialogFragment {

    String diseaseNames[];
    GridView gridView;
    int [] diseaseImages = {R.drawable.broken_bone, R.drawable.blood, R.drawable.breath_problem, R.drawable.heart_attack, R.drawable.severe_pain,
            R.drawable.stroke, R.drawable.emergency_delivery, R.drawable.burn, R.drawable.electrocution, R.drawable.choking,
            R.drawable.seizures, R.drawable.eye_trauma, R.drawable.poison, R.drawable.pediatric, R.drawable.coronavirus};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_medical_service, null);
        builder.setView(view).setTitle("");
        setCancelable(false);
        builder.setNegativeButton("Close", (dialog, which) -> {});

        gridView = view.findViewById(R.id.gridViewID);
        diseaseNames = getResources().getStringArray(R.array.disease_array);

        final MedicalServiceGridViewAdapter adapter = new MedicalServiceGridViewAdapter(getContext(), diseaseImages, diseaseNames);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            String nameValue = diseaseNames[position];
            int imageValue = diseaseImages[position];

            Bundle bundle = new Bundle();
            bundle.putString("diseaseNameKey", nameValue);
            bundle.putInt("diseaseImageKey", imageValue);
            MedicalServiceSaveDiseaseInfo medicalServiceSaveDiseaseInfo = new MedicalServiceSaveDiseaseInfo ();
            medicalServiceSaveDiseaseInfo.setArguments(bundle);
            medicalServiceSaveDiseaseInfo.show(getActivity().getSupportFragmentManager(), "Custom Sheet");
        });

        return builder.create();
    }
}
