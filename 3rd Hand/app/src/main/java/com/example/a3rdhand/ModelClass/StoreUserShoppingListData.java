package com.example.a3rdhand.ModelClass;

public class StoreUserShoppingListData {
    String itemName;
    String itemQuantity;

    public StoreUserShoppingListData() {}

    public StoreUserShoppingListData(String itemName, String itemQuantity) {
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}
