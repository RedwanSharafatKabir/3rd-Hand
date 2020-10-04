package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a3rdhand.ForgotPasswordPackage.Forgot_Password;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import dmax.dialog.SpotsDialog;

public class SigninActivity extends AppCompatDialogFragment implements View.OnClickListener {

    AlertDialog waitingDialog;
    CheckBox checkBox;
    Animation fromTop, fromBottom;
    EditText signinEmailText, signinpasswordText;
    ImageButton signinButton;
    private FirebaseAuth mAuth;
    LinearLayout linearLayoutID;
    String emailObj, passObj, passedString = "Remember me";
    Button close, forgetPass;
    View view;
    boolean connection = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_signin, null);

        builder.setView(view).setTitle("LOGIN");
        setCancelable(false);

        linearLayoutID = view.findViewById(R.id.second);
        signinEmailText = view.findViewById(R.id.loginEmailID);
        signinpasswordText = view.findViewById(R.id.loginpassID);

        forgetPass = view.findViewById(R.id.forgetPassID);
        forgetPass.setOnClickListener(this);
        close = view.findViewById(R.id.closeDialogID1);
        close.setOnClickListener(this);
        signinButton = view.findViewById(R.id.SigninID);
        signinButton.setOnClickListener(this);

        linearLayoutID.setAnimation(fromTop);
        signinButton.setAnimation(fromBottom);
        checkBox = view.findViewById(R.id.rememberCheckBoxID);

        mAuth = FirebaseAuth.getInstance();

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        emailObj = signinEmailText.getText().toString();
        passObj = signinpasswordText.getText().toString();
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();

        if(v.getId()==R.id.SigninID){
            waitingDialog.show();

            if (emailObj.isEmpty()) {
                signinEmailText.setError("Please enter email address");
                waitingDialog.dismiss();
                return;
            }

            if (passObj.isEmpty()) {
                signinpasswordText.setError("Please enter username");
                waitingDialog.dismiss();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(emailObj).matches()) {
                Toast t = Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                signinEmailText.requestFocus();
                waitingDialog.dismiss();
                return;
            }

            if(checkBox.isChecked()){
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connection = true;
                } else {
                    connection = false;
                    Toast.makeText(getActivity(), "wifi or mobile data connection lost", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                }

                if(connection==true){
                rememberMethod(passedString);
                mAuth.signInWithEmailAndPassword(emailObj, passObj).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            waitingDialog.dismiss();

                            getActivity().finish();

                            Intent it = new Intent(getActivity(), MainActivity.class);
                            startActivity(it);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            signinEmailText.setText("");
                            signinpasswordText.setText("");
                        }
                        else {
                            waitingDialog.dismiss();
                            Toast t = Toast.makeText(getActivity(), "Authentication failed\nError : " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }
                });}
            }

            if(!checkBox.isChecked()) {
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connection = true;
                } else {
                    connection = false;
                    Toast.makeText(getActivity(), "Internet connection lost", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                }

                if(connection==true){
                passedString = "";
                setNullDataMethod(passedString);
                mAuth.signInWithEmailAndPassword(emailObj, passObj).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            waitingDialog.dismiss();

                            getActivity().finish();

                            Intent it = new Intent(getActivity(), MainActivity.class);
                            startActivity(it);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            signinEmailText.setText("");
                            signinpasswordText.setText("");
                        } else {
                            waitingDialog.dismiss();
                            Toast t = Toast.makeText(getActivity(), "Authentication failed\nError : " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }
                });}
            }
        }

        if(v.getId()==R.id.closeDialogID1){
            getDialog().dismiss();
            waitingDialog.dismiss();
        }

        if(v.getId()==R.id.forgetPassID){
            Forgot_Password forgot_password = new Forgot_Password();
            forgot_password.show(getFragmentManager(), "Sample dialog");
        }
    }

    public void rememberMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput("Personal_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();
            Snackbar.make(view, "Data saved successfully", Snackbar.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNullDataMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput("Personal_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
