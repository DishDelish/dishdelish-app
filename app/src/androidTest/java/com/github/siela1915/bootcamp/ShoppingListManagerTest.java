package com.github.siela1915.bootcamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ShoppingListManagerTest {

    private ShoppingListManager manager;

    @Before
    public void createManager() {
        Context context = ApplicationProvider.getApplicationContext();
        manager = new ShoppingListManager(context);
    }

    @After
    public void closeDb(){
        manager.dbHelper.close();
    }

    @Test
    public void addIngredientAddsItemsToShoppingList(){
        manager.addIngredient("Candy");
        manager.addIngredient("Sugar");

        List<String> shoppingList = manager.getShoppingList();

        assertTrue(shoppingList.contains("Candy"));
        assertTrue(shoppingList.contains("Sugar"));

    }

    @Test
    public void removeIngredientRemovesItemsFromShoppingList(){

        manager.addIngredient("Candy");
        manager.addIngredient("Sugar");

        manager.removeIngredient("Sugar");

        List<String> shoppingList = manager.getShoppingList();

        assertFalse(shoppingList.contains("Sugar"));
        assertTrue(shoppingList.contains("Candy"));

    }

    @Test
    public void upgradeDatabaseRestartsDB() {
        // Add some data to the database
        manager.addIngredient("Candy");
        manager.addIngredient("Sugar");

        // Upgrade the database version
        ShoppingListHelper dbHelper = new ShoppingListHelper(ApplicationProvider.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 2, 2);

        Cursor cursor = db.query(ShoppingListContract.ShoppingListEntry.TABLE_NAME,
                null, null, null, null, null, null);

        assertEquals(0, cursor.getCount());
    }




}
