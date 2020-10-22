package com.example.a3rdhand.ForgotPasswordPackage;

import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.a3rdhand.R;

public class VerifyEmail_OTP_Code extends AppCompatDialogFragment implements View.OnClickListener {

    Button verifyEmailSubmitButton, resendVerificationEmailButton;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.activity_verify_email__o_t_p__code, null);

        builder.setView(view);
        setCancelable(false);

        verifyEmailSubmitButton = view.findViewById(R.id.verifyEmailSubmitID);
        verifyEmailSubmitButton.setOnClickListener(this);
        resendVerificationEmailButton = view.findViewById(R.id.resendVerificationEmailID);
        resendVerificationEmailButton.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onStart() {
        Toast t = Toast.makeText(getActivity(), "Verification email is sent to your email address", Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();

        super.onStart();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.verifyEmailSubmitID) {
            getDialog().dismiss();
        }

        if(v.getId()==R.id.resendVerificationEmailID) {
            Forgot_Password forgot_password = new Forgot_Password();
            forgot_password.show(getFragmentManager(), "Sample dialog");

            getDialog().dismiss();
        }
    }
}
