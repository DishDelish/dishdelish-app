package com.github.siela1915.bootcamp.UploadRecipe;

import java.util.stream.Stream;

public enum TimeUnit {
    MIN("min"), HOUR("hour"), DAY("day"), WEEK("week"), MONTH("month"), YEAR("year");

    private final String display_string;

    // constructor to set the string
    TimeUnit(String name) {display_string = name;}

    public static String[] getAll(){
        TimeUnit[] timeUnits = TimeUnit.values();
        return Stream.of(timeUnits).map(TimeUnit::toString).toArray(String[]::new);
    }

    @Override
    public String toString() {
        return display_string;
    }
}
