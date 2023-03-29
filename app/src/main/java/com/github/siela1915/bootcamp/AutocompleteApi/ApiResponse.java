package com.github.siela1915.bootcamp.AutocompleteApi;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class ApiResponse {
    public String name;
    public String image;
    public int id;
    public String aisle;
    public String[] possibleUnits;

    public ApiResponse(String name, String image, int id, String aisle, String[] possibleUnits) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.aisle = aisle;
        this.possibleUnits = possibleUnits;
    }

}
