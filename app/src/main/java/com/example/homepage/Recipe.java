package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    public int id;
    public String title;
    public int readyInMinutes;
    public String image;
    public String instructions;
    public int servings;
    public Ingredient[] extendedIngredients;
    public boolean vegetarian;
    public boolean vegan;
    public boolean glutenFree;
    public boolean dairyFree;
    public boolean veryHealthy;
    public boolean cheap;
    public boolean veryPopular;
    public int likes;
}
