package com.github.siela1915.bootcamp.Labelling;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DietTypeTest {
    @Test
    public void getAllReturnsAllEnums(){
        String[] dt={"None", "Keto","Vegan","Vegetarian","Paleo","Gluten-free",
        "Histamine-free","Dairy-free","Sulfite-free"};
        assertThat(dt,is(DietType.getAll()));
    }

    @Test
    public void toStringReturnTheNameOfEnum(){
        String str= DietType.DAIRY.toString();
        assertThat("Dairy-free",is(str));
    }
    @Test
    public void fromStringReturnsTheCorrectEnumFromGivenString(){
        DietType dt= DietType.DAIRY;
        assertEquals(dt,DietType.fromString("Dairy-free"));
    }

    @Test
    public void returnsCorrectViolatingIngredientsList(){
        //None diet type
        assertEquals(0, DietType.getViolatingIngredients(0).size());
        assertEquals(Arrays.asList("rice", "wheat", "oats", "potato", "corn"), DietType.getViolatingIngredients(1));
    }

    @Test
    public void fromIntReturnsCorrectType(){
        for(int i = 0; i < DietType.values().length; i++){
            assertEquals(DietType.values()[i], DietType.fromInt(i));
        }
    }

    @Test
    public void fromIntListReturnsCorrectTypes(){
        List<Integer> l = Arrays.asList(0,1,2,3,4,5,6,7,8);
        assertEquals(Arrays.stream(DietType.values()).collect(Collectors.toList()), DietType.fromIntList(l));
    }

}