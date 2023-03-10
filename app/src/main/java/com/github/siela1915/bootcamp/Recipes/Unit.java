package com.github.siela1915.bootcamp.Recipes;


//Number of ingredients needed, different measurements, easily changeable to be avble to change number of servings wanted
//have a set of shared functions for all possible types of units
public class Unit {

    private int value;
    private String info;

    public Unit(int value, String info) {
        this.value = value;
        this.info = info;
    }

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
        this.info = info;
    }

    @Override
    public String toString(){
        return value + " " + info;
    }

}
