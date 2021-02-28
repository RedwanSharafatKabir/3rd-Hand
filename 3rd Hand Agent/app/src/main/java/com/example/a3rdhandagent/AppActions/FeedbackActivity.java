package com.example.a3rdhandagent.AppActions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.example.a3rdhandagent.R;
import com.example.a3rdhandagent.AgentAuthentication.JavaMailApi;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatDialogFragment implements View.OnClickListener{

    private Button btn;
    private String userPhoneNumber, userName, adminEmail="thirdhandsolution3@gmail.com";
    private EditText editText;
    private String mostFriendlyText="", averageFriendlyText="", notFriendlyText="";
    private CheckBox mostFriendly, averageFriendly, notFriendly;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    boolean connected = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_feedback, null);

        builder.setView(view).setTitle("Review App");
        setCancelable(false);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        editText = view.findViewById(R.id.writtenFeedbackId);
        mostFriendly = view.findViewById(R.id.mostFriendlyCheckBoxId);
        averageFriendly = view.findViewById(R.id.averageFriendlyCheckBoxId);
        notFriendly = view.findViewById(R.id.notFriendlyCheckBoxId);
        btn = view.findViewById(R.id.submitFeedbackID);
        btn.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Agent Information")
                .child(userPhoneNumber).child("username");

        String writtenFeedbackText = String.valueOf(editText.getText());
        if(mostFriendly.isChecked()){
            mostFriendlyText = " User interface of the app is most user friendly.";
        }
        if(averageFriendly.isChecked()){
            averageFriendlyText = " User interface of the app is average category.";
        }
        if(notFriendly.isChecked()){
            notFriendlyText = " User interface of the app is not user friendly.";
        }

        if(writtenFeedbackText.isEmpty()){
            editText.setError("Fill this field");
        } else {
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName = snapshot.getValue(String.class);
                        String mailSubject = "Feedback from agent: " + userName + " (" + userPhoneNumber + ")";
                        String mailMessage = (mostFriendlyText + averageFriendlyText + notFriendlyText + "\n" + writtenFeedbackText);
                        JavaMailApi javaMailAPI = new JavaMailApi(adminEmail, mailSubject, mailMessage);
                        javaMailAPI.execute();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            } else {
                connected = false;
                Snackbar snackbar = Snackbar.make(getView(), "Turn on internet connection", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                snackbar.setDuration(10000).show();
            }

            editText.setText("");
        }
    }
}
