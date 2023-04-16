package com.github.siela1915.bootcamp.Labelling;

//Diet types, also includes food intolerances
public enum DietType {
    KETO("Keto"), VEGAN("Vegan"), VEGETARIAN("Vegetarian"), PALEO("Paleo"),
    GLUTEN("Gluten-free"), HISTAMINE("Histamine-free"), DAIRY("Dairy-free"), SULFITE("Sulfite-free");

    private final String display_string;

    // constructor to set the string
    DietType(String name){display_string = name;}

    @Override
    public String toString() {
        return display_string;
    }

    /**
     * returns the enum matching the display string or null if no such enum type exists
     * @param str to find the enum of
     * @return the corresponding enum, or null if no such enum found
     */
    public static DietType fromString(String str){
        for (DietType t : DietType.values()) {
            if (t.toString().equalsIgnoreCase(str)) {
                return t;
            }
        }
        return null;
    }
}
