package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Search {
    //the list of recipes from a search
    public ArrayList<Recipe> results;

    //the number of results returned
    public int totalResults;
}