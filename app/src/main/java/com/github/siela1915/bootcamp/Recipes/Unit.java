package com.github.siela1915.bootcamp.Recipes;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

//Number of ingredients needed, different measurements, easily changeable to be avble to change number of servings wanted
//have a set of shared functions for all possible types of units
public class Unit implements Parcelable {

    private int value;
    private String info;

    public Unit(){}
    public Unit(int value, String info) {
        this.value = value;
        this.info = info.toLowerCase();
    }

    protected Unit(Parcel in) {
        value = in.readInt();
        info = in.readString();
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info.toLowerCase();
    }

    @NonNull
    @Override
    public String toString(){
        return value + " " + info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Unit) {
            return value == ((Unit) obj).value && info.equals(((Unit) obj).info);
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeString(info);
    }
}
