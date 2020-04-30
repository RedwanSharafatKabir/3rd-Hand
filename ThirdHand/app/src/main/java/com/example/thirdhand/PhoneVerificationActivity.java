package com.example.thirdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;

import android.widget.Spinner;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatDialogFragment {

    EditText verificationCodeEditText;
    Button verifyButton;
    Animation fromTop, fromBottom;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String Code, phoneNumber, verificationCodeID;
    ProgressBar progressBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_phone_verification, null);

        builder.setView(view).setTitle("CONFIRM VERIFICATION");
        builder.setCancelable(false);

        verificationCodeEditText = view.findViewById(R.id.verificationCodeInputID);

        verifyButton = view.findViewById(R.id.VerifyID);

        verificationCodeEditText.setAnimation(fromTop);
        verifyButton.setAnimation(fromBottom);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User Information");
        progressBar = view.findViewById(R.id.progressBarID);

        Bundle mArgs = getArguments();
        phoneNumber = mArgs.getString("phonenumber");
        sendVerificationMethod(phoneNumber);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Code = verificationCodeEditText.getText().toString();
                if((Code.isEmpty()) || (Code.length()<5)){
                    verificationCodeEditText.setError("Enter valid code.");
                    verificationCodeEditText.requestFocus();
                    return;
                }

                verifyCodeMethod(Code);
            }
        });

        return builder.create();
    }

    public void verifyCodeMethod(String Code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeID, Code);
        signinWithCredential(credential);
    }

    public void signinWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    getActivity().finish();

                    Intent it = new Intent(getActivity(), MainActivity.class);
                    startActivity(it);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    verificationCodeEditText.setText("");
                } else {
                    Toast t = Toast.makeText(getActivity(), "Authentication failed\nError : " +
                            task.getException().getMessage(), Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
            }
        });
    }

    public void sendVerificationMethod(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeID = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String Code = phoneAuthCredential.getSmsCode();
            if(Code != null){
                progressBar.setVisibility(View.VISIBLE);
                verifyCodeMethod(Code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}
