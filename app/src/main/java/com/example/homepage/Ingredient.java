package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {

    public Ingredient(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public String toString() {
       return "Name: " + this.name + " Amount: " + this.amount + " Unit: " + this.unit + "\n";
    }

    //ingredient id (may be useful for something but for now not used)
    public int id;

    //ingredient name
    public String name;

    //ingredient category
    public String aisle;

    //quantity needed for a recipe OR how much you need to buy in shoppinglist
    public double amount;

    //the unit the item is measured in (ie tablespoon)
    public String unit;

    //the unit the item is measured in plural (ie tablespoons)
    public String unitLong;

    //the unit the item is measured in abbreviated (ie tbs)
    public String unitShort;

    //image URL of the ingredient
    public String image;

    //any other information that is needed for this ingredient
    public String[] metaInformation;

}
