package com.github.siela1915.bootcamp.Labelling;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


import junit.framework.TestCase;

import org.junit.Test;

public class AllergyTypeTest extends TestCase {

    @Test
    public void getAllReturnsAllAllergyEnums(){
        String[] at= {"Eggs","Fish","Crustaceans","Tree nuts","Peanuts","Wheat","Soybeans","Sesame","Milk"};
        assertThat(at, is(AllergyType.getAll()));
    }

    @Test
    public void fromStringReturnTheCorrectEnumFromGivenString(){
        AllergyType allergyType= AllergyType.EGGS;
        assertEquals(allergyType,AllergyType.fromString("Eggs"));
    }
}