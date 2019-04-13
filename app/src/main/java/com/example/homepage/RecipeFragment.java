package com.example.homepage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecipeFragment extends Fragment {

    public static ArrayList<Recipe> mealList;
    final RecipesRecyclerViewAdapter rcAdapter = new RecipesRecyclerViewAdapter();

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.w("myApp", "atRecFrag");
        View view = inflater.inflate(R.layout.fragment_recipeschedule_list, container, false);
        RecyclerView rcView = (RecyclerView) view.findViewById(R.id.planList);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(rcAdapter);

        if (mealList == null) {
            mealList = new ArrayList<Recipe>();
        }
        //Log.w("here", "" + MainActivity.mealList.size());
        DatabaseReference mealDatabase = MainActivity.mDatabase.child("plan");
        mealDatabase.addValueEventListener(new ValueEventListener() {
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
                    /*
                    String dateText = date.replace(";","/");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(dateText);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    dateText = convertedDate.toString().replace("00:00:00 EDT ", "");
                    */
                    String dateText = Recipe.makeDateText(date);
                    Boolean prev = false;
                    for (DataSnapshot meal : dates.getChildren()) {
                        title = meal.getKey();
                        time = "0:00-23:59pm";
                        if (meal.child("time").exists())
                            time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + dateText);
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
                        r.dateText = dateText;
                        r.withPrev = prev;
                        prev = true;
                        mealList.add(r);
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
