package com.example.homepage;

public class User {

    //african, chinese, japanese, korean, vietnamese, thai, indian, british, irish, french, italian,
    // mexican, spanish, middle eastern, jewish, american, cajun, southern, greek, german, nordic,
    // eastern european, caribbean, or latin american.
    public String[] favoriteCuisines;

    //pescetarian, lacto vegetarian, ovo vegetarian, vegan, paleo, primal, and vegetarian.
    public String diet;

    //user can choose items that they are allergic/intolerant to
    public String[] intolerances;

    //find recipes that have specific ingredients
    public String[] includeIngredients;

    //find recipes that do not have specific ingredients
    public String[] excludeIngredients;

    //useful when finding appropriate recipes by serving
    public int numberOfDependents;

}
