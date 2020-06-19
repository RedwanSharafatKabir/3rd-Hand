package com.example.a3rdhand;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{

    Button signin, register;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser = null;
    String passedString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_screen);

        signin = findViewById(R.id.signinPageID);
        register = findViewById(R.id.signupPageID);
        signin.setOnClickListener(this);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(v.getId()==R.id.signinPageID){
            rememberMeMethod();
            if (firebaseUser != null && !passedString.isEmpty()) {
                finish();
                Intent it = new Intent(StartScreen.this, MainActivity.class);
                startActivity(it);
            }
            else if(firebaseUser == null || passedString.isEmpty()){
                SigninActivity signinActivity = new SigninActivity();
                signinActivity.show(getSupportFragmentManager(), "Sample dialog");
            }
        }

        if(v.getId()==R.id.signupPageID){
            SignupActivity signupActivity = new SignupActivity();
            signupActivity.show(getSupportFragmentManager(), "Sample dialog");
        }
    }

    public void rememberMeMethod(){
        try {
            FileInputStream fileInputStream = openFileInput("Personal_Info.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String recievedMessage;
            StringBuffer stringBuffer = new StringBuffer();

            while((recievedMessage=bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            }

            passedString = stringBuffer.toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
                finish();
                System.exit(0);
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
