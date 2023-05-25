package com.github.siela1915.bootcamp.Labelling;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


import junit.framework.TestCase;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllergyTypeTest{

    @Test
    public void getAllReturnsAllAllergyEnums(){
        String[] at= {"None", "Eggs","Fish","Crustaceans","Tree nuts","Peanuts","Wheat","Soybeans","Sesame","Milk"};
        assertThat(at, is(AllergyType.getAll()));
    }

    @Test
    public void fromStringReturnTheCorrectEnumFromGivenString(){
        AllergyType allergyType= AllergyType.EGGS;
        assertEquals(allergyType,AllergyType.fromString("Eggs"));
    }

    @Test
    public void fromIntReturnsCorrectType(){
        for(int i = 0; i < AllergyType.values().length; i++){
            assertEquals(AllergyType.values()[i], AllergyType.fromInt(i));
        }
    }

    @Test
    public void fromIntListReturnsCorrectTypes(){
        List<Integer> l = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
        assertEquals(Arrays.stream(AllergyType.values()).collect(Collectors.toList()), AllergyType.fromIntList(l));
    }
}