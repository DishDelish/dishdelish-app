package com.github.siela1915.bootcamp.UploadRecipe;

public class TextValidator {
    public static boolean isTextValid(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isNumberPositive(String text) {
        boolean isValid = false;
        try {
            isValid = Integer.parseInt(text) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
        return isValid;
    }
}
