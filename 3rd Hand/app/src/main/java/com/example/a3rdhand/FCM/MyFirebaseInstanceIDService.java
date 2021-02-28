package com.example.a3rdhand.FCM;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "mFirebaseIIDService";
    private static String SUBSCRIBE_TO;
    private String userPhoneNumber, token;

    @Override
    public void onNewToken(@NonNull String s) {
        token = String.valueOf(FirebaseInstallations.getInstance().getToken(true));

        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                .child(userPhoneNumber).child("username");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SUBSCRIBE_TO = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
        Log.i(TAG, "onTokenRefresh completed with token: " + token);
    }
}
