package com.example.a3rdhand.AppActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
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
import com.bumptech.glide.Glide;
import com.example.a3rdhand.ModelClass.StoreUserData;
import com.example.a3rdhand.ModelClass.StoreUserImageUrlData;
import com.example.a3rdhand.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class ProfileActivity extends DialogFragment implements View.OnClickListener{

    View view;
    ProgressDialog dialog;
    boolean connected = false;
    MaskEditText NIDnumberText;
    DatabaseReference databaseReference, databaseReference2;
    TextView userPhoneNumberText, regionText, emailText, usernameText;
    Button close;
    ImageView profilePic, uploadProfilePic;
    String username, userphone, usercountry, useremail, usernid, profileImageUrl, image_name;
    AlertDialog waitingDialog;
    private static final int CHOOSE_IMAGE_REQUEST = 1;
    private static Uri uriProfileImage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, null);
        getDialog().setCanceledOnTouchOutside(false);

        close = view.findViewById(R.id.closeProfileID);
        close.setOnClickListener(this);

        userPhoneNumberText = view.findViewById(R.id.profileActivityPhoneID);
        emailText = view.findViewById(R.id.profileActivityEmailID);
        usernameText = view.findViewById(R.id.profileActivityNameID);
        NIDnumberText = view.findViewById(R.id.profileActivityNIDnumberID);
        regionText = view.findViewById(R.id.profileActivityRegionID);

        profilePic = view.findViewById(R.id.profileActivityPicID);
        uploadProfilePic = view.findViewById(R.id.uploadProfilePictureID);
        uploadProfilePic.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("User Information");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User Image URL");
        findInfoMethod();
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        dialog = new ProgressDialog(getActivity());

        return view;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getPhotoUrl() != null) {
                    Glide.with(getActivity()).load(user.getPhotoUrl().toString()).into(profilePic);
                }
            }
        } else { connected = false;
            Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_SHORT).show(); }
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    DatabaseReference ref5 = databaseReference.child(user.getDisplayName()).child("email");
                    ref5.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            emailText.setText(" " + dataSnapshot.getValue(String.class));
                            useremail = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
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
                storeMethod(useremail, username, userphone, usercountry, usernid);
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
                Snackbar.make(view, "Turn on internet connection", Snackbar.LENGTH_LONG)
                        .getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red));
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
            profile= new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {}
            });

            storeImageMethod(profileImageUrl);
            dialog.dismiss();
            Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeMethod(String email, String username, String phone,
                            String country, String nid){

        String Key_User_Info = phone;
        StoreUserData storeUserData = new StoreUserData(email, username, phone, country, nid);
        databaseReference.child(Key_User_Info).setValue(storeUserData);
    }

    public void storeImageMethod(String profileImageUrl){
        StoreUserImageUrlData storeUserImageUrlData = new StoreUserImageUrlData(profileImageUrl);
        databaseReference2.child(image_name).setValue(storeUserImageUrlData);
    }
}
