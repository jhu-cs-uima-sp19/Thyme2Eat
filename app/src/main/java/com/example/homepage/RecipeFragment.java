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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.widget.TextView;


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
        final TextView emptyView = (TextView)view.findViewById(R.id.empty_view);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(rcAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy;MM;dd");
        final String curDate = sdf.format(new Date());

        if (mealList == null) {
            mealList = new ArrayList<Recipe>();
        }
        //Log.w("here", "" + MainActivity.mealList.size());
        final DatabaseReference mealDatabase = MainActivity.mDatabase.child("plan");
        mealDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealList = new ArrayList<Recipe>();
                Log.w("data", "in snap");
                long id = 0;
                String date;
                String time;
                String instruct;
                String image = "";
                String title = "";
                ArrayList<Ingredient> ingreds;
                int duration = 60;
                for (DataSnapshot dates : dataSnapshot.getChildren()) {
                    date = dates.getKey();
//                    if (date.compareTo(curDate) < 0) {
//                        mealDatabase.child(date).setValue(null);
//                        continue;
//                    }
                    String dateText = Recipe.makeDateText(date);
                    //Boolean prev = false;
                    for (DataSnapshot meal : dates.getChildren()) {
                        if (date.compareTo(curDate) < 0) {
                            mealDatabase.child(date).setValue(null);
                            //continue;
                        }
                        title = meal.getKey();
                        time = "0:00-23:59pm";
                        if (meal.child("id").exists()) {
                            id = Integer.parseInt(meal.child("id").getValue().toString());
                        }
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
                        if (meal.child("image").exists()) {
                            image = meal.child("image").getValue().toString();
                        }

                        if (meal.child("readyInMinutes").exists()) {
                            duration = Integer.parseInt(meal.child("readyInMinutes").getValue().toString());
                        }

                        Recipe r = new Recipe(id, title, date, time, instruct, ingreds, image, duration);
                        if (meal.child("alts").exists()) {
                            r.hasAlts = true;
                        }
                        r.dateText = dateText;
                        //r.withPrev = prev;
                        if (meal.child("id").exists())
                            r.id = (long)meal.child("id").getValue();
                        //prev = true;
                        if (date.compareTo(curDate) < 0) {
                            rcAdapter.deleteIngreds(getContext(), r);
                        } else {
                            mealList.add(r);
                        }
                    }
                }
                Collections.sort(mealList, new RecipesRecyclerViewAdapter.CustomComparator());
                if (mealList.size() > 0) {
                    mealList.get(0).withPrev = false;
                    for (int i = 1; i < mealList.size(); i++) {
                        Recipe cur = mealList.get(i);
                        Recipe prev = mealList.get(i - 1);
                        if (prev.date.equals(cur.date)) {
                            cur.withPrev = true;
                        } else {
                            cur.withPrev = false;
                        }
                    }
                }
                rcAdapter.notifyDataSetChanged();
                if (mealList.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
