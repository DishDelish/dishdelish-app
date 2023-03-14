package com.github.siela1915.bootcamp.Labelling;

public enum CuisineType {
    AMERICAN("American"), CHINESE("Chinese"), INDIAN("Indian"), LIBYAN("Libyan"),
    MEXICAN("Mexican"), ITALIAN("Italian"), GREEK("Greek"), FRENCH("French"),
    CARIBBEAN("Caribbean"), ASIAN("Asian"), EUROPEAN("European"), NORDIC("Nordic"),
    TURKISH("Turkish");

    private final String display_string;

    // constructor to set the string
    CuisineType(String name){display_string = name;}

    // toString returns the display string
    @Override
    public String toString() {
        return display_string;
    }
}



