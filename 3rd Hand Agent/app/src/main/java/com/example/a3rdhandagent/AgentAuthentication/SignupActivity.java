package com.example.a3rdhandagent.AgentAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Toast;

import com.example.a3rdhandagent.AppActions.MainActivity;
import com.example.a3rdhandagent.R;
import com.example.a3rdhandagent.ModelClass.StoreAgentData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santalu.maskedittext.MaskEditText;

import dmax.dialog.SpotsDialog;

public class SignupActivity extends AppCompatDialogFragment implements View.OnClickListener {

    //    Spinner spinner;
    MaskEditText signupPhoneText;
    Animation fromTop, fromBottom;
    EditText signupEmailText, signupUsernameText, signupPasswordText, signupNIDnumberText, signupEmployeeIdText;
    ImageButton signupButton;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    LinearLayout linearLayout;
    String phonenumber;
    Button close;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_signup, null);

        builder.setView(view).setTitle("SIGN UP");
        builder.setMessage("You can use a contact number only once." +
                "\nUsing a phone number for multiple accounts " +
                "will remove your previous account data.");
        setCancelable(false);

        linearLayout = view.findViewById(R.id.first);
        close = view.findViewById(R.id.closeDialogID);
        close.setOnClickListener(this);

        signupEmailText = view.findViewById(R.id.signupEmailID);
        signupUsernameText = view.findViewById(R.id.signupUsernameID);
        signupPhoneText = view.findViewById(R.id.signupPhoneID);
        signupPasswordText = view.findViewById(R.id.signupPasswordID);
        signupNIDnumberText = view.findViewById(R.id.signupNIDnumberID);
        signupEmployeeIdText = view.findViewById(R.id.signupEmployeeID);
//        spinner = view.findViewById(R.id.spinnerCountriesID);
//        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        signupButton = view.findViewById(R.id.SignupID);
        signupButton.setOnClickListener(this);

        linearLayout.setAnimation(fromTop);
        signupButton.setAnimation(fromBottom);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Agent Information");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
//        final String country = CountryData.countryNames[spinner.getSelectedItemPosition()];
//        final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        final String country = "Bangladesh";
        final String code = "88";
        final String email = signupEmailText.getText().toString();
        final String username = signupUsernameText.getText().toString();
        final String employeeid = signupEmployeeIdText.getText().toString();
        final String phone = signupPhoneText.getText().toString();
        final String password = signupPasswordText.getText().toString();
        final String nid = signupNIDnumberText.getText().toString();

        if(v.getId()==R.id.SignupID) {
            final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
            waitingDialog.show();

            if (email.isEmpty()) {
                waitingDialog.dismiss();
                signupEmailText.setError("Please enter email address");
                return;
            }

            if (username.isEmpty()) {
                waitingDialog.dismiss();
                signupUsernameText.setError("Please enter username");
                return;
            }

            if (employeeid.isEmpty()) {
                waitingDialog.dismiss();
                signupEmployeeIdText.setError("Please enter employee id");
                return;
            }

            if (nid.isEmpty()) {
                waitingDialog.dismiss();
                signupNIDnumberText.setError("Please enter your NID / Birth certificate number");
                return;
            }

            if (phone.isEmpty()) {
                waitingDialog.dismiss();
                signupPhoneText.setError("Please enter your contact number");
                return;
            }

            if (password.isEmpty()) {
                waitingDialog.dismiss();
                signupPasswordText.setError("Please enter password");
                return;
            }

            if (password.length() < 8) {
                waitingDialog.dismiss();
                signupPasswordText.setError("Password must be at least 8 characters");
                return;
            }

            if(phone.length() < 11) {
                waitingDialog.dismiss();
                signupPhoneText.setError("Invalid phone number");
            }

            else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            waitingDialog.dismiss();
                            phonenumber = "+" + code + phone;
                            storeUserDataMethod(email, username, employeeid, phonenumber, country, nid);
                            Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_LONG).show();

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        waitingDialog.dismiss();

                                        getActivity().finish();

                                        Intent it = new Intent(getActivity(), MainActivity.class);
                                        startActivity(it);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    } else {
                                        waitingDialog.dismiss();
                                        Toast t = Toast.makeText(getActivity(), "Authentication failed\nError : "
                                                + "Connection lost.", Toast.LENGTH_LONG);
                                        t.setGravity(Gravity.CENTER, 0, 0);
                                        t.show();
                                    }
                                }
                            });

                            signupEmailText.setText("");
                            signupUsernameText.setText("");
                            signupEmployeeIdText.setText("");
                            signupPhoneText.setText("");
                            signupPasswordText.setText("");

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                waitingDialog.dismiss();
                                Toast t = Toast.makeText(getActivity(), "User is already registered",
                                        Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            } else {
                                waitingDialog.dismiss();
                                Toast t = Toast.makeText(getActivity(), "Authentication failed. Error : "
                                        + "Connection lost.", Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            }
                        }
                    }
                });
            }
        }

        if(v.getId()==R.id.closeDialogID){
            getDialog().dismiss();
        }
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
}
