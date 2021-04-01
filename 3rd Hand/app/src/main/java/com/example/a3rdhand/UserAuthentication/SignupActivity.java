package com.example.a3rdhand.UserAuthentication;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.a3rdhand.AppActions.MainActivity;
import com.example.a3rdhand.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.santalu.maskedittext.MaskEditText;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import dmax.dialog.SpotsDialog;

public class SignupActivity extends AppCompatDialogFragment implements View.OnClickListener {

//    Spinner spinner;
    MaskEditText signupPhoneText;
    Animation fromTop, fromBottom;
    EditText signupEmailText, signupUsernameText, signupPasswordText, signupNIDnumberText;
    ImageButton signupButton;
    LinearLayout linearLayout;
    Button close;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    boolean connected = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fromTop = AnimationUtils.loadAnimation(getActivity(), R.anim.fromtoptobottom);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.frombottomtotop);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_signup, null);

        builder.setView(view).setTitle("SIGN UP");
        builder.setMessage("Create account with Gmail.\nYou can use a contact number only once." +
                "\nOne contact number for multiple accounts " +
                "will remove your first account data.");
        setCancelable(false);

        linearLayout = view.findViewById(R.id.first);
        close = view.findViewById(R.id.closeDialogID);
        close.setOnClickListener(this);

        signupEmailText = view.findViewById(R.id.signupEmailID);
        signupUsernameText = view.findViewById(R.id.signupUsernameID);
        signupPhoneText = view.findViewById(R.id.signupPhoneID);
        signupPasswordText = view.findViewById(R.id.signupPasswordID);
        signupNIDnumberText = view.findViewById(R.id.signupNIDnumberID);
//        spinner = view.findViewById(R.id.spinnerCountriesID);
//        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        signupButton = view.findViewById(R.id.SignupID);
        signupButton.setOnClickListener(this);

        linearLayout.setAnimation(fromTop);
        signupButton.setAnimation(fromBottom);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
//        final String country = CountryData.countryNames[spinner.getSelectedItemPosition()];
//        final String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
        final String country = "Bangladesh";
        final String email = signupEmailText.getText().toString();
        final String username = signupUsernameText.getText().toString();
        final String phone = signupPhoneText.getText().toString();
        final String password = signupPasswordText.getText().toString();
        final String nid = signupNIDnumberText.getText().toString();
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

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
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    waitingDialog.dismiss();
                    sendInfoToFile(email, username, phone, country, nid, password);
                    Intent it = new Intent(getActivity(), VerifyEmailActivity.class);
                    startActivity(it);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    signupEmailText.setText("");
                    signupUsernameText.setText("");
                    signupNIDnumberText.setText("");
                    signupPhoneText.setText("");
                    signupPasswordText.setText("");
                } else {
                    connected = false;
                    Snackbar snackbar = Snackbar.make(getView(), "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
            }
        }

        if(v.getId()==R.id.closeDialogID){
            getDialog().dismiss();
        }
    }

    public void sendInfoToFile(String email, String username, String phonenumber,
                               String country, String nid, String password){
        try {
            FileOutputStream fileOutputStream1 = getContext().openFileOutput("email_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream1.write(email.getBytes());
            fileOutputStream1.close();

            FileOutputStream fileOutputStream2 = getContext().openFileOutput("username_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream2.write(username.getBytes());
            fileOutputStream2.close();

            FileOutputStream fileOutputStream4 = getContext().openFileOutput("phonenumber_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream4.write(phonenumber.getBytes());
            fileOutputStream4.close();

            FileOutputStream fileOutputStream5 = getContext().openFileOutput("country_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream5.write(country.getBytes());
            fileOutputStream5.close();

            FileOutputStream fileOutputStream6 = getContext().openFileOutput("nid_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream6.write(nid.getBytes());
            fileOutputStream6.close();

            FileOutputStream fileOutputStream7 = getContext().openFileOutput("Password_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream7.write(password.getBytes());
            fileOutputStream7.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }
}
