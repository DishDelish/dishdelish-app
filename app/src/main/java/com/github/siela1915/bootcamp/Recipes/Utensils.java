package com.github.siela1915.bootcamp.Recipes;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

//list of strings
public class Utensils implements Parcelable {
    private List<String> utensils;

    public Utensils(){}
    public Utensils(List<String> utensils) {
        if(utensils == null){
            throw new IllegalArgumentException();
        }
        this.utensils = utensils;
    }
    protected Utensils(Parcel in) {
        utensils = in.createStringArrayList();
    }

    public static final Creator<Utensils> CREATOR = new Creator<Utensils>() {
        @Override
        public Utensils createFromParcel(Parcel in) {
            return new Utensils(in);
        }

        @Override
        public Utensils[] newArray(int size) {
            return new Utensils[size];
        }
    };

    public List<String> getUtensils() {
        return utensils;
    }
    public void setUtensils(List<String> utensils) {
        if(utensils == null){
            throw new IllegalArgumentException();
        }
        this.utensils = utensils;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Utensils) {
            for (String u : ((Utensils) obj).utensils) {
                if (!utensils.contains(u)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return utensils.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringList(utensils);
    }
}
