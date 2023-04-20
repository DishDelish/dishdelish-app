package com.github.siela1915.bootcamp.Recipes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import junit.framework.TestCase;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class PreparationTimeTest extends TestCase {
    @Test
    public void getAllReturnAllEnums(){
        String[] prepTime= {"= 1/2 hour","< 1 hour",PreparationTime.LES_THAN_2H.toString(),PreparationTime.LES_THAN_3H.toString()};
        List<String> timeList=Arrays.asList(prepTime);
        assertThat(PreparationTime.getAll(),is(prepTime));
    }

    @Test
    public void test2(){
        String prep= "< 1 hour";
        assertThat(prep, is(PreparationTime.LES_THAN_1H.toString()));
    }



}