package com.example.homepage;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    public Recipe (String date, String time, String instruct, ArrayList<Ingredient> ingreds) {
        this.date = date;
        this.time = time;
        this.instructions = instruct;
        this.extendedIngredients = ingreds;
        Log.w("test", date + time + instruct + ingreds.toString());
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }


    //Date of recipe in plan
    public String date;
    //Time for recipe in plan
    public String time;

    //id needed to search recipe info
    public int id;

    //title of recipe
    public String title;

    //how long it takes to make the recipe
    public int readyInMinutes;

    //image URL of the recipe
    public String image;

    //recipe instructions
    public String instructions;

    //number of servings (useful to determine if there will be leftovers)
    public int servings;

    //list of all ingredients present in the recipe
    public ArrayList<Ingredient> extendedIngredients;

    //list of boolean values for recipe types
    public boolean vegetarian;
    public boolean vegan;
    public boolean glutenFree;
    public boolean dairyFree;
    public boolean veryHealthy;
    public boolean cheap;
    public boolean veryPopular;

    //determine popularity of a dish
    public int likes;
}
