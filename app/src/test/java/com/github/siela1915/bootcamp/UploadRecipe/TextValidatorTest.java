package com.github.siela1915.bootcamp.UploadRecipe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TextValidatorTest {
    @Test
    public void testIsTextValid_withNullText_shouldReturnFalse() {
        assertFalse(TextValidator.isTextValid(null));
    }

    @Test
    public void testIsTextValid_withEmptyText_shouldReturnFalse() {
        assertFalse(TextValidator.isTextValid(""));
    }

    @Test
    public void testIsTextValid_withWhitespaceText_shouldReturnFalse() {
        assertFalse(TextValidator.isTextValid("  "));
    }

    @Test
    public void testIsTextValid_withValidText_shouldReturnTrue() {
        assertTrue(TextValidator.isTextValid("hello"));
    }

    @Test
    public void testIsNumberPositive_withInvalidText_shouldReturnFalse() {
        assertFalse(TextValidator.isNumberPositive("abc"));
    }

    @Test
    public void testIsNumberPositive_withNegativeNumber_shouldReturnFalse() {
        assertFalse(TextValidator.isNumberPositive("-1"));
    }

    @Test
    public void testIsNumberPositive_withZero_shouldReturnFalse() {
        assertFalse(TextValidator.isNumberPositive("0"));
    }

    @Test
    public void testIsNumberPositive_withPositiveNumber_shouldReturnTrue() {
        assertTrue(TextValidator.isNumberPositive("10"));
    }
}