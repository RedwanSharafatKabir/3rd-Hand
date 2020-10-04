package com.example.a3rdhandagent;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskedittext.MaskEditText;

public class ProfileActivity extends DialogFragment implements View.OnClickListener{

    boolean connected = false;
    MaskEditText NIDnumberText;
    DatabaseReference databaseReference;
    TextView userPhoneNumberText, regionText, emailText, usernameText, employeeidText;
    Button close;
    ImageView profilePic;
    String username, userphone, usercountry, useremail, usernid, employeeidID;

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
        employeeidText = v.findViewById(R.id.profileActivityEmployeeID);

        profilePic = v.findViewById(R.id.profileActivityPicID);
        profilePic.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Agent Information");
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else { connected = false; }

        if(connected == true) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() != null) {
                    DatabaseReference ref1 = databaseReference.child(user.getDisplayName()).child("phone");
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userPhoneNumberText.setText(" " + dataSnapshot.getValue(String.class));
                            userphone = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref2 = databaseReference.child(user.getDisplayName()).child("username");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usernameText.setText(" " + dataSnapshot.getValue(String.class));
                            username = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref3 = databaseReference.child(user.getDisplayName()).child("country");
                    ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            regionText.setText(" " + dataSnapshot.getValue(String.class));
                            usercountry = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref4 = databaseReference.child(user.getDisplayName()).child("nid");
                    ref4.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        String str = Long.toString(dataSnapshot.getValue(Long.class));
                            NIDnumberText.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref5 = databaseReference.child(user.getDisplayName()).child("email");
                    ref5.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            emailText.setText(" " + dataSnapshot.getValue(String.class));
                            useremail = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref6 = databaseReference.child(user.getDisplayName()).child("employeeid");
                    ref6.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            employeeidText.setText(" " + dataSnapshot.getValue(String.class));
                            employeeidID = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.closeProfileID){
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
            } else {
                connected = false;
                getDialog().dismiss();
            }

            if(connected == true) {
                usernid = NIDnumberText.getRawText();
                storeMethod(useremail, username, userphone, usercountry, usernid, employeeidID);
                getDialog().dismiss();
            }
        }
    }

    public void storeMethod(String email, String username, String phone,
                            String country, String nid, String employeeid){

        String Key_User_Info = phone;
        StoreAgentData storeAgentData = new StoreAgentData(email, username, phone, country, nid,employeeid);
        databaseReference.child(Key_User_Info).setValue(storeAgentData);
    }
}
