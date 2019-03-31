package com.example.homepage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Search {
    public Recipe[] results;
    public String baseUri;
    public int offset;
    public int number;
    public int totalResults;
}
