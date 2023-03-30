package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;

import com.github.siela1915.bootcamp.Labelling.AllergyType;
import com.github.siela1915.bootcamp.Labelling.CuisineType;
import com.github.siela1915.bootcamp.Labelling.DietType;

import org.junit.Test;

public class LabellingTest {

    @Test
    public void toStringMethodsWorkForLabelClasses(){
        for(DietType d : DietType.values()){
            //
            if(!d.toString().contains("-") && !d.name().contains("_")) {
                assertEquals(firstLetterUppercase(d.name()), d.toString());
            }

        }
        for(AllergyType d : AllergyType.values()){
            if(!d.toString().contains("-")&& !d.name().contains("_")) {
                assertEquals(firstLetterUppercase(d.name()), d.toString());
            }
        }
        for(CuisineType d : CuisineType.values()){
            if(!d.toString().contains("-")&& !d.name().contains("_")) {
                assertEquals(firstLetterUppercase(d.name()), d.toString());
            }
        }

    }

    public String firstLetterUppercase(String s){
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
