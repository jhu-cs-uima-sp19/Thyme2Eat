package com.example.homepage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewRecipe extends AppCompatActivity {

    private Fragment viewRecipe;
    private Fragment viewIngredients;
    private FragmentTransaction transaction;
    public static String instructions;
    public static ArrayList<Ingredient> ingredients;

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
        int index = getIntent().getIntExtra("index", 0);
        instructions = MainActivity.mealList.get(index).instructions;
        ingredients = MainActivity.mealList.get(index).extendedIngredients;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewRecipe = new ViewRecipeFragment();
        viewIngredients = new ViewIngredientsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, viewIngredients).commit();
    }

}
