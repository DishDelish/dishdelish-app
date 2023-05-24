package com.github.siela1915.bootcamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListManager {

    final ShoppingListHelper dbHelper;

    public ShoppingListManager(Context context) {
        dbHelper = new ShoppingListHelper(context);
    }

    public void addIngredient(String ingredient){

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT, ingredient);

        // Insert the new row
        db.insert(ShoppingListContract.ShoppingListEntry.TABLE_NAME, null, values);

    }

    public void removeIngredient(String ingredient){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT + "=?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { ingredient };

        // Issue SQL statement.
        db.delete(ShoppingListContract.ShoppingListEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void clearShoppingList() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ShoppingListContract.ShoppingListEntry.TABLE_NAME, null, null);
    }

    public List<String> getShoppingList() {
        List<String> shoppingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT };
        Cursor cursor = db.query(
                ShoppingListContract.ShoppingListEntry.TABLE_NAME,  // The table to query
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String ingredient = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT));
            shoppingList.add(ingredient);
        }
        cursor.close();
        return shoppingList;
    }



}
