package com.example.a3rdhand.ShoppingOrderAndReceive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3rdhand.ModelClass.StoreUserShoppingListData;
import com.example.a3rdhand.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingDetailsAdpter extends RecyclerView.Adapter<ShoppingDetailsAdpter.MyViewHolder> {

    Context context;
    ArrayList<StoreUserShoppingListData> storeUserShoppingData;
    String userPhoneNumber;

    public ShoppingDetailsAdpter(Context context, ArrayList<StoreUserShoppingListData> storeUserShoppingData) {
        this.context = context;
        this.storeUserShoppingData = storeUserShoppingData;
    }

    @NonNull
    @Override
    public ShoppingDetailsAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.shopping_details_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingDetailsAdpter.MyViewHolder holder, int position) {
        int itemPosition = position + 1;
        String selectedItem = storeUserShoppingData.get(position).getItemName();
        holder.itemNumber.setText("Item " + itemPosition);
        holder.itemName.setText("Name: " + storeUserShoppingData.get(position).getItemName());
        holder.itemQuantity.setText("Quantity: " + storeUserShoppingData.get(position).getItemQuantity());

        holder.deleteBtn.setOnClickListener(v -> {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("Are you sure you want to delete this shopping item ?");
            alertDialog.setIcon(R.drawable.exit);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Shopping List Record of All Users")
                        .child(userPhoneNumber).child(selectedItem);
                ref.removeValue().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast t = Toast.makeText(context, "Record deleted", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();

                        dialog.cancel();
                    } else {
                        Toast t = Toast.makeText(context, "Item is not removed", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    }
                });
            });

            alertDialog.setNeutralButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialogBuilder = alertDialog.create();
            alertDialogBuilder.show();
        });
    }

    @Override
    public int getItemCount() {
        return storeUserShoppingData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemNumber, itemName, itemQuantity;
        ImageView deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.itemNumberId);
            itemName = itemView.findViewById(R.id.itemNameId);
            itemQuantity = itemView.findViewById(R.id.itemQuantityId);
            deleteBtn = itemView.findViewById(R.id.deleteItemButton);
        }
    }
}
