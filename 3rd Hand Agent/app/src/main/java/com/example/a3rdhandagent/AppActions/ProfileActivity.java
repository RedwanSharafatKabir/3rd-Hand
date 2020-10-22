package com.example.a3rdhandagent.AppActions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.a3rdhandagent.ModelClass.StoreAgentData;
import com.example.a3rdhandagent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileActivity extends DialogFragment implements View.OnClickListener{

    View v;
    ProgressDialog dialog;
    boolean connected = false;
    MaskEditText NIDnumberText;
    DatabaseReference databaseReference;
    TextView userPhoneNumberText, regionText, emailText, usernameText, employeeidText;
    Button close;
    ImageView profilePic, uploadProfilePic;
    String username, userphone, usercountry, useremail, usernid, employeeidID, profileImageUrl, image_name;;
    private static final int CHOOSE_IMAGE_REQUEST = 1;
    private static Uri uriProfileImage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_profile, null);
        getDialog().setCanceledOnTouchOutside(false);

        close = v.findViewById(R.id.closeProfileID);
        close.setOnClickListener(this);

        userPhoneNumberText = v.findViewById(R.id.profileActivityPhoneID);
        emailText = v.findViewById(R.id.profileActivityEmailID);
        usernameText = v.findViewById(R.id.profileActivityNameID);
        NIDnumberText = v.findViewById(R.id.profileActivityNIDnumberID);
        regionText = v.findViewById(R.id.profileActivityRegionID);
        employeeidText = v.findViewById(R.id.profileActivityEmployeeID);

        profilePic = v.findViewById(R.id.profileActivityPicID);
        uploadProfilePic = v.findViewById(R.id.uploadProfilePictureID);
        uploadProfilePic.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Agent Information");
        findInfoMethod();

        dialog = new ProgressDialog(getActivity());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(getActivity()).load(user.getPhotoUrl().toString()).into(profilePic);
            }
        }
    }

    public void findInfoMethod(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else { connected = false; }

        if(connected == true) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() != null) {
                    DatabaseReference ref1 = databaseReference.child(user.getDisplayName()).child("phone");
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userPhoneNumberText.setText(" " + dataSnapshot.getValue(String.class));
                            userphone = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref2 = databaseReference.child(user.getDisplayName()).child("username");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usernameText.setText(" " + dataSnapshot.getValue(String.class));
                            username = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref3 = databaseReference.child(user.getDisplayName()).child("country");
                    ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            regionText.setText(" " + dataSnapshot.getValue(String.class));
                            usercountry = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref4 = databaseReference.child(user.getDisplayName()).child("nid");
                    ref4.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                        String str = Long.toString(dataSnapshot.getValue(Long.class));
                            NIDnumberText.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref5 = databaseReference.child(user.getDisplayName()).child("email");
                    ref5.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            emailText.setText(" " + dataSnapshot.getValue(String.class));
                            useremail = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    DatabaseReference ref6 = databaseReference.child(user.getDisplayName()).child("employeeid");
                    ref6.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            employeeidText.setText(" " + dataSnapshot.getValue(String.class));
                            employeeidID = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.closeProfileID){
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
            } else {
                connected = false;
                getDialog().dismiss();
            }

            if(connected == true) {
                usernid = NIDnumberText.getRawText();
                storeMethod(useremail, username, employeeidID, userphone, usercountry, usernid);
                getDialog().dismiss();
            }
        }

        if(v.getId()==R.id.uploadProfilePictureID){
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                connected = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
            } else {
                connected = false;
                Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE_REQUEST && resultCode==RESULT_OK){
            if(data!=null && data.getData()!=null){
                uriProfileImage = data.getData();
                profilePic.setImageURI(uriProfileImage);
                Picasso.get().load(uriProfileImage).into(profilePic);
                uploadImageToFirebase();
            }
        }
    }

    private void uploadImageToFirebase() {
        dialog.setMessage("Uploading.....");
        dialog.show();

        image_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        storageReference = FirebaseStorage.getInstance()
                .getReference("profile images/" + image_name + ".jpg");

        if(uriProfileImage!=null){
            storageReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageUrl = uri.toString();
                            saveUserInfo();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {}
            });
        }
    }

    private void saveUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile;
            profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {}
            });
            dialog.dismiss();
            Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeMethod(String email, String username, String employeeid, String phone,
                            String country, String nid){

        String Key_User_Info = phone;
        StoreAgentData storeAgentData = new StoreAgentData(email, username, employeeid, phone, country, nid);
        databaseReference.child(Key_User_Info).setValue(storeAgentData);
    }
}
