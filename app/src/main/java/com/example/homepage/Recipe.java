package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

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
    public Ingredient[] extendedIngredients;

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
