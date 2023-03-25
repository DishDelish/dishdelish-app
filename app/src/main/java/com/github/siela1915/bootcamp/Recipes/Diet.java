package com.github.siela1915.bootcamp.Recipes;

public enum Diet {
    LOW_FAT("Low fat"), LOW_CALORIE("Low calorie"), NON_VEG("Non-veg"), VEGETARIAN("Vegetarian ") ;
    private final String display_diet;

    Diet(String s) {
        display_diet=s;
    }
    @Override
    public String toString(){
        return display_diet;
    }

    public static String[] getAll(){
        String[] res= {LOW_FAT.display_diet, LOW_CALORIE.display_diet, NON_VEG.display_diet, VEGETARIAN.display_diet};
        return res;
    }
}
