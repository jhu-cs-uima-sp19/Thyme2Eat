package com.example.homepage;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
    public Recipe (String title, String date, String time, String instruct, ArrayList<Ingredient> ingreds, String image) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.instructions = instruct;
        this.extendedIngredients = ingreds;
        this.image = image;
        //Log.w("test", date + time + instruct + ingreds.toString());
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

    //id needed to search recipe info
    public int id;

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
        //dateText = convertedDate.toString().replace("00:00:00 EDT ", "");
        dateText = convertedDate.toString().substring(0, 11);
        return dateText;
    }
}
