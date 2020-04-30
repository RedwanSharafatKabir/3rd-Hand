package com.example.thirdhand;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatDialogFragment implements View.OnClickListener {

    Spinner spinner;
    Animation fromTop, fromBottom;
    EditText signupEmailText, signupUsernameText, signupPhoneText, signupPasswordText;
    ImageButton signupButton;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    LinearLayout linearLayout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_signup, null);

        builder.setView(view).setTitle("SIGN UP");
        builder.setMessage("You can use a contact number only for one account." +
                "\n\nIf you use a phone number for multiple accounts " +
                "your previous data (email, username and password) will be lost.");
        setCancelable(false);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        linearLayout = view.findViewById(R.id.first);

        signupEmailText = view.findViewById(R.id.signupEmailID);
        signupUsernameText = view.findViewById(R.id.signupUsernameID);
        signupPhoneText = view.findViewById(R.id.signupPhoneID);
        signupPasswordText = view.findViewById(R.id.signupPasswordID);

        spinner = view.findViewById(R.id.spinnerCountriesID);
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        signupButton = view.findViewById(R.id.SignupID);
        signupButton.setOnClickListener(this);

        linearLayout.setAnimation(fromTop);
        signupButton.setAnimation(fromBottom);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User Information");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        final String email = signupEmailText.getText().toString();
        final String username = signupUsernameText.getText().toString();
        final String phone = signupPhoneText.getText().toString();
        final String password = signupPasswordText.getText().toString();

        if(v.getId()==R.id.SignupID){
            if (email.isEmpty()) {
                signupEmailText.setError("Please enter email address");
                return;
            }

            if (username.isEmpty()) {
                signupUsernameText.setError("Please enter username");
                return;
            }

            if (phone.isEmpty()) {
                signupPhoneText.setError("Please enter your contact number");
                return;
            }

            if (password.isEmpty()) {
                signupPasswordText.setError("Please enter password");
                return;
            }

            if (password.length() < 8) {
                signupPasswordText.setError("Password must be at least 8 characters");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast t = Toast.makeText(getActivity(), "You will get a code to your number.",
                                Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();

                        String phonenumber = "+" + code + phone;

                        Bundle args = new Bundle();
                        args.putString("phonenumber", phonenumber);
                        PhoneVerificationActivity phoneVerificationActivity = new PhoneVerificationActivity();
                        phoneVerificationActivity.setArguments(args);
                        phoneVerificationActivity.show(getActivity().getSupportFragmentManager(), "Sample dialog");

                        storeUserDataMethod(email, username, phone, password);
                        Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_LONG).show();

                        signupEmailText.setText("");
                        signupUsernameText.setText("");
                        signupPhoneText.setText("");
                        signupPasswordText.setText("");

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast t = Toast.makeText(getActivity(), "User is already registered",
                                    Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        } else {
                            Toast t = Toast.makeText(getActivity(), "Authentication failed. Error : "
                                    + task.getException().getMessage(), Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }
                }
            });
        }
    }

    public void storeUserDataMethod(String email, String username, String phone, String password){
        String Key_User_Info = phone;
        StoreUserData storeUserData = new StoreUserData(email, username, phone, password);
        databaseReference.child(Key_User_Info).setValue(storeUserData);
    }
}
