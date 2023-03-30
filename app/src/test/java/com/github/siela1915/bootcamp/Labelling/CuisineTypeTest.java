package com.github.siela1915.bootcamp.Labelling;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Test;

public class CuisineTypeTest {

    @Test
    public void getAllReturnsAllCuisineTypeEnums(){
        String[] ct= {"American","Chinese","Indian","Libyan","Mexican","Italian","Greek","French","Caribbean",
                "Asian","European","Nordic","Turkish"};
        assertThat(ct,is(CuisineType.getAll()));
    }

    @Test
    public void toStringReturnsTheName(){
        String  str= "American";
        assertThat(str,is(CuisineType.AMERICAN.toString()));
    }

    @Test
    public void fromStringReturnsTheCorrectEnumFromGivenString(){
        CuisineType ct= CuisineType.AMERICAN;
        Assert.assertEquals(ct, CuisineType.fromString("American"));
    }

}