package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {

    public int id;
    public String name;
    public String aisle;
    public double amount;
    public String unit;
    public String unitLong;
    public String unitShort;
    public String image;
    public String[] metaInformation;

}
