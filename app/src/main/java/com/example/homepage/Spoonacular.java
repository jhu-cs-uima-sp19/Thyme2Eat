package com.example.homepage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static com.example.homepage.ViewRecipe.r;


public class Spoonacular extends AsyncTask <String, String, String> {
    public static boolean skip = false;
    public static DatabaseReference mDatabase;
    private WeakReference<Context> contextRef;
    private ProgressBar progressBar;
    private Dialog dialog;
    private TextView message;
    private static ArrayList<Ingredient> ingredients;
    private static ArrayList<Long> recipeIds = new ArrayList<>();
    private DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
    private boolean search = false;
    private boolean similar = false;
    private String date ="";
    private String name = "";
    private Context c;
    public static boolean wentThrough = false;

    public Spoonacular(Context context) {
        c = context;
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        if (skip) {
            skip = false;
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextRef.get());
        LayoutInflater layoutInflater = LayoutInflater.from(contextRef.get());
        View progressDialogBox = layoutInflater.inflate(R.layout.loading_dialog, null);
        alertDialogBuilder.setView(progressDialogBox);
        progressBar = progressDialogBox.findViewById(R.id.progressBar);
        message = progressDialogBox.findViewById(R.id.output);
        message.setText("Finding recipes based on preferences...");
        progressBar.setMax(100);
        dialog = alertDialogBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ingredients = new ArrayList<>();

        for (int i = 0; i < RecipeFragment.mealList.size(); i++) {
            recipeIds.add(RecipeFragment.mealList.get(i).id);
        }


        shopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot items : dataSnapshot.getChildren()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.name = items.getKey();
                    if (items.child("amount").getValue() instanceof Long)
                        ingredient.amount = (Long) items.child("amount").getValue();
                    else {
                        ingredient.amount = (Double) items.child("amount").getValue();
                    }
                    if (items.child("unit").exists())
                        ingredient.unit = items.child("unit").getValue().toString();
                    ingredients.add(ingredient);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected String doInBackground(String... args) {
        ArrayList<Recipe> recipes = null;
        ArrayList<String> roots = new ArrayList<>();
        if (args[0].equals("search") || args[0].equals("getSimilar")) {

            int numberOfRecipes = 0;
            int numMealsPerDay = 0;

            if (args[0].equals("search")) {
                for (int i = 0; i < 3; i++) {
                    if (MainActivity.myPreferences.getBoolean("Meal " + (i+1), false))
                        numMealsPerDay++;
                }
                if (numMealsPerDay == 0) {
                    numMealsPerDay = 1;
                }
                search = true;
                mDatabase = MainActivity.mDatabase.child("plan");
                String datesString = args[8];

                while (datesString.length() >0) {
                    roots.add(datesString.substring(0,10));
                    datesString = datesString.substring(10);
                }
                numberOfRecipes = roots.size();
                try {
                    recipes = searchRecipes(args[1], args[2], args[3], args[4], args[5], args[6], args[7], roots.size(), numMealsPerDay);
                    message.setText("Recipes found! Choosing best recipes...");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (args[0].equals("getSimilar")) {
                date = args[3];
                name = args[2];
                similar = true;
                numberOfRecipes = 5;
                numMealsPerDay = 1;
                roots.add("alts");
                mDatabase = MainActivity.mDatabase.child("plan").child(args[3]).child(args[2]);
                try {
                    recipes = getSimilarRecipes(Integer.valueOf(args[1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final HashMap<String, Ingredient> shoppingList = new HashMap<>();
            MainActivity.mDatabase.child("shop").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ingred : dataSnapshot.getChildren()) {
                        String name = ingred.getKey();
                        String unit = ingred.child("unit").getValue().toString();
                        Double amount = Double.parseDouble(ingred.child("amount").getValue().toString());
                        Ingredient i = new Ingredient(name, amount, unit);
                        shoppingList.put(i.name, i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            for (int d = 0; d < numberOfRecipes; d++) {

                for (int i = 0; i < numMealsPerDay; i++) {
                    System.out.println("Number of recipes " + numberOfRecipes);

                    if (i + d*numMealsPerDay >= recipes.size()) {
                        System.out.println("true");
                        break;
                    }
                    Recipe recipe = recipes.get(i + d*numMealsPerDay);
                    if (recipe.instructions == null)
                        break;
                    recipe.title = recipe.title.replace("[", "(");
                    recipe.title = recipe.title.replace("]", ")");
                    recipe.title = recipe.title.replace(".", "");
                    recipe.title = recipe.title.replace("#", "");
                    recipe.title = recipe.title.replace("$", "");

                    String root;
                    if (roots.get(0).equals("alts")) {
                        root = "alts";
                    } else {
                        root = roots.get(d);
                    }
                    mDatabase.child(root).child(recipe.title).child("image").setValue(recipe.image);
                    mDatabase.child(root).child(recipe.title).child("id").setValue(recipe.id);
                    mDatabase.child(root).child(recipe.title).child("readyInMinutes").setValue(recipe.readyInMinutes);
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

                    //updateShopList(recipe, root, false, true, args[0]);
                    for (Ingredient ingredient : recipe.extendedIngredients) {
                        mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("amount").setValue(ingredient.amount);
                        mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("unit").setValue(ingredient.unit);
                        String altKey = ingredient.name + " :" + ingredient.unit;
                        if (!args[0].equals("getSimilar")) {
                            if (shoppingList.containsKey(ingredient.name)) {
                                double existingVal = shoppingList.get(ingredient.name).amount;
                                String dbUnit = shoppingList.get(ingredient.name).unit;
                                double addVal;
                                if (ingredient.unit.equals(dbUnit) || ingredient.unit.contains(dbUnit) || dbUnit.contains(ingredient.unit)) {
                                    shoppingList.get(ingredient.name).amount = existingVal + ingredient.amount;
                                } else {
                                    addVal = -1;
                                    try {
                                        addVal = convertUnit(ingredient, dbUnit).amount;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (addVal != -1) {
                                        shoppingList.get(ingredient.name).amount = existingVal + addVal;
                                    } else if (shoppingList.containsKey(altKey)) {
                                        existingVal = shoppingList.get(altKey).amount;
                                        shoppingList.get(altKey).amount = existingVal + ingredient.amount;
                                    } else {
                                        shoppingList.put(altKey, ingredient);
                                    }
                                }
                            } else {
                                shoppingList.put(ingredient.name, ingredient);
                            }
                        }
                    }

                    mDatabase.child(root).child(recipe.title).child("instructions").setValue(recipe.instructions);
                    String start = MainActivity.myPreferences.getString("Meal " + (i + 1) + " start", "14:00");
                    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                    Calendar cal = Calendar.getInstance();
                    try {
                        cal.setTime(sdf.parse(start));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    start = sdf.format(cal.getTime());
                    cal.add(Calendar.MINUTE, recipe.readyInMinutes);
                    String end = sdf.format(cal.getTime());
                    mDatabase.child(root).child(recipe.title).child("time").setValue(start + "-" + end);
                    progressBar.incrementProgressBy(100 / numberOfRecipes);
                }
            }
            final DatabaseReference shoppingDB = MainActivity.mDatabase.child("shop");
            shoppingDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (String key : shoppingList.keySet()) {
                        shoppingDB.child(key).child("unit").setValue(shoppingList.get(key).unit);
                        shoppingDB.child(key).child("amount").setValue(shoppingList.get(key).amount);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (args[0].equals("convert")) {
            Ingredient i = new Ingredient();
            i.amount = Double.parseDouble(args[1]);
            i.unit = args[2];
            i.name = args[4];
            String target = args[3];
            try {
                RecipesRecyclerViewAdapter.convertedAmount = convertUnit(i, args[3]).amount;
                //Double temp = convertUnit(i, args[3]).amount;
                /*
                if (temp == -1) {
                    RecipesRecyclerViewAdapter.convertedAmount = 0;
                } else {
                    RecipesRecyclerViewAdapter.convertedAmount = temp;
                }
                */

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Spoonacular.wentThrough = true;
        } else if (args[0].equals("updateShop")) {
            final Recipe r;
            if (args[1].equals("alts")) {
                r = AlternateRecipeRcViewAdapter.alternatives.get(AlternateRecipeRcViewAdapter.selected);
                updateShopList(r, r.date, false, false, "alt");
            } else {
                int idx = Integer.parseInt(args[2]);
                r = Favorites.favoritesList.get(idx);
                updateShopList(r, r.date, false, false, "");
            }
        }
        return "Success";
    }

    @Override
    protected void onPostExecute(String bitmaps) {
        Context context = contextRef.get();
        if (search) {
            if (context != null) {
                Intent myIntent = new Intent(context, MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialog.dismiss();
                if (bitmaps == null) {
                    MainActivity.wasFound = false;
                }
                context.startActivity(myIntent);
            }
        } else if (similar) {
            if (context != null) {
                Intent myIntent = new Intent(context, ChooseAlternative.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myIntent.putExtra("name", name);
                myIntent.putExtra("date",date);
                dialog.dismiss();
                context.startActivity(myIntent);
            }
        }
    }

    public void updateShopList(final Recipe recipe, final String root, boolean readDatabase, final boolean updateDb, final String arg) {
        final DatabaseReference shopDatabase = MainActivity.mDatabase.child("shop");
        if (readDatabase) {
            ingredients = new ArrayList<>();
            shopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot items : dataSnapshot.getChildren()) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.name = items.getKey();
                        if (items.child("amount").getValue() instanceof Long)
                            ingredient.amount = (Long) items.child("amount").getValue();
                        else {
                            ingredient.amount = (Double) items.child("amount").getValue();
                        }
                        if (items.child("unit").exists())
                            ingredient.unit = items.child("unit").getValue().toString();
                        ingredients.add(ingredient);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        double conversions[] = new double[recipe.extendedIngredients.size()];
        int i = 0;
        /*
        for (Ingredient ingredient : recipe.extendedIngredients) {
            if (shopDatabase.child(ingredient.name) != null) {
                convertUnit(ingredient, )
            }
        }
        */
        shopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (arg.equals("alt")) {
                    for (Ingredient ingredient : ChooseAlternative.oldRecipe.extendedIngredients) {
                        String altKey = ingredient.name + " :" + ingredient.unit;
                        if (dataSnapshot.child(ingredient.name).exists()) {
                            double existingVal = Double.parseDouble(dataSnapshot.child(ingredient.name).child("amount").getValue().toString());
                            String dbUnit = dataSnapshot.child(ingredient.name).child("unit").toString();
                            double addVal;
                            if (ingredient.unit.equals(dbUnit) || ingredient.unit.contains(dbUnit) || dbUnit.contains(ingredient.unit)) {
                                Double newAmount = existingVal - ingredient.amount;
                                if (newAmount > 0.1) {
                                    shopDatabase.child(ingredient.name).child("amount").setValue(newAmount);
                                } else {
                                    shopDatabase.child(ingredient.name).setValue(null);
                                }
                            } else {
                                Spoonacular.skip = true;
                                try {
                                    new Spoonacular(Spoonacular.this.c).execute("convert", String.valueOf(ingredient.amount), ingredient.unit, dbUnit, ingredient.name).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                long startTime = System.currentTimeMillis();
                                Spoonacular.wentThrough = false;
                                addVal = RecipesRecyclerViewAdapter.convertedAmount;
                                if (addVal != -1) {
                                    Double newAmount = existingVal - addVal;
                                    if (newAmount > 0.1) {
                                        shopDatabase.child(ingredient.name).child("amount").setValue(newAmount);
                                    } else {
                                        shopDatabase.child(ingredient.name).setValue(null);
                                    }
                                } else if (dataSnapshot.child(altKey).exists()){
                                    existingVal = Double.parseDouble(dataSnapshot.child(altKey).child("amount").getValue().toString());
                                    Double newAmount = existingVal - ingredient.amount;
                                    if (newAmount > 0.1) {
                                        shopDatabase.child(altKey).child("amount").setValue(newAmount);
                                    } else {
                                        shopDatabase.child(altKey).setValue(null);
                                    }
                                }
                                //shopDatabase.child(ingredient.name).child("unit").setValue("oz");
                            }
//                            shopDatabase.child(ingredient.name).child("amount").setValue(ing.amount);
//                            shopDatabase.child(ingredient.name).child("unit").setValue(ing.unit);
                        } else if (dataSnapshot.child(altKey).exists()){
                            Double existingVal = Double.parseDouble(dataSnapshot.child(altKey).child("amount").getValue().toString());
                            Double newAmount = existingVal - ingredient.amount;
                            if (newAmount > 0.1) {
                                shopDatabase.child(altKey).child("amount").setValue(newAmount);
                            } else {
                                shopDatabase.child(altKey).setValue(null);
                            }
                        }
                    }
                    shopDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (Ingredient ingredient : recipe.extendedIngredients) {
                                if (updateDb) {
                                    mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("amount").setValue(ingredient.amount);
                                    mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("unit").setValue(ingredient.unit);
                                }
                                boolean contains = false;
                                if (!arg.equals("getSimilar")) {
                                    if (dataSnapshot.child(ingredient.name).exists()) {
                                        double existingVal = Double.parseDouble(dataSnapshot.child(ingredient.name).child("amount").getValue().toString());
                                        String dbUnit = dataSnapshot.child(ingredient.name).child("unit").toString();
                                        double addVal;
                                        if (ingredient.unit.equals(dbUnit) || ingredient.unit.contains(dbUnit) || dbUnit.contains(ingredient.unit)) {

                                            shopDatabase.child(ingredient.name).child("amount").setValue(existingVal + ingredient.amount);
                                        } else {
                                            Spoonacular.skip = true;
                                            try {
                                                new Spoonacular(Spoonacular.this.c).execute("convert", String.valueOf(ingredient.amount), ingredient.unit, dbUnit, ingredient.name).get();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            long startTime = System.currentTimeMillis();
                                            Spoonacular.wentThrough = false;
                                            addVal = RecipesRecyclerViewAdapter.convertedAmount;
                                            if (addVal != -1)
                                                shopDatabase.child(ingredient.name).child("amount").setValue(existingVal + addVal);
                                            else {
                                                ingredient.name += " :" + ingredient.unit;
                                                shopDatabase.child(ingredient.name).child("amount").setValue(ingredient.amount);
                                                shopDatabase.child(ingredient.name).child("unit").setValue(ingredient.unit);
                                            }
                                        }
                                    } else {
                                        shopDatabase.child(ingredient.name).child("amount").setValue(ingredient.amount);
                                        shopDatabase.child(ingredient.name).child("unit").setValue(ingredient.unit);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return;
                }


                for (Ingredient ingredient : recipe.extendedIngredients) {
                    if (updateDb) {
                        mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("amount").setValue(ingredient.amount);
                        mDatabase.child(root).child(recipe.title).child("ingredients").child(ingredient.name).child("unit").setValue(ingredient.unit);
                    }
                    boolean contains = false;
                    if (!arg.equals("getSimilar")) {
                        if (dataSnapshot.hasChild(ingredient.name)) {
                            double existingVal = Double.parseDouble(dataSnapshot.child(ingredient.name).child("amount").getValue().toString());
                            String dbUnit = dataSnapshot.child(ingredient.name).child("unit").toString();
                            double addVal;
                            if (ingredient.unit.equals(dbUnit) || ingredient.unit.contains(dbUnit) || dbUnit.contains(ingredient.unit)) {
                                shopDatabase.child(ingredient.name).child("amount").setValue(existingVal + ingredient.amount);
                            } else {
                                Spoonacular.skip = true;
                                try {
                                    new Spoonacular(Spoonacular.this.c).execute("convert", String.valueOf(ingredient.amount), ingredient.unit, dbUnit, ingredient.name).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                long startTime = System.currentTimeMillis();
                                Spoonacular.wentThrough = false;
                                addVal = RecipesRecyclerViewAdapter.convertedAmount;
                                if (addVal != -1) {
                                    shopDatabase.child(ingredient.name).child("amount").setValue(existingVal + addVal);
                                } else {
                                    ingredient.name += " :" + ingredient.unit;
                                    shopDatabase.child(ingredient.name).child("amount").setValue(ingredient.amount);
                                    shopDatabase.child(ingredient.name).child("unit").setValue(ingredient.unit);
                                }
                            }
                        } else {
                            shopDatabase.child(ingredient.name).child("amount").setValue(ingredient.amount);
                            shopDatabase.child(ingredient.name).child("unit").setValue(ingredient.unit);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                                         String type, String number, int days, int meals) throws IOException {

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
        Search search = new Search();
        search = mapper.readValue(json.toString(), Search.class);
        System.out.println(search.results.size());

        //Take all ids to be used to get recipe information
        String ids = "";
        int count = 0;
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < meals; j++) {
                while (true) {
                    if (search.results.size() - 1 != 0) {
                        int index = new Random().nextInt(search.results.size() - 1);
                        long id = search.results.get(index).id;

                        boolean contains = false;
                        boolean time = true;

                        for (int k = 0; k < recipeIds.size(); k++) {
                            if (recipeIds.get(k) == id) {
                                contains = true;
                                break;
                            }
                        }

                        String start = MainActivity.myPreferences.getString("Meal " + (j + 1) + " start", "14:00");
                        String end = MainActivity.myPreferences.getString("Meal " + (j + 1) + " end", "15:00");
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                        Calendar cal = Calendar.getInstance();
                        Calendar endCal = Calendar.getInstance();
                        try {
                            cal.setTime(sdf.parse(start));
                            endCal.setTime(sdf.parse(end));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        cal.add(Calendar.MINUTE, search.results.get(index).readyInMinutes);
                        if (cal.getTime().after(endCal.getTime())) {
                            time = false;
                        }

                        if (!contains && time) {
                            if (i == days - 1 && j == meals - 1) {
                                ids += id;
                            } else {
                                ids += id + "%2C";
                            }
                            count++;
                            search.results.remove(search.results.get(index));
                            recipeIds.add(id);
                            break;
                        }
                        search.results.remove(search.results.get(index));
                    } else {
                        break;
                    }
                }
            }
        }
        return searchBulk(ids) ;
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
        JSONObject jsonObj = null;
        jsonObj = new JSONObject(json.toString());
        String oldUnit = ingredient.unit;
        ingredient.unit = target;
        try {
            ingredient.amount = Double.valueOf(jsonObj.get("targetAmount").toString());
        } catch (JSONException e) {
            ingredient.name += " :" + ingredient.unit;
            ingredient.amount = -1;
            e.printStackTrace();
        }
        return ingredient;
    }


    /*This method returns a list of similar recipes given an id; will be very useful when offering alternatives*/
    public static ArrayList<Recipe> getSimilarRecipes(int id) throws IOException {
        StringBuffer json = new StringBuffer();
        System.out.println(id);
        try{
            URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/"+ id +"/similar");
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