package com.example.homepage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference mDatabase;
    public static ArrayList<Recipe> mealList;
    public static ArrayList<String> stringShopList;
    private RecipeFragment mealPlan;
    private Fragment editMealPlan;
    private FragmentTransaction transaction;
    private Menu menu;
    private MenuItem edit;
    private Button addMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mealList == null) {
            mealList = new ArrayList<Recipe>();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("plan");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealList = new ArrayList<Recipe>();
                Log.w("data", "in snap");
                String date;
                String time;
                String instruct;
                String image = "";
                String title = "";
                ArrayList<Ingredient> ingreds;
                for (DataSnapshot dates : dataSnapshot.getChildren()) {
                    date = dates.getKey();
                    date = date.replace(";","/");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    date = convertedDate.toString().replace("00:00:00 GMT ", "");
                    for (DataSnapshot meal : dates.getChildren()) {
                        title = meal.getKey();
                        time = "0:00-23:59pm";
                        if (meal.child("time").exists())
                            time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + date);
                        instruct = "Insert Instructions Here";
                        if (meal.child("instructions").exists())
                            instruct = meal.child("instructions").getValue().toString();
                        ingreds = new ArrayList<>();
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
                        if (meal.child("image").exists())
                            image = meal.child("image").getValue().toString();
                        Recipe r = new Recipe(title, date, time, instruct, ingreds, image);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mealPlan = new RecipeFragment();
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
            }
        });
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
            Intent intent = new Intent(this, UserPreferences.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class getImage extends AsyncTask<String, Bitmap, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            try {
                java.net.URL url = new java.net.URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                String filename = urls[0].substring(urls[0].lastIndexOf('/')+1);
                File file = new File(getCacheDir(), filename);
                FileOutputStream outputStream = new FileOutputStream(file);
                try {
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }


}