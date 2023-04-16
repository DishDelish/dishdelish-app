package com.github.siela1915.bootcamp.Labelling;

public enum CuisineType {
    AMERICAN("American"), CHINESE("Chinese"), INDIAN("Indian"), LIBYAN("Libyan"),
    MEXICAN("Mexican"), ITALIAN("Italian"), GREEK("Greek"), FRENCH("French"),
    CARIBBEAN("Caribbean"), ASIAN("Asian"), EUROPEAN("European"), NORDIC("Nordic"),
    TURKISH("Turkish");

    private final String display_string;

    // constructor to set the string
    CuisineType(String name){display_string = name;}

    @Override
    public String toString() {
        return display_string;
    }

    /**
     * returns the enum matching the display string or null if no such enum type exists
     * @param str to find the enum of
     * @return the corresponding enum, or null if no such enum found
     */
    public static CuisineType fromString(String str){
        for (CuisineType t : CuisineType.values()) {
            if (t.toString().equalsIgnoreCase(str)) {
                return t;
            }
        }
        return null;
    }
}



