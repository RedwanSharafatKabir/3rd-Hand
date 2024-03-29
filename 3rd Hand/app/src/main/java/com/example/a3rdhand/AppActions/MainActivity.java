package com.example.a3rdhand.AppActions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.a3rdhand.MedicalServiceOrderAndReceive.MedicalServiceActivity;
import com.example.a3rdhand.PackageOrderAndReceive.LeftEquipmentActivity;
import com.example.a3rdhand.PackageOrderAndReceive.LeftEquipmentSavedRecord;
import com.example.a3rdhand.R;
import com.example.a3rdhand.ShoppingOrderAndReceive.ShoppingSavedRecord;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCCustomerInfoInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCProductInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCShipmentInfoInitializer;
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SSLCTransactionResponseListener {

    ImageView imageView;
    TextView name, email, phone;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String userPhoneNumber, location_Thing;
    View hView, parentLayout;
    boolean connected = false;
    Snackbar snackbar;
    FirebaseAuth mAuth;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    LinearLayout linearLayout;

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
        imageView = hView.findViewById(R.id.headerProfileImageID);
        imageView.setOnClickListener(this);
        linearLayout = hView.findViewById(R.id.headerProfileDetailID);
        linearLayout.setOnClickListener(this);

        parentLayout = findViewById(android.R.id.content);

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

        navigationDrawerOpen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getPhotoUrl() != null) {
                    Glide.with(MainActivity.this).load(user.getPhotoUrl().toString()).into(imageView);
                }
            }
        } else { connected = false;
            Toast.makeText(getApplicationContext(), "Turn on internet connection", Toast.LENGTH_SHORT).show(); }
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
                if (user.getPhotoUrl() != null) {
                    Glide.with(MainActivity.this).load(user.getPhotoUrl().toString()).into(imageView);
                }

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
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        switch (id){
            case R.id.leftEquipmentSearchID:
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    checkEuipmentRequest();
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
                return true;

            case R.id.paymentMethodID:
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    paymentMethod();
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
                return true;

            case R.id.requestShopID:
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    ShoppingSavedRecord shoppingSavedRecord = new ShoppingSavedRecord();
                    shoppingSavedRecord.show(getSupportFragmentManager(), "Sample dialog");
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
                return true;

            case R.id.requestMedicalID:
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    MedicalServiceActivity medicalServiceActivity = new MedicalServiceActivity();
                    medicalServiceActivity.show(getSupportFragmentManager(), "Sample dialog");
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
                return true;

            case R.id.feedbackID:
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    FeedbackActivity feedbackActivity = new FeedbackActivity();
                    feedbackActivity.show(getSupportFragmentManager(), "Sample dialog");
                } else {
                    connected = false;
                    snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
                return true;

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
                        mAuth.getInstance().signOut();
                        finish();
                        Intent it = new Intent(MainActivity.this, StartScreen.class);
                        startActivity(it);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
    public void onClick(View v) {
        if(v.getId()==R.id.headerProfileImageID || v.getId()==R.id.headerProfileDetailID){
            ProfileActivity profileActivity = new ProfileActivity();
            profileActivity.show(getSupportFragmentManager(), "Sample dialog");
        }
    }

    public void paymentMethod(){
        final SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization ("3rdha6062afeb302fc", "3rdha6062afeb302fc@ssl",
                10, SSLCCurrencyType.BDT,"123456789098765", "yourProductType", SSLCSdkType.TESTBOX);
        final SSLCCustomerInfoInitializer customerInfoInitializer = new SSLCCustomerInfoInitializer("customer name", "customer email",
                "address", "dhaka", "1214", "Bangladesh", "phoneNumber");
        final SSLCProductInitializer productInitializer = new SSLCProductInitializer ("food", "food",
                new SSLCProductInitializer.ProductProfile.TravelVertical("Travel", "10",
                        "A", "12", "Dhk-Syl"));
        final SSLCShipmentInfoInitializer shipmentInfoInitializer = new SSLCShipmentInfoInitializer ("Courier",
                2, new SSLCShipmentInfoInitializer.ShipmentDetails("AA","Address 1",
                "Dhaka","1000","BD"));
        IntegrateSSLCommerz.getInstance(MainActivity.this)
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .addCustomerInfoInitializer(customerInfoInitializer)
                .addProductInitializer(productInitializer)
                .buildApiCall(this);
    }

    @Override
    public void transactionSuccess(SSLCTransactionInfoModel sslcTransactionInfoModel) {
        Toast.makeText(MainActivity.this, sslcTransactionInfoModel.getAPIConnect() + "---" + sslcTransactionInfoModel.getStatus(), Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void transactionFail(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void merchantValidationError(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();;
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
