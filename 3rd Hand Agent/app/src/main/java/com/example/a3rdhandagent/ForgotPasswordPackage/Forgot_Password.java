package com.example.a3rdhandagent.ForgotPasswordPackage;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.a3rdhandagent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.santalu.maskedittext.MaskEditText;
import java.io.FileOutputStream;

public class Forgot_Password extends AppCompatDialogFragment implements View.OnClickListener {

    ProgressDialog dialog;
    MaskEditText phoneText;
    EditText emailText;
    Button submit, close;
    View view;
    LinearLayout linearLayoutID;
    Animation fromTop, fromBottom;
    FirebaseAuth mAuth;
    boolean connection = false;

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
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connection = true;
                    dialog.dismiss();
                    sendEmailByFile(emailobj);
                    VerifyChangePasswordByEmail verifyEmail = new VerifyChangePasswordByEmail();
                    verifyEmail.show(getFragmentManager(), "Sample dialog");
                    getDialog().dismiss();
                } else {
                    connection = false;
                    Toast.makeText(getActivity(), "Internet connection lost", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }

        if(v.getId()==R.id.closeTheDialogID){
            getDialog().dismiss();
        }
    }

    public void sendEmailByFile(String emailobj){
        try {
            FileOutputStream fileOutputStream = getContext().openFileOutput("emailAddress.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(emailobj.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Email not found. "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
