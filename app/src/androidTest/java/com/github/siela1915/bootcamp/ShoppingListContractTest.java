package com.github.siela1915.bootcamp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShoppingListContractTest {

    @Test
    public void testShoppingListContract() {
        assertEquals("shopping_list", ShoppingListContract.ShoppingListEntry.TABLE_NAME);
        assertEquals("ingredient", ShoppingListContract.ShoppingListEntry.COLUMN_NAME_INGREDIENT);
    }
}
