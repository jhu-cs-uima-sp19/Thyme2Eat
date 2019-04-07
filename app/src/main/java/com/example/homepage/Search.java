package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Search {
    //the list of recipes from a search
    public Recipe[] recipes;

    //the number of results returned
    public int totalResults;
}
