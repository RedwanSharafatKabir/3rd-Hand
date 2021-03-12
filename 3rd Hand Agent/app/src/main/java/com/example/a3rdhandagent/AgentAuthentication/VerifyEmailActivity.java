package com.example.a3rdhandagent.AgentAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.a3rdhandagent.AppActions.MainActivity;
import com.example.a3rdhandagent.AppActions.StartScreen;
import com.example.a3rdhandagent.ModelClass.StoreAgentData;
import com.example.a3rdhandagent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santalu.maskedittext.MaskEditText;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class VerifyEmailActivity extends AppCompatActivity implements View.OnClickListener  {

    TextView setEmail;
    Button submitOtp, resendOtp;
    MaskEditText otpInputText;
    double randomNumber;
    String randomStringOtpCode, recievedMessage;
    String email, phonenumber, nid, employeeid, username, country, password, phone;
    FirebaseAuth mAuth;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    boolean connected = false;
    Snackbar snackbar;
    View parentLayout;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    FileInputStream fileInputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    StringBuffer stringBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_email);

        otpInputText = findViewById(R.id.otpInputID);
        submitOtp = findViewById(R.id.submitOtpID);
        submitOtp.setOnClickListener(this);
        resendOtp = findViewById(R.id.resendOtpID);
        resendOtp.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBarID);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Agent Information");
        parentLayout = findViewById(android.R.id.content);

        getInfoFromFile();
        setEmail =findViewById(R.id.setEmailID);
        setEmail.setText("A verification code is sent to " + email);
        sendOTP();
    }

    public void sendOTP() {
        randomNumber = Math.random()*100000;
        int randomIntegerNumber = (int)randomNumber;
        randomStringOtpCode = Integer.toString(randomIntegerNumber);
        String mailSubject = "3rd Hand Agent: Verification Code";
        String finalOtpMessage = randomStringOtpCode + " is your OTP verification code.";

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
            JavaMailApi javaMailAPI = new JavaMailApi(email, mailSubject, finalOtpMessage);
            javaMailAPI.execute();

        } else {
            connected = false;
            snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(VerifyEmailActivity.this, R.color.Red));
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setDuration(10000).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.submitOtpID){
            progressBar.setVisibility(View.VISIBLE);
            String code = otpInputText.getText().toString();

            if(randomStringOtpCode.equals(code)){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            storeUserDataMethod(email, username, employeeid, phonenumber, country, nid);
                            Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                        Intent it = new Intent(VerifyEmailActivity.this, MainActivity.class);
                                        startActivity(it);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                task.getException().getMessage(), Toast.LENGTH_LONG);
                                        t.setGravity(Gravity.CENTER, 0, 0);
                                        t.show();
                                    }
                                }
                            });

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressBar.setVisibility(View.GONE);
                                Toast t = Toast.makeText(getApplicationContext(), R.string.email_alert,
                                        Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed. Error : "
                                        + "Connection lost.", Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            }
                        }
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Invalid OTP !", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }

        if(v.getId()==R.id.resendOtpID){
            otpInputText.setText("");
            Toast.makeText(VerifyEmailActivity.this, "New code sent", Toast.LENGTH_LONG).show();
            sendOTP();
        }
    }

    public void getInfoFromFile(){
        try {
            fileInputStream = openFileInput("email_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } email = stringBuffer.toString();

            fileInputStream = openFileInput("username_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } username = stringBuffer.toString();

            fileInputStream = openFileInput("employeeid_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } employeeid = stringBuffer.toString();

            fileInputStream = openFileInput("country_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } country = stringBuffer.toString();

            fileInputStream = openFileInput("nid_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } nid = stringBuffer.toString();

            fileInputStream = openFileInput("phonenumber_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } phone = stringBuffer.toString();
            final String countrycode = "88";
            phonenumber = "+" + countrycode + phone;

            fileInputStream = openFileInput("Password_Info.txt");
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            } password = stringBuffer.toString();

        } catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) { e.printStackTrace();}
    }

    public void storeUserDataMethod(String email, String username, String employeeid, String phone,
                                    String country, String nid){

        String displayname = phone;
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            UserProfileChangeRequest profile;
            profile= new UserProfileChangeRequest.Builder().setDisplayName(displayname).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {}
            });
        }

        String Key_User_Info = phone;
        StoreAgentData storeAgentData = new StoreAgentData(email, username, employeeid, phone, country, nid);
        databaseReference.child(Key_User_Info).setValue(storeAgentData);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent it = new Intent(VerifyEmailActivity.this, StartScreen.class);
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }
}
