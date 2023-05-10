package com.github.siela1915.bootcamp;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapterTest {
    private List<String> items= new ArrayList<>();
    private Context context;

    public void setItem(String item) {
        this.items.add(item);
    }


    @Test
    public void getITemCountReturnsCorrectNumberOfElements(){
        setItem("item1");

        ShoppingListAdapter shoppingListAdapter= new ShoppingListAdapter(items,null);
        assertEquals(shoppingListAdapter.getItemCount(),1);
    }
    @Test
    public void getItemCountReturnsZeroWithEmptyList(){
        ShoppingListAdapter shoppingListAdapter = new ShoppingListAdapter(items,null);
        assertEquals(0,shoppingListAdapter.getItemCount());
    }
}
