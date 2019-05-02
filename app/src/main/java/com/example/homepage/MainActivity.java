package com.example.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.provider.Settings.Secure;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference mDatabase;
    private RecipeFragment mealPlan;
    private Fragment mealSettings;
    private Fragment timeSettings;
    private Fragment shoppingList;
    private FragmentTransaction transaction;
    private LinearLayout buttonPanel;
    private FloatingActionButton addMeals;
    public static SharedPreferences myPreferences;
    public static SharedPreferences.Editor editor;
    private LinearLayout linearLayout;
    public static boolean wasFound = true;
    public BottomNavigationView navigation;
    public TextView timeSettingsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = myPreferences.edit();
        String android_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(android_id);
        //DatabaseReference shopDatabase = mDatabase.child("shop");

        if (!wasFound) {
            Toast.makeText(MainActivity.this, "No results found! Please change your preferences!", Toast.LENGTH_SHORT).show();
            wasFound = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigation = (BottomNavigationView) findViewById(R.id.settings_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setVisibility(View.INVISIBLE);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mealPlan = new RecipeFragment();
        mealSettings = new SettingsFragment();
        timeSettings = new TimeSettingsFragment();
        shoppingList = new ShoppingListItemsFragment();
        timeSettingsInfo = findViewById(R.id.time_settings_info);
        timeSettingsInfo.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mealPlan).commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        addMeals = (FloatingActionButton)findViewById(R.id.addMeals);
//        addMeals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent myIntent = new Intent(MainActivity.this, CreateMealPlan.class);
//                startActivity(myIntent);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFrag instanceof RecipeFragment){
            finish();
        } else if (currentFrag instanceof SettingsFragment) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mealPlan);
            transaction.addToBackStack(null);
            //buttonPanel = findViewById(R.id.buttonPanel);
            //buttonPanel.setVisibility(View.VISIBLE);
            //addMeals.setVisibility(View.VISIBLE);
            setTitle("Meal Plan");
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mealPlan);
            transaction.addToBackStack(null);
            //buttonPanel = findViewById(R.id.buttonPanel);
            //buttonPanel.setVisibility(View.VISIBLE);
            //addMeals.setVisibility(View.VISIBLE);
            navigation.setVisibility(View.INVISIBLE);
            timeSettingsInfo.setVisibility(View.INVISIBLE);
            setTitle("Meal Plan");
            transaction.commit();
        } else if (id == R.id.nav_list) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, shoppingList);
            transaction.addToBackStack(null);
            //buttonPanel = findViewById(R.id.buttonPanel);
            //buttonPanel.setVisibility(View.INVISIBLE);
            //addMeals.setVisibility(View.INVISIBLE);
            setTitle("Shopping List");
            timeSettingsInfo.setVisibility(View.INVISIBLE);
            navigation.setVisibility(View.INVISIBLE);
            transaction.commit();

        } else if (id == R.id.nav_favorite) {
            //buttonPanel = findViewById(R.id.buttonPanel);
            //buttonPanel.setVisibility(View.INVISIBLE);
            navigation.setVisibility(View.INVISIBLE);
            timeSettingsInfo.setVisibility(View.INVISIBLE);
            Favorites favs = new Favorites();
            setTitle("Favorite Recipes");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, favs).commit();
        } else if (id == R.id.nav_settings) {
            transaction = getSupportFragmentManager().beginTransaction();
            if (navigation.getSelectedItemId() == R.id.meal_settings)
                transaction.replace(R.id.fragment_container, mealSettings);
            else {
                transaction.replace(R.id.fragment_container, timeSettings);
                timeSettingsInfo.setVisibility(View.VISIBLE);
            }
            transaction.addToBackStack(null);
            //buttonPanel = findViewById(R.id.buttonPanel);
            //buttonPanel.setVisibility(View.INVISIBLE);
            //addMeals.setVisibility(View.INVISIBLE);
            linearLayout = findViewById(R.id.fragment_container);
            setTitle("Settings");
            navigation.setVisibility(View.VISIBLE);
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.meal_settings:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mealSettings);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    timeSettingsInfo.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.time_settings:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, timeSettings);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    timeSettingsInfo.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

}