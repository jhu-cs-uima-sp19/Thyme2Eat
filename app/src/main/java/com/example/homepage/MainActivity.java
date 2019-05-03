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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.provider.Settings.Secure;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "Calendar";
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
    private SharedPreferences myDates;
    private SharedPreferences.Editor dateEditor;
    private static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
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
        myDates = getSharedPreferences("dates", MODE_PRIVATE);
        dateEditor = myDates.edit();

//        addMeals = (FloatingActionButton)findViewById(R.id.addMeals);
//        addMeals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent myIntent = new Intent(MainActivity.this, CreateMealPlan.class);
//                startActivity(myIntent);
//            }
//        });

        DatabaseReference planDB = mDatabase.child("plan");
        planDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                i = 0;
                for (DataSnapshot planDate : dataSnapshot.getChildren()) {
                    String date = planDate.getKey();
                    //year;month;day
                    Log.d(TAG, "date " + date);
                    try {
                        Date datePlanned = new SimpleDateFormat("yyyy;MM;dd").parse(date);
                        Log.d(TAG, "date planned" + datePlanned);
                        dateEditor.putLong("date" + i, datePlanned.getTime());
                    }
                    catch(java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                dateEditor.putInt("numDates", i);
                dateEditor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public int getYear(String date){
        return Integer.parseInt(date.substring(0,4));
    }

    public int getMonth(String date){
        return Integer.parseInt(date.substring(5,7));
    }

    public int getDate(String date){
        return Integer.parseInt(date.substring(8));
    }

    public String convertDate(Date dateClicked){
        String date = dateClicked.toString();
        String convertedDate = date.substring(24);
        convertedDate += ";";
        String month = date.substring(4,7);
        switch(month){
            case "Jan":
                convertedDate += "01;";
                break;
            case "Feb":
                convertedDate += "02;";
                break;
            case "Mar":
                convertedDate += "03;";
                break;
            case "Apr":
                convertedDate += "04;";
                break;
            case "May":
                convertedDate += "05;";
                break;
            case "Jun":
                convertedDate += "06;";
                break;
            case "Jul":
                convertedDate += "07;";
                break;
            case "Aug":
                convertedDate += "08;";
                break;
            case "Sep":
                convertedDate += "09;";
                break;
            case "Oct":
                convertedDate += "10;";
                break;
            case "Nov":
                convertedDate += "11;";
                break;
            case "Dec":
                convertedDate += "12;";
                break;
        }
        convertedDate += date.substring(8,10);
        return convertedDate;
    }

}