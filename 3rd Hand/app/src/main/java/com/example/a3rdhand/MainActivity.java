package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.a3rdhand.EquipmentOrderAndReceive.LeftEquipmentActivity;
import com.example.a3rdhand.EquipmentOrderAndReceive.LeftEquipmentSavedRecord;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    ImageView imageView;
    TextView name, email, phone;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String userPhoneNumber, location_Thing;
    View hView;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.TYPE_STATUS_BAR);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerID);
        toolbar = findViewById(R.id.toolBarID);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView = findViewById(R.id.navigationViewID);
        navigationView.setItemIconTintList(null);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.requestShopID);
        hView = navigationView.getHeaderView(0);

        name = hView.findViewById(R.id.profileNameID);
        email = hView.findViewById(R.id.profileEmailID);
        phone = hView.findViewById(R.id.profilePhoneID);
        imageView = hView.findViewById(R.id.profileImageID);

        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, " Login successful", Snackbar.LENGTH_LONG).show();

        navigationDrawerOpen();
    }

    public void navigationDrawerOpen(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else { connected = false; }

        if(connected == true) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getEmail() != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                            .child(user.getDisplayName()).child("email");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            email.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }

                if (user.getDisplayName() != null) {
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                            .child(user.getDisplayName()).child("username");
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("User Information")
                            .child(user.getDisplayName()).child("phone");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            phone.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id){
            case R.id.leftEquipmentSearchID:
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                } else {
                    connected = false;
                    Toast.makeText(MainActivity.this, "Turn on internet connection", Toast.LENGTH_SHORT).show();
                }

                if(connected == true) {
                    checkEuipmentRequest();
                }

                return true;

            case R.id.paymentMethodID:
                PaymentMethodActivity paymentMethodActivity = new PaymentMethodActivity();
                paymentMethodActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;

            case R.id.feedbackID:
                FeedbackActivity feedbackActivity = new FeedbackActivity();
                feedbackActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;

            case R.id.aboutID:
                AboutActivity aboutActivity = new AboutActivity();
                aboutActivity.show(getSupportFragmentManager(), "Sample dialog");
                return true;
        }

        return true;
    }

    public void checkEuipmentRequest(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("phone");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userPhoneNumber = dataSnapshot.getValue(String.class);
                        try {
                            DatabaseReference ref3 = FirebaseDatabase.getInstance()
                                    .getReference("Left Equipment List Record of All Users")
                                    .child(userPhoneNumber).child("locationThing");
                            ref3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    location_Thing = dataSnapshot.getValue(String.class);
                                    try {
                                        if (!location_Thing.isEmpty()) {
                                            LeftEquipmentSavedRecord leftEquipmentSavedRecord = new LeftEquipmentSavedRecord();
                                            leftEquipmentSavedRecord.show(getSupportFragmentManager(), "Sample dialog");
                                        }
                                    } catch (Exception e) {
                                        LeftEquipmentActivity leftEquipmentActivity = new LeftEquipmentActivity();
                                        leftEquipmentActivity.show(getSupportFragmentManager(), "Sample dialog");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        } catch (Exception e){
                            Toast t = Toast.makeText(MainActivity.this, "Record deleted", Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("EXIT !");
        alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
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
