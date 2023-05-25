package com.github.siela1915.bootcamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingListManager {

    final ShoppingListHelper dbHelper;

    public ShoppingListManager(Context context) {
        dbHelper = new ShoppingListHelper(context);
    }

    public void addIngredient(String ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String currentDate = getCurrentDate(); // Get the current date

        ContentValues values = new ContentValues();
        values.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT, ingredient);
        values.put(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE, currentDate);

        db.insert(ShoppingListContract.ShoppingListEntry.TABLE_NAME, null, values);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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
        String[] projection = {
                ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT,
                ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE
        };
        Cursor cursor = db.query(
                ShoppingListContract.ShoppingListEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String ingredient = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(ShoppingListContract.ShoppingListEntry.COLUMN_NAME_DATE));

            String itemWithDate = ingredient + " - date: " + date;
            shoppingList.add(itemWithDate);
        }
        cursor.close();
        return shoppingList;
    }



}
