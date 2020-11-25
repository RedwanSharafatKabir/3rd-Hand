package com.example.a3rdhand.ForgotPasswordPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.a3rdhand.R;
import com.example.a3rdhand.UserAuthentication.SigninActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class VerifyChangePasswordByEmail extends AppCompatDialogFragment implements View.OnClickListener {

    Button verifyEmailSubmitButton, resendVerificationEmailButton;
    View view;
    TextView textView;
    String emailObj;
    ProgressDialog dialog;
    boolean connection = false;
    FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_verify_change_passwordby_email, null);

        builder.setView(view);
        setCancelable(false);

        verifyEmailSubmitButton = view.findViewById(R.id.verifyEmailSubmitID);
        verifyEmailSubmitButton.setOnClickListener(this);
        resendVerificationEmailButton = view.findViewById(R.id.resendVerificationEmailID);
        resendVerificationEmailButton.setOnClickListener(this);

        textView = view.findViewById(R.id.checkEmailTextID);
        getEmailFromFile();
        textView.setText("Check " + emailObj + " and change password.");
        mAuth = FirebaseAuth.getInstance();
        sendEmail(emailObj);

        return builder.create();
    }

    private void sendEmail(String emailObj) {
        dialog = ProgressDialog.show(getActivity(), "Submitting",
                "Please wait.......", true);

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connection = true;
            mAuth.sendPasswordResetEmail(emailObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast t = Toast.makeText(getActivity(), R.string.codemsg, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();

                    } else {
                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });
        } else {
            connection = false;
            Toast.makeText(getActivity(), "Internet connection lost", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.verifyEmailSubmitID) {
            SigninActivity signinActivity = new SigninActivity();
            signinActivity.show(getFragmentManager(), "Sample dialog");
            getDialog().dismiss();
        }

        if(v.getId()==R.id.resendVerificationEmailID) {
            sendEmail(emailObj);
        }
    }

    private void getEmailFromFile() {
        try {
            FileInputStream fileInputStream = getContext().openFileInput("emailAddress.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String recievedMessage;
            while((recievedMessage = bufferedReader.readLine())!=null){
                stringBuffer.append(recievedMessage);
            }
            emailObj = stringBuffer.toString();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Email not found. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
