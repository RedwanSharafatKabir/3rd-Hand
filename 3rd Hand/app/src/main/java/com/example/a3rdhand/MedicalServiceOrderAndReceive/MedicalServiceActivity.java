package com.example.a3rdhand.MedicalServiceOrderAndReceive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3rdhand.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicalServiceActivity extends AppCompatDialogFragment {

    String diseaseNames[];
    GridView gridView;
    int [] diseaseImages = {R.drawable.broken_bone, R.drawable.blood, R.drawable.breath_problem, R.drawable.heart_attack, R.drawable.severe_pain,
            R.drawable.stroke, R.drawable.emergency_delivery, R.drawable.burn, R.drawable.electrocution, R.drawable.choking,
            R.drawable.seizures, R.drawable.eye_trauma, R.drawable.poison, R.drawable.pediatric, R.drawable.coronavirus};
    TextView previousDisease;
    DatabaseReference databaseReference;
    ImageView deleteDisease;
    FrameLayout frameLayout;
    String userPhoneNumber;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("Medical Request Record of All Users");
        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        frameLayout = view.findViewById(R.id.frameLayoutId);
        frameLayout.setVisibility(View.INVISIBLE);
        previousDisease = view.findViewById(R.id.previousDiseaseId);
        try {
            databaseReference.child(userPhoneNumber).child("diseaseName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    frameLayout.setVisibility(View.VISIBLE);
                    previousDisease.setText("Past: " + snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    frameLayout.setVisibility(View.INVISIBLE);
                }
            });
        } catch (Exception e){
            frameLayout.setVisibility(View.INVISIBLE);
        }

        deleteDisease = view.findViewById(R.id.deleteDiseaseButton);
        deleteDisease.setOnClickListener(v -> {
            databaseReference.child(userPhoneNumber).child("diseaseName").removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    frameLayout.setVisibility(View.INVISIBLE);
                    Toast t = Toast.makeText(getActivity(), "Item deleted", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    frameLayout.setVisibility(View.INVISIBLE);
                }
            });
        });

        return builder.create();
    }
}
