package com.github.siela1915.bootcamp;

import android.provider.BaseColumns;

public class ShoppingListContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ShoppingListContract() {}

    /* Inner class that defines the table contents */
    public static class ShoppingListEntry implements BaseColumns {
        public static final String TABLE_NAME = "shopping_list";
        public static final String COLUMN_NAME_INGREDIENT = "ingredient";
    }
}
