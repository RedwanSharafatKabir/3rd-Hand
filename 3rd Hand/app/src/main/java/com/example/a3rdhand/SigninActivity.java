package com.example.a3rdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import dmax.dialog.SpotsDialog;

public class SigninActivity extends AppCompatDialogFragment implements View.OnClickListener {

    //    AllianceLoader allianceLoader;
    Animation fromTop, fromBottom;
    EditText signinEmailText, signinpasswordText;
    ImageButton signinButton;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    LinearLayout linearLayoutID;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_signin, null);

        builder.setView(view).setTitle("LOGIN");
        setCancelable(false);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        linearLayoutID = view.findViewById(R.id.second);
//        allianceLoader = view.findViewById(R.id.AllianceLoaderID);
        signinEmailText = view.findViewById(R.id.loginEmailID);
        signinpasswordText = view.findViewById(R.id.loginpassID);

        signinButton = view.findViewById(R.id.SigninID);
        signinButton.setOnClickListener(this);

        linearLayoutID.setAnimation(fromTop);
        signinButton.setAnimation(fromBottom);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User Information");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String emailObj = signinEmailText.getText().toString();
        String passObj = signinpasswordText.getText().toString();

        if(v.getId()==R.id.SigninID){
//            allianceLoader.setVisibility(VISIBLE);
            final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
            waitingDialog.show();

            if (emailObj.isEmpty()) {
                signinEmailText.setError("Please enter email address");
//                allianceLoader.setVisibility(INVISIBLE);
                waitingDialog.dismiss();
                return;
            }

            if (passObj.isEmpty()) {
                signinpasswordText.setError("Please enter username");
//                allianceLoader.setVisibility(INVISIBLE);
                waitingDialog.dismiss();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(emailObj).matches()) {
                Toast t = Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
                signinEmailText.requestFocus();
//                allianceLoader.setVisibility(INVISIBLE);
                waitingDialog.dismiss();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailObj, passObj).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
//                        allianceLoader.setVisibility(INVISIBLE);
                        waitingDialog.dismiss();
                        Toast toast = Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        getActivity().finish();

                        Intent it = new Intent(getActivity(), MainActivity.class);
                        startActivity(it);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        signinEmailText.setText("");
                        signinpasswordText.setText("");
                    }
                    else {
//                        allianceLoader.setVisibility(INVISIBLE);
                        waitingDialog.dismiss();
                        Toast t = Toast.makeText(getActivity(), "Authentication failed\nError : " +
                                task.getException().getMessage(), Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    }
                }
            });
        }
    }
}
