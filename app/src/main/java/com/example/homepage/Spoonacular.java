package com.example.homepage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Spoonacular extends AsyncTask <String, String, String> {
    public static DatabaseReference mDatabase;
    private WeakReference<Context> contextRef;
    private Context context;
    private ProgressBar progressBar;
    private Dialog dialog;
    private TextView message;

    public Spoonacular(Context context) {
        contextRef = new WeakReference<>(context);
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextRef.get());
        LayoutInflater layoutInflater = LayoutInflater.from(contextRef.get());
        View progressDialogBox = layoutInflater.inflate(R.layout.loading_dialog, null);
        alertDialogBuilder.setView(progressDialogBox);
        progressBar = progressDialogBox.findViewById(R.id.progressBar);
        message = progressDialogBox.findViewById(R.id.output);
        message.setText("Finding recipes based on preferences..");
        progressBar.setMax(100);
        dialog = alertDialogBuilder.create();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... args) {
        ArrayList<Recipe> recipes = null;
        if (args[0].equals("search")) {

            String datesString = args[8];
            ArrayList<String> dates = new ArrayList<>();
            while (datesString.length() >0) {
                dates.add(datesString.substring(0,10));
                datesString = datesString.substring(10);
            }

            try {
                recipes = searchRecipes(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                message.setText("Recipes found! Choosing best recipes...");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


            final ArrayList<String> recipeTitles = new ArrayList<>();
            mDatabase = MainActivity.mDatabase.child("plan");

            for (int i = 0; i < RecipeFragment.mealList.size(); i++) {
                recipeTitles.add(RecipeFragment.mealList.get(i).title);
            }

            for (int d = 0; d < dates.size(); d++) {
                Recipe recipe;
                while (true) {
                    recipe = recipes.get(new Random().nextInt(recipes.size() - 1));
                    recipe.title = recipe.title.replace("[", "(");
                    recipe.title = recipe.title.replace("]", ")");
                    recipe.title = recipe.title.replace(".", "");
                    recipe.title = recipe.title.replace("#", "");
                    recipe.title = recipe.title.replace("$", "");
                    if (!recipeTitles.contains(recipe.title)) {
                        recipeTitles.add(recipe.title);
                        break;
                    } else {
                        recipes.remove(recipe);
                    }
                }

                String date = dates.get(d);
                mDatabase.child(date).child(recipe.title).child("image").setValue(recipe.image);
                Bitmap myBitmap;
                try {
                    java.net.URL url = new java.net.URL(recipe.image);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);
                    String filename = url.toString().substring(url.toString().lastIndexOf('/') + 1);
                    File file = new File("/data/user/0/com.example.homepage/cache", filename);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final ArrayList<Ingredient> ingredients = new ArrayList<>();
                DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
                for (int i = 0; i < recipe.extendedIngredients.size(); i++) {
                    Ingredient ingredient = recipe.extendedIngredients.get(i);
                    boolean contains = false;
                    for (Ingredient ing: ingredients) {
                        if (ingredient.name.equals(ing.name)) {
                            contains = true;
                            if (ingredient.unit.equals(ing.unit)) {
                                ing.amount += ingredient.amount;
                            } else {
                                try {
                                    System.out.print("CONVERTED UNIT");
                                    ingredient = convertUnit(ingredient, "oz");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ing.amount += ingredient.amount;
                            }
                            break;
                        }
                    }
                    if (!contains) {
                        ingredients.add(ingredient);
                    }
                }
                for (Ingredient ingredient: ingredients) {
                    mDatabase.child(date).child(recipe.title).child("ingredients").child(ingredient.name).child("amount").setValue(ingredient.amount);
                    mDatabase.child(date).child(recipe.title).child("ingredients").child(ingredient.name).child("unit").setValue(ingredient.unit);
                    shopDatabase.child(ingredient.name).child("amount").setValue(ingredient.amount);
                    shopDatabase.child(ingredient.name).child("unit").setValue(ingredient.unit);
                }
                mDatabase.child(date).child(recipe.title).child("instructions").setValue(recipe.instructions);
                mDatabase.child(date).child(recipe.title).child("time").setValue("2:00-3:00pm");
                progressBar.incrementProgressBy(100/dates.size());
            }
        }
        return "Success";
    }

    @Override
    protected void onPostExecute(String bitmaps) {
        Context context = contextRef.get();
        if (context != null ) {
            Intent myIntent = new Intent(context, MainActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialog.dismiss();
            if (bitmaps == null) {
                MainActivity.wasFound = false;
            }
            context.startActivity(myIntent);
        }
    }

    /* This method allows you to get search results in bulk;
    *  used to convert a search that yields
    *  multiple results into a list of Recipes*/
    private static ArrayList<Recipe> searchBulk(String ids) throws IOException {

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


        ObjectMapper mapper = new ObjectMapper();
        Recipe[] recipes = mapper.readValue(json.toString(), Recipe[].class);
        ArrayList<Recipe> mcList = new ArrayList<>(Arrays.asList(recipes));
        return mcList;
    }

    /*This method searches recipes based on a query, but we can make this one more complex if need be*/
    public static ArrayList<Recipe> searchRecipes(String cuisine, String diet, String includeIngredients,
                                         String excludeIngredients, String intolerances,
                                         String type, String number) throws IOException {

        System.out.println("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/searchComplex?" +
                cuisine + diet + includeIngredients + excludeIngredients + intolerances + type +
                "&ranking=2&fillIngredients=true&instructionsRequired=true&addRecipeInformation=true&limitLicense=false&offset=0&number=" + number);


        //Searches recipes with a given query and number of results to return
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/searchComplex?" +
                    cuisine + diet + includeIngredients + excludeIngredients + intolerances + type +
                    "&ranking=2&fillIngredients=true&instructionsRequired=true&addRecipeInformation=true&limitLicense=false&offset=0&number=" + number);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
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
        System.out.println(json.toString());

        return searchBulk(ids);
    }

    /*this method searches by ingredients*/
    public static ArrayList<Recipe> searchByIngredients(String[] ingredients, int number, int ranking, boolean ignorePantry) throws IOException {

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

    public static Ingredient convertUnit (Ingredient ingredient, String target) throws JSONException {
        StringBuffer json = new StringBuffer();
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/convert?sourceUnit="
                    + ingredient.unit + "&sourceAmount="+ ingredient.amount + "&ingredientName="+ ingredient.name +"&targetUnit=" + target);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("X-RapidAPI-Key", "ebbeaa7cbemsh020d1b6ca0a5850p11572bjsnf2dead442a16");
            connection.setRequestProperty("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com");
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
        JSONObject jsonObj = new JSONObject(json.toString());
        ingredient.unit = target;
        ingredient.amount = Double.valueOf(jsonObj.get("targetAmount").toString());
        return ingredient;
    }


    /*This method returns a list of similar recipes given an id; will be very useful when offering alternatives*/
    public static ArrayList<Recipe> getSimilarRecipes(int id) throws IOException {
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
