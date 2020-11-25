package com.example.a3rdhandagent.AppActions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a3rdhandagent.PaymentSystem.PaymentMethodActivity;
import com.example.a3rdhandagent.R;
import com.firebase.geofire.GeoFire;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigation;
    LinearLayout taskList, paymentMethod, contact, feedback, logout;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    boolean connected = false;
    Snackbar snackbar;
    View parentLayout;
    DatabaseReference databaseReference;
    String agentPhone_Number;
    FirebaseUser agent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = findViewById(R.id.tasklistID);
        taskList.setOnClickListener(this);
        paymentMethod = findViewById(R.id.paymentID);
        paymentMethod.setOnClickListener(this);
        contact = findViewById(R.id.contactID);
        contact.setOnClickListener(this);
        feedback = findViewById(R.id.feedbackID);
        feedback.setOnClickListener(this);
        logout = findViewById(R.id.logoutID);
        logout.setOnClickListener(this);

        bottomNavigation = findViewById(R.id.bottomNavigationID);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.getMenu().setGroupCheckable(0, false, true);

        parentLayout = findViewById(android.R.id.content);

        agent1 = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Agent Current Location");

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
            snackbar = Snackbar.make(parentLayout, "Tap location icon", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Green));
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setDuration(2500).show();

        } else {
            connected = false;
            snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setDuration(10000).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tasklistID:
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }

                if(connected == true) {
//                    TaskListActivity taskListActivity = new TaskListActivity();
//                    taskListActivity.show(getSupportFragmentManager(), "Sample dialog");
                    Toast.makeText(this, "task", Toast.LENGTH_SHORT).show();
                }
                return;

            case R.id.paymentID:
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }

                if(connected == true) {
                    PaymentMethodActivity paymentMethodActivity = new PaymentMethodActivity();
                    paymentMethodActivity.show(getSupportFragmentManager(), "Sample dialog");
                }
                return;

            case R.id.contactID:
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }

                if(connected == true) {
//                    ContactActivity contactActivity = new ContactActivity();
//                    contactActivity.show(getSupportFragmentManager(), "Sample dialog");
                    Toast.makeText(this, "contact", Toast.LENGTH_SHORT).show();
                }
                return;

            case R.id.feedbackID:
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }

                if(connected == true) {
                    FeedbackActivity feedbackActivity = new FeedbackActivity();
                    feedbackActivity.show(getSupportFragmentManager(), "Sample dialog");
                }
                return;

            case R.id.logoutID:
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("LOGOUT ?");
                alertDialogBuilder.setMessage("Your positive decision will make you logged out.");
                alertDialogBuilder.setIcon(R.drawable.exit);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DatabaseReference onlineAvailableAgents = FirebaseDatabase.getInstance().getReference("Online Available Agents")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            onlineAvailableAgents.removeValue();

                            mAuth.getInstance().signOut();
                            finish();
                            Intent it = new Intent(MainActivity.this, StartScreen.class);
                            startActivity(it);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        } catch (Exception e){
                            Toast.makeText(MainActivity.this, "Try later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch(id){
            case R.id.profileID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                ProfileActivity profileActivity = new ProfileActivity();
                profileActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;

            case R.id.helpID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                HelpActivity helpActivity = new HelpActivity();
                helpActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;

            case R.id.aboutID:
                bottomNavigation.getMenu().setGroupCheckable(0, true, true);
                AboutActivity aboutActivity = new AboutActivity();
                aboutActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.alert_title);
        alertDialogBuilder.setMessage(R.string.alert_message);
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (agent1 != null) {
                    if (agent1.getDisplayName() != null) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Agent Information")
                                .child(agent1.getDisplayName()).child("phone");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                agentPhone_Number = dataSnapshot.getValue(String.class);
                                try {
                                    databaseReference.child(agentPhone_Number).removeValue();
                                    finish();
                                    System.exit(0);
                                } catch(Exception e){
                                    finish();
                                    System.exit(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                finish();
                                System.exit(0);
                            }
                        });
                    }
                }
            }
        });

        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
