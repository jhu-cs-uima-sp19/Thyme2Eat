package com.example.homepage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseAlternative extends AppCompatActivity {

    public static ArrayList<Recipe> alternativeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String name = intent.getStringExtra("name");
        setContentView(R.layout.alternative_recipe_list);
        alternativeList = new ArrayList<Recipe>();
        final AlternateRecipeRcViewAdapter rcAdapter = new AlternateRecipeRcViewAdapter(alternativeList);
        DatabaseReference alts = MainActivity.mDatabase.child("plan").child(date).child(name).child("alts");
        alts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date = "";
                String time = "";
                String instruct = "";
                String image = "";
                String title = "";
                ArrayList<Ingredient> ingreds = new ArrayList<>();
                for (DataSnapshot meal : dataSnapshot.getChildren()) {
                    title = meal.getKey();
                    time = "0:00-23:59pm";
                    if (meal.child("time").exists())
                        time = meal.child("time").getValue().toString();
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
                    alternativeList.add(r);
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        RecyclerView rcView = (RecyclerView) findViewById(R.id.alternateList);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        rcView.setAdapter(rcAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_meal_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.back_button){
            startActivity(new Intent(this, MainActivity.class));
        }
        return true;
    }
}
