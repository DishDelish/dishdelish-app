package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.AutocompleteApi.BooleanWrapper;

import org.junit.Test;

public class BooleanWrapperTest {


    @Test
    public void booleanWrapperSettingAndGettingWorks(){
        BooleanWrapper wrapper = new BooleanWrapper(false);

        assertTrue(!wrapper.getBool());

        wrapper.setBool(true);

        assertTrue(wrapper.getBool());
    }
}
