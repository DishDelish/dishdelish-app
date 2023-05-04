package com.github.siela1915.bootcamp.Labelling;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

}