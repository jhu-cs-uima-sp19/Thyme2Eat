package com.example.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    public static ArrayList<Recipe> mealList;
    public static ArrayList<String> stringShopList;
    private RecipeFragment mealPlan;
    private Fragment editMealPlan;
    private FragmentTransaction transaction;
    private Menu menu;
    private MenuItem edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mealList = new ArrayList<Recipe>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("plan");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealList = new ArrayList<Recipe>();
                Log.w("data", "in snap");
                String date;
                String time;
                String instruct;
                ArrayList<Ingredient> ingreds;
                for (DataSnapshot dates : dataSnapshot.getChildren()) {
                    date = dates.getKey();
                    for (DataSnapshot meal : dates.getChildren()) {
                        ingreds = new ArrayList<>();
                        time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + date);
                        instruct = meal.child("instructions").getValue().toString();
                        for (DataSnapshot ingred : meal.child("ingredients").getChildren()) {
                            Ingredient i = new Ingredient(ingred.getKey().toString(),
                                    Double.parseDouble(ingred.child("amount").getValue().toString()),
                                    ingred.child("unit").getValue().toString());
                            ingreds.add(i);
                        }
                        Recipe r = new Recipe(date, time, instruct, ingreds);
                        mealList.add(r);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        editMealPlan = new EditMealPlan();
        mealPlan = new RecipeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mealPlan).commit();
        Log.w("myApp", "hello");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        edit = menu.findItem(R.id.action_edit);
        System.out.println(edit.getTitle());

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit && edit.getTitle().equals("Edit")) {
            edit.setIcon(R.drawable.close);
            edit.setTitle("Close");
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, editMealPlan);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.action_edit && edit.getTitle().equals("Close")) {
            edit.setIcon(R.drawable.edit);
            edit.setTitle("Edit");
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mealPlan);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_list) {

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_favorite) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
