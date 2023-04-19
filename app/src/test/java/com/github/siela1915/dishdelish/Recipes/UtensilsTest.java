package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.github.siela1915.bootcamp.Recipes.Utensils;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UtensilsTest {

    @Test
    public void getAfterSetReturnsValue() {
        Utensils utensils = new Utensils();
        List<String> ls = Arrays.asList("fork", "spoon", "knife");
        utensils.setUtensils(ls);
        assertEquals(utensils.getUtensils(), ls);
    }

    @Test
    public void equalsReturnsFalseOnDifferentList() {
        Utensils utensils1 = new Utensils(Arrays.asList("fork", "spoon", "knife"));
        Utensils utensils2 = new Utensils(Arrays.asList("fork", "stick", "knife"));
        assertNotEquals(utensils2, utensils1);
    }

    @Test
    public void equalsReturnsTrueOnIdenticalUtensils() {
        Utensils utensils1 = new Utensils(Arrays.asList("fork", "spoon", "knife"));
        Utensils utensils2 = new Utensils(Arrays.asList("fork", "spoon", "knife"));
        assertEquals(utensils2, utensils1);
    }


    /*
        It is worth noting that the default equals method on List<T> compares the equality
        of each element IN ORDER meaning that if you switch the order of the elements the
        equals method will return false even if the lists contain the same elements.
     */


    @Test
    public void equalsReturnsTrueOnDifferentListOrder() {
        Utensils utensils1 = new Utensils(Arrays.asList("fork", "spoon", "knife"));
        Utensils utensils2 = new Utensils(Arrays.asList("knife", "fork", "spoon"));
        assertEquals(utensils2, utensils1);
    }
}
