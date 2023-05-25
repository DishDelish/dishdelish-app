package com.github.siela1915.bootcamp.Labelling;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CuisineTypeTest {

    @Test
    public void getAllReturnsAllCuisineTypeEnums(){
        String[] ct= {"None", "American","Chinese","Indian","Libyan","Mexican","Italian","Greek","French","Caribbean",
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

    @Test
    public void fromIntReturnsCorrectType(){
        for(int i = 0; i < CuisineType.values().length; i++){
            assertEquals(CuisineType.values()[i], CuisineType.fromInt(i));
        }
    }

    @Test
    public void fromIntListReturnsCorrectTypes(){
        List<Integer> l = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13);
        assertEquals(Arrays.stream(CuisineType.values()).collect(Collectors.toList()), CuisineType.fromIntList(l));
    }

}