package com.example.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference mDatabase;
    //public static ArrayList<Recipe> mealList;
    //public static ArrayList<String> stringShopList;
    private RecipeFragment mealPlan;
    private Fragment settings;
    private Fragment shoppingList;
    private FragmentTransaction transaction;
    private LinearLayout buttonPanel;
    private Menu menu;
    private MenuItem edit;
    private Button addMeals;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editor;

//    public void getShopDatabase(DatabaseReference shopDatabase) {
//        shopDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (mealList == null) {
//            mealList = new ArrayList<Recipe>();
//        }
        myPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = myPreferences.edit();
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference shopDatabase = mDatabase.child("shop");
//        getShopDatabase(shopDatabase);
//        DatabaseReference mealDatabase = mDatabase.child("plan");
//        mealDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mealList = new ArrayList<Recipe>();
//                Log.w("data", "in snap");
//                String date;
//                String time;
//                String instruct;
//                String image = "";
//                String title = "";
//                ArrayList<Ingredient> ingreds;
//                for (DataSnapshot dates : dataSnapshot.getChildren()) {
//                    date = dates.getKey();
//                    date = date.replace(";","/");
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
//                    Date convertedDate = new Date();
//                    try {
//                        convertedDate = dateFormat.parse(date);
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    date = convertedDate.toString().replace("00:00:00 GMT ", "");
//                    for (DataSnapshot meal : dates.getChildren()) {
//                        title = meal.getKey();
//                        time = "0:00-23:59pm";
//                        if (meal.child("time").exists())
//                            time = meal.child("time").getValue().toString();
//                        Log.w("myApp", time + date);
//                        instruct = "Insert Instructions Here";
//                        if (meal.child("instructions").exists())
//                            instruct = meal.child("instructions").getValue().toString();
//                        ingreds = new ArrayList<>();
//                        if (meal.child("ingredients").exists()) {
//                            for (DataSnapshot ingred : meal.child("ingredients").getChildren()) {
//                                if (ingred.child("amount").exists() && ingred.child("unit").exists()) {
//                                    Ingredient i = new Ingredient(ingred.getKey(),
//                                            Double.parseDouble(ingred.child("amount").getValue().toString()),
//                                            ingred.child("unit").getValue().toString());
//                                    ingreds.add(i);
//                                }
//                            }
//                        }
//                        if (meal.child("image").exists())
//                            image = meal.child("image").getValue().toString();
//                        Recipe r = new Recipe(title, date, time, instruct, ingreds, image);
//                        mealList.add(r);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mealPlan = new RecipeFragment();
        settings = new SettingsFragment();
        shoppingList = new ShoppingListItemsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mealPlan).commit();
        Log.w("myApp", "hello");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addMeals = (Button)findViewById(R.id.addMeals);
        addMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO NOT UNCOMMENT THIS CODE HERE!!!!!
              //new Spoonacular().execute("searchRandom");

                new Spoonacular().execute("search", myPreferences.getString("cuisineUrl", ""),
                        myPreferences.getString("dietUrl", ""), myPreferences.getString("includeUrl", ""),
                        myPreferences.getString("excludeUrl", ""), myPreferences.getString("intoleranceUrl", ""),
                        "&type=main+course", "5");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFrag instanceof MealPlan){
            finish();
        } else if (currentFrag instanceof SettingsFragment) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mealPlan);
            transaction.addToBackStack(null);
            buttonPanel = findViewById(R.id.buttonPanel);
            buttonPanel.setVisibility(View.VISIBLE);
            addMeals.setVisibility(View.VISIBLE);
            setTitle("Meal Plan");
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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
            buttonPanel = findViewById(R.id.buttonPanel);
            buttonPanel.setVisibility(View.VISIBLE);
            addMeals.setVisibility(View.VISIBLE);
            setTitle("Meal Plan");
            transaction.commit();
        } else if (id == R.id.nav_list) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, shoppingList);
            transaction.addToBackStack(null);
            buttonPanel = findViewById(R.id.buttonPanel);
            buttonPanel.setVisibility(View.INVISIBLE);
            addMeals.setVisibility(View.INVISIBLE);
            setTitle("Shopping List");
            transaction.commit();

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_settings) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, settings);
            transaction.addToBackStack(null);
            buttonPanel = findViewById(R.id.buttonPanel);
            buttonPanel.setVisibility(View.INVISIBLE);
            addMeals.setVisibility(View.INVISIBLE);
            setTitle("Settings");
            transaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}