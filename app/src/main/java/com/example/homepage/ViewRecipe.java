package com.example.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRecipe extends AppCompatActivity {

    private Fragment viewRecipe;
    private Fragment viewIngredients;
    private FragmentTransaction transaction;
    public static String title;
    public static String instructions;
    public static ArrayList<Ingredient> ingredients;
    public static String imageUrl;
    public static Recipe r;
    public static int arrayChoice;

    public static boolean fav;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_ingredients:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, viewIngredients);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.navigation_recipe:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, viewRecipe);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        ArrayList<Recipe> viewArray;
        final int index = getIntent().getIntExtra("index", -1);
        arrayChoice = getIntent().getIntExtra("array", 0);
        if (arrayChoice == 0) {
            viewArray = RecipeFragment.mealList;
        } else if (arrayChoice == 1){
            viewArray = ChooseAlternative.alternativeList;
        } else {
            viewArray = Favorites.favoritesList;
        }
        r = viewArray.get(index);

        DatabaseReference favs = MainActivity.mDatabase.child("favs");
        favs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot favRecipes) {
                boolean found = false;
                for (DataSnapshot fav : favRecipes.getChildren()) {
                    if (r.id == Integer.parseInt(fav.child("id").getValue().toString())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    fav = true;
                } else {
                    fav = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        instructions = viewArray.get(index).instructions;
        ingredients = viewArray.get(index).extendedIngredients;
        imageUrl = viewArray.get(index).image;
        title = viewArray.get(index).title;
        actionBar.setTitle(title);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewRecipe = new ViewRecipeFragment();
        viewIngredients = new ViewIngredientsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, viewIngredients).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_meal_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.back_button){
            finish();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("closing", "view recipe");
        final DatabaseReference db = MainActivity.mDatabase;
        if (fav && arrayChoice != 2) {
            Log.w("closing", "here");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (arrayChoice == 0) {
                        db.child("favs").child(r.title).setValue(dataSnapshot.child("plan").child(r.date).child(r.title).getValue());
                    } else if (arrayChoice == 1) {
                        db.child("favs").child(r.title).setValue(dataSnapshot.child("plan").child(r.date).child(r.parentName).child("alts").child(r.title).getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (!fav) {
            db.child("favs").child(r.title).setValue(null);
        }
    }
}
