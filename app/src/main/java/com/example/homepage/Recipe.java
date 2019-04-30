package com.example.homepage;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Recipe (String title, String date, String time, String instruct, ArrayList<Ingredient> ingreds, String image, int duration) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.instructions = instruct;
        this.extendedIngredients = ingreds;
        this.image = image;
        this.readyInMinutes = duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public Recipe() {

    }

    public void moveRecipeInFirebase(DatabaseReference dataBase, final String oldDate) {
        final DatabaseReference db = dataBase;
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                DatabaseReference planDate = db.child(Recipe.this.date);
                planDate.child(Recipe.this.title).setValue(ds.child(oldDate).child(Recipe.this.title).getValue());
                db.child(oldDate).child(Recipe.this.title).setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public String dayOfWeek;
    public String day;
    public String month;
    //Date of recipe in plan
    public String date;
    public String dateText;
    //Time for recipe in plan
    public String time;

    public String timeText;

    public boolean hasAlts = false;

    //id needed to search recipe info
    public long id;

    //title of recipe
    public String title;

    //how long it takes to make the recipe
    public int readyInMinutes;

    //image URL of the recipe
    public String image;

    public Bitmap bitmap;

    //recipe instructions
    public String instructions;

    //number of servings (useful to determine if there will be leftovers)
    public int servings;

    //list of all ingredients present in the recipe
    public ArrayList<Ingredient> extendedIngredients;

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

    public boolean withPrev;

    public static String makeDateText(String date) {
        String dateText = date.replace(";","/");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateText);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dateText = convertedDate.toString().substring(0, 11);
        return dateText;
    }

    public static void readMeals(DatabaseReference db, final ArrayList<Recipe> meals, final RecipesRecyclerViewAdapter rcAdapter, final String date) {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String altDate = date;
                String time = "";
                String instruct = "";
                String image = "";
                String title = "";
                ArrayList<Ingredient> ingreds;
                int duration = 60;
                for (DataSnapshot meal : dataSnapshot.getChildren()) {
                    ingreds = new ArrayList<>();
                    title = meal.getKey();
                    time = "0:00-23:59pm";
                    if (meal.child("time").exists())
                        time = meal.child("time").getValue().toString();
                    instruct = "Insert Instructions Here";
                    if (meal.child("instructions").exists())
                        instruct = meal.child("instructions").getValue().toString();
                    if (meal.child("ingredients").exists()) {
                        for (DataSnapshot ingred : meal.child("ingredients").getChildren()) {
                            if (ingred.child("amount").exists() && ingred.child("unit").exists()) {
                                Ingredient i = new Ingredient(ingred.getKey(),
                                        Double.parseDouble(ingred.child("amount").getValue().toString()),
                                        ingred.child("unit").getValue().toString());
                                ingreds.add(i);
                            }
                        }
                    }
                    if (meal.child("readyInMinutes").exists()) {
                        duration = Integer.parseInt(meal.child("readyInMinutes").getValue().toString());
                    }
                    if (meal.child("image").exists())
                        image = meal.child("image").getValue().toString();
                    Recipe r = new Recipe(title, altDate, time, instruct, ingreds, image, duration);
                    meals.add(r);
                }
                rcAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
