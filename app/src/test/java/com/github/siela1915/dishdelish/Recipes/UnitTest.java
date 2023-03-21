package com.github.siela1915.dishdelish.Recipes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.siela1915.bootcamp.Recipes.Unit;

import org.junit.Test;

public class UnitTest {

    @Test
    public void getAfterSetInfoReturnsCorrectInfo() {
        Unit unit = new Unit();
        unit.setInfo("information");
        assertEquals(unit.getInfo(), "information");
    }

    @Test
    public void getAfterSetValueReturnsCorrectValue() {
        Unit unit = new Unit();
        unit.setValue(5);
        assertEquals(unit.getValue(), 5);
    }

    @Test
    public void toStringReturnsCorrectFormat() {
        Unit unit = new Unit(5, "information");
        assertEquals(unit.toString(), "5 information");
    }

    @Test
    public void equalsReturnsTrueWhenUnitsAreTheSame() {
        Unit unit1 = new Unit(45, "information");
        Unit unit2 = new Unit(45, "information");
        assertTrue(unit1.equals(unit2));
    }

    @Test
    public void equalsReturnsFalseWhenInformationDiffers() {
        Unit unit1 = new Unit(45, "information");
        Unit unit2 = new Unit(45, "information that differs");
        assertFalse(unit1.equals(unit2));
    }

    @Test
    public void equalsReturnsFalseWhenValueDiffers() {
        Unit unit1 = new Unit(45, "information");
        Unit unit2 = new Unit(90, "information");
        assertFalse(unit1.equals(unit2));
    }
}
