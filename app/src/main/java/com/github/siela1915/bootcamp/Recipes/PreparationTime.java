package com.github.siela1915.bootcamp.Recipes;

public enum PreparationTime {
    HALF_HOUR("= 1/2 hour"),LES_THAN_1H("< 1 hour"), LES_THAN_2H("< 2 hours"), LES_THAN_3H("< 3 ");
    private final String prepTimeDisplay;

    PreparationTime(String s) {
        prepTimeDisplay=s;
    }

    @Override
    public String toString(){
        return prepTimeDisplay;
    }

    public static String[] getAll(){
        String [] res= {HALF_HOUR.prepTimeDisplay,LES_THAN_1H.prepTimeDisplay, LES_THAN_2H.prepTimeDisplay, LES_THAN_3H.prepTimeDisplay};
        return res;
    }
}
