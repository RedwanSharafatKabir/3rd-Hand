package com.example.a3rdhand.ForgotPasswordPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.a3rdhand.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.santalu.maskedittext.MaskEditText;

public class Forgot_Password extends AppCompatDialogFragment implements View.OnClickListener {

    ProgressDialog dialog;
    MaskEditText phoneText;
    EditText emailText;
    Button submit, close;
    View view;
    LinearLayout linearLayoutID;
    Animation fromTop, fromBottom;
    FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_forgot__password, null);

        builder.setView(view).setTitle("Recover account");
        setCancelable(false);

        linearLayoutID = view.findViewById(R.id.second);
        emailText = view.findViewById(R.id.recoverEmailID);
        phoneText = view.findViewById(R.id.recoverPhoneID);

        submit = view.findViewById(R.id.submitID);
        submit.setOnClickListener(this);
        close = view.findViewById(R.id.closeTheDialogID);
        close.setOnClickListener(this);

        linearLayoutID.setAnimation(fromTop);
        submit.setAnimation(fromBottom);

        mAuth = FirebaseAuth.getInstance();

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        final String emailobj = emailText.getText().toString();
        final String phoneobj = phoneText.getText().toString();

        if(v.getId()==R.id.submitID) {
            dialog = ProgressDialog.show(getActivity(), "Submitting",
                    "Please wait.......", true);

            if(!Patterns.EMAIL_ADDRESS.matcher(emailobj).matches()) {
                Toast t = Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                emailText.requestFocus();
                dialog.dismiss();
                return;
            }

            if(emailobj.isEmpty() || phoneobj.isEmpty()){
                emailText.setError("Fill the required field");
                phoneText.setError("Fill the required field");
                dialog.dismiss();
                return;
            }

            else {
                mAuth.sendPasswordResetEmail(emailobj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            VerifyEmail_OTP_Code verifyEmail = new VerifyEmail_OTP_Code();
                            verifyEmail.show(getFragmentManager(), "Sample dialog");

                            getDialog().dismiss();

                        } else {
                            Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }

        if(v.getId()==R.id.closeTheDialogID){
            getDialog().dismiss();
        }
    }
}
