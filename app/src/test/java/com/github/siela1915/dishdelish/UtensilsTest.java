package com.github.siela1915.dishdelish;

import com.github.siela1915.bootcamp.Recipes.Utensils;

import org.junit.Test;

import static org.junit.Assert.*;

import android.os.Parcel;

import java.util.Arrays;

public class UtensilsTest {

    @Test
    public void setUtensilsWorksCorrectly(){
        Utensils ut = new Utensils(Arrays.asList("Pan", "Skillet"));
        ut.setUtensils(Arrays.asList("Pan"));
        assertEquals(Arrays.asList("Pan"), ut.getUtensils());
        assertThrows(IllegalArgumentException.class, ()-> ut.setUtensils(null));
    }

    @Test
    public void toStringWorksCorrectly(){
        Utensils ut = new Utensils(Arrays.asList("Pan", "Skillet"));
        assertEquals(Arrays.asList("Pan", "Skillet").toString(), ut.toString());
    }
}
