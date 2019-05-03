package com.example.homepage;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorites extends Fragment {

    public static ArrayList<Recipe> favoritesList;


    public Favorites() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_list, container, false);
        if (favoritesList == null) {
            favoritesList = new ArrayList<Recipe>();
        }
        final FavoritesRcViewAdapter rcAdapter = new FavoritesRcViewAdapter();
        RecyclerView rcView = (RecyclerView) view.findViewById(R.id.favsList);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(rcAdapter);
        DatabaseReference favs = MainActivity.mDatabase.child("favs");
        favs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoritesList = new ArrayList<>();
                long id = 0;
                String date = "";
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
                    if (meal.child("id").exists()) {
                        id = Integer.parseInt(meal.child("id").getValue().toString());
                    }
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
                    Recipe r = new Recipe(id, title, date, time, instruct, ingreds, image, duration);
                    favoritesList.add(r);
                }
                rcAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
