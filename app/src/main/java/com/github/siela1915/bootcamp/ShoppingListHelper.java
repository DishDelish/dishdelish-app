package com.github.siela1915.bootcamp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShoppingListHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 1;

    public ShoppingListHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " +
                ShoppingListContract.ShoppingListEntry.TABLE_NAME + " (" +
                ShoppingListContract.ShoppingListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_SHOPPING_LIST_TABLE);
    }

    /**
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ShoppingListContract.ShoppingListEntry.TABLE_NAME);
        onCreate(db);

    }
}
