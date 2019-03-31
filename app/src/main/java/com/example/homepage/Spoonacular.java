package com.example.homepage;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Spoonacular {

    /* This method allows you to get search results in bulk;
    *  used to convert a search that yields
    *  multiple results into a list of Recipes*/
    private static Recipe[] searchBulk(String ids) throws IOException {

        //request bulk recipe information
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/informationBulk?ids=" + ids);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                json.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }

        //print results to external file
        writeToExternalFile(json, "searchResults.txt");

        ObjectMapper mapper = new ObjectMapper();
        Recipe[] recipes = mapper.readValue(json.toString(), Recipe[].class);

        return recipes;
    }

    /*This method writes results to external files, this is good for testing and saving on API calls*/
    private static void writeToExternalFile(StringBuffer json, String fileName ){
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-16");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(json.toString());

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*This method searches recipes based on a query, but we can make this one more complex if need be*/
    public static Recipe[] searchRecipes(String query, int number) throws IOException {

        //Searches recipes with a given query and number of results to return
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/search?number=" + number +"&query=" + query);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                json.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }

        //Write results to an external file
        writeToExternalFile(json, "search.txt");

        ObjectMapper mapper = new ObjectMapper();
        Search search = mapper.readValue(json.toString(), Search.class);

        //Take all ids to be used to get recipe information
        String ids = "";
        for (int i = 0; i < search.results.length; i++) {
            if (i < search.results.length-1)
                ids += search.results[i].id + "%2C";
            else {
                ids += search.results[i].id;
            }
        }

        return searchBulk(ids);
    }

    /*this method searches by ingredients*/
    public static Recipe[] searchByIngredients(String[] ingredients, int number, int ranking, boolean ignorePantry) throws IOException {

        String ingredientUrl = "";
        for (int i = 0; i < ingredients.length; i++) {
            if (i < ingredients.length-1)
                ingredientUrl += ingredients[i] + "%2C";
            else {
                ingredientUrl += ingredients[i];
            }
        }

        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients?number="
                    +number+"&ranking="+ranking+"&ignorePantry="+ignorePantry+"&ingredients="+ingredientUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                json.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }

        //Write results to an external file
        writeToExternalFile(json, "searchIngredients.txt");

        ObjectMapper mapper = new ObjectMapper();
        Recipe[] search = mapper.readValue(json.toString(), Recipe[].class);

        String ids = "";
        for (int i = 0; i < search.length; i++) {
            if (i < search.length-1)
                ids += search[i].id + "%2C";
            else {
                ids += search[i].id;
            }
        }

        return searchBulk(ids);
    }


    /*This method returns a list of similar recipes given an id; will be very useful when offering alternatives*/
    public static Recipe[] getSimilarRecipes(int id) throws IOException {
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/"+ id +"/similar");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                json.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }

        writeToExternalFile(json, "similar.txt");

        ObjectMapper mapper = new ObjectMapper();
        Recipe[] search = mapper.readValue(json.toString(), Recipe[].class);

        String ids = "";
        for (int i = 0; i < search.length; i++) {
            if (i < search.length-1)
                ids += search[i].id + "%2C";
            else {
                ids += search[i].id;
            }
        }

        return searchBulk(ids);
    }

    /*Used for API testing*/
    public static void main (String args[]) throws IOException {

    }
}
