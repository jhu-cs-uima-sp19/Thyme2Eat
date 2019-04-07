package com.example.homepage;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class Spoonacular extends AsyncTask <String, String, String> {
    public static DatabaseReference mDatabase;

    @Override
    protected String doInBackground(String... args) {
        Recipe[] recipes;
        if (args[0].equals("searchRandom")) {
            try {
                recipes = searchRandom(1);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Failed";
            }
            mDatabase = FirebaseDatabase.getInstance().getReference().child("plan");
            mDatabase.child("2019;03;24").child(recipes[0].title).child("image").setValue(recipes[0].image);
            for (int i = 0; i < recipes[0].extendedIngredients.size(); i++) {
                Ingredient ingredient = recipes[0].extendedIngredients.get(i);
                mDatabase.child("2019;03;24").child(recipes[0].title).child("ingredients").child(ingredient.name).child("amount").setValue(ingredient.amount);
                mDatabase.child("2019;03;24").child(recipes[0].title).child("ingredients").child(ingredient.name).child("unit").setValue(ingredient.unit);
            }
            mDatabase.child("2019;03;24").child(recipes[0].title).child("instructions").setValue(recipes[0].instructions);
            mDatabase.child("2019;03;24").child(recipes[0].title).child("time").setValue("2:00-3:00pm");
        }
        return "Success";
    }

    @Override
    protected void onPostExecute(String bitmaps) {

    }

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
        for (int i = 0; i < search.recipes.length; i++) {
            if (i < search.recipes.length-1)
                ids += search.recipes[i].id + "%2C";
            else {
                ids += search.recipes[i].id;
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

    public static Recipe[] searchRandom(int number) throws IOException, JSONException {
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random?number="
                    + number);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
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

        writeToExternalFile(json, "random.txt");
        JSONObject jsonObject = new JSONObject(json.toString());
        String r = jsonObject.get("recipes").toString();

        ObjectMapper mapper = new ObjectMapper();
        Recipe[] recipes = mapper.readValue(r, Recipe[].class);

        return recipes;
    }

    /*Used for API testing*/
    public static void main (String args[]) throws IOException {
    }
}
