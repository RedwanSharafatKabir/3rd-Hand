package com.example.a3rdhand.ShoppingOrderAndReceive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.a3rdhand.ModelClass.StoreUserShoppingListData;
import com.example.a3rdhand.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShoppingSavedRecord extends AppCompatDialogFragment implements View.OnClickListener{

    CircleImageView addItem;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    String userPhoneNumber;
    ArrayList<StoreUserShoppingListData> storeUserShoppingArrayList;
    ShoppingDetailsAdpter shoppingDetailsAdpter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_shopping_saved_record, null);

        builder.setView(view).setTitle("Shopping List");
        setCancelable(false);
        builder.setNegativeButton("Close", (dialog, which) -> {});

        addItem = view.findViewById(R.id.addItemId);
        addItem.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recyclerViewID);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference("Shopping List Record of All Users");
        storeUserShoppingArrayList = new ArrayList<StoreUserShoppingListData>();

        return builder.create();
    }

    @Override
    public void onStart() {
        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        try {
            DatabaseReference ref = databaseReference.child(userPhoneNumber);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    storeUserShoppingArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        StoreUserShoppingListData storeUserShoppingListData = dataSnapshot1.getValue(StoreUserShoppingListData.class);
                        storeUserShoppingArrayList.add(storeUserShoppingListData);
                    }

                    Collections.reverse(storeUserShoppingArrayList);
                    shoppingDetailsAdpter = new ShoppingDetailsAdpter(getActivity(), storeUserShoppingArrayList);
                    recyclerView.setAdapter(shoppingDetailsAdpter);
                    shoppingDetailsAdpter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
        }

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.addItemId){
            ShoppingDetailsInput shoppingDetailsInput = new ShoppingDetailsInput();
            shoppingDetailsInput.show(getActivity().getSupportFragmentManager(), "Sample Dialog");
        }
    }
}
