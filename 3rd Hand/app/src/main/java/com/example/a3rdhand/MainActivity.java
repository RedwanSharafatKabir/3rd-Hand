package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Fragment fragment;
    String username, email;
    TextView textView1, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.profileNameID);
        textView2 = findViewById(R.id.profileEmailID);
        drawerLayout = findViewById(R.id.drawerID);
        navigationView = findViewById(R.id.navigationViewID);
        toolbar = findViewById(R.id.toolBarID);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        fragment = new Fragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentID, fragment).commit();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if(user.getEmail()!=null){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("email");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        email = dataSnapshot.getValue(String.class);
//                        textView2.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            if (user.getDisplayName() != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Information")
                        .child(user.getDisplayName()).child("username");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
//                        textView1.setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.paymentMethodID){

        }
        if(menuItem.getItemId()==R.id.helpID){

        }
        if(menuItem.getItemId()==R.id.feedbackID){

        }
        return false;
    }

    @Override
    public void onBackPressed() {
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
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
