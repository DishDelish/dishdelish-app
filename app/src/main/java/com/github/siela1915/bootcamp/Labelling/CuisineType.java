package com.github.siela1915.bootcamp.Labelling;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CuisineType {
    NONE("None"), AMERICAN("American"), CHINESE("Chinese"), INDIAN("Indian"), LIBYAN("Libyan"),
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
    public static String[] getAll(){
        List<CuisineType> allCuisine= Arrays.asList(CuisineType.values());
        String[] all= new String[allCuisine.size()];
        for(int i= 0; i<allCuisine.size();i++){
            all[i]=allCuisine.get(i).display_string;
        }
        return all;
    }
    public static CuisineType fromString(String text) {
        for (CuisineType b : CuisineType.values()) {
            if (b.display_string.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public static CuisineType fromInt(Integer i){
        return CuisineType.values()[i];
    }

    public static List<CuisineType> fromIntList(List<Integer> l){
        return l.stream().map(i -> CuisineType.values()[i]).collect(Collectors.toList());
    }
}



