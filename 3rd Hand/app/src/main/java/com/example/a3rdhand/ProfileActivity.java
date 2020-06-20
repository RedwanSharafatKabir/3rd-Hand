package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends DialogFragment implements View.OnClickListener{

    DatabaseReference databaseReference;
    TextView userPhoneNumberText, usernameText, regionText, NIDnumberText, emailText;
    Button close;
    ImageView profilePic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, null);
        getDialog().setCanceledOnTouchOutside(false);

        close = v.findViewById(R.id.closeProfileID);
        close.setOnClickListener(this);

        userPhoneNumberText = v.findViewById(R.id.profileActivityPhoneID);
        emailText = v.findViewById(R.id.profileActivityEmailID);
        usernameText = v.findViewById(R.id.profileActivityNameID);
        NIDnumberText = v.findViewById(R.id.profileActivityNIDnumberID);
        regionText = v.findViewById(R.id.profileActivityRegionID);

        profilePic = v.findViewById(R.id.profileActivityPicID);
        profilePic.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("User Information");
        findInfoMethod();

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

    public void findInfoMethod(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = databaseReference.child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhoneNumberText.setText(" " + dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                DatabaseReference ref2 = databaseReference.child(user.getDisplayName()).child("username");
                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usernameText.setText(" " + dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                DatabaseReference ref3 = databaseReference.child(user.getDisplayName()).child("country");
                ref3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        regionText.setText(" " + dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                DatabaseReference ref4 = databaseReference.child(user.getDisplayName()).child("NID");
                ref4.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String str = Long.toString(dataSnapshot.getValue(Long.class));
                        NIDnumberText.setText(" NID: " + str);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                DatabaseReference ref5 = databaseReference.child(user.getDisplayName()).child("email");
                ref5.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        emailText.setText(" " + dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.closeProfileID){
            getDialog().dismiss();
        }
    }
}
