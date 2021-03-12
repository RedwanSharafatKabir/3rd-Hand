package com.example.a3rdhand.ShoppingOrderAndReceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.a3rdhand.AppActions.MainActivity;
import com.example.a3rdhand.ModelClass.StoreUserData;
import com.example.a3rdhand.ModelClass.StoreUserShoppingListData;
import com.example.a3rdhand.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShoppingDetailsInput extends AppCompatDialogFragment implements View.OnClickListener {

    EditText itemName, itemQuantity;
    Button saveBtn;
    DatabaseReference databaseReference;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    boolean connected = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.shopping_details_input, null);
        builder.setView(view).setTitle("Create your shopping list");
        setCancelable(false);
        builder.setNegativeButton("Close", (dialog, which) -> {});

        itemName = view.findViewById(R.id.NameID);
        itemQuantity = view.findViewById(R.id.quantityID);
        saveBtn = view.findViewById(R.id.shoppingSaveButtonID);
        saveBtn.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("Shopping List Record of All Users");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String name = itemName.getText().toString();
        String quantity = itemQuantity.getText().toString();
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if(v.getId()==R.id.shoppingSaveButtonID){
            if(name.isEmpty()){
                itemName.setError("Fill this field");
            }

            if(quantity.isEmpty()){
                itemQuantity.setError("Fill this field");
            }

            else {
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    connected = true;
                    StoreUserShoppingData(name, quantity);
                    Toast t = Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();

                    getDialog().dismiss();
                } else {
                    connected = false;
                    Toast t = Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
            }
        }
    }

    public void StoreUserShoppingData(String itemName, String itemQuantity){
        String keyPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        StoreUserShoppingListData storeUserShoppingListData = new StoreUserShoppingListData(itemName, itemQuantity);
        databaseReference.child(keyPhone).child(itemName).setValue(storeUserShoppingListData);
    }
}
