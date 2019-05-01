package com.example.homepage;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChooseAlternative extends AppCompatActivity {

    public static ArrayList<Recipe> alternativeList;
    public static FloatingActionButton swap;
    public static String spoonacularArgs[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Alternate Recipes");
        Intent intent = getIntent();
        final String date = intent.getStringExtra("date");
        final String oldName = intent.getStringExtra("name");
        int pos = intent.getIntExtra("pos", 0);
        final Recipe oldRecipe = RecipeFragment.mealList.get(pos);
        setContentView(R.layout.alternative_recipe_list);
        alternativeList = new ArrayList<Recipe>();
        final AlternateRecipeRcViewAdapter rcAdapter = new AlternateRecipeRcViewAdapter(alternativeList);
        DatabaseReference alts = MainActivity.mDatabase.child("plan").child(date).child(oldName).child("alts");
        alts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String altDate = date;
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
                    Recipe r = new Recipe(title, altDate, time, instruct, ingreds, image, duration);
                    alternativeList.add(r);
                }
                rcAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        RecyclerView rcView = (RecyclerView) findViewById(R.id.alternateList);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        rcView.setAdapter(rcAdapter);
        swap = (FloatingActionButton) findViewById(R.id.swapButton);
        if (rcAdapter.selected == -1) {
            swap.setEnabled(false);
            swap.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        } else {
            swap.setEnabled(true);
            swap.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("swap", "clicked");
                final Recipe r = rcAdapter.alternatives.get(rcAdapter.selected);
                final DatabaseReference db = MainActivity.mDatabase.child("plan").child(r.date);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot oldAlts = dataSnapshot.child(oldName).child("alts");
                        db.child(r.title).setValue(oldAlts.child(r.title).getValue());
                        DatabaseReference newRecipeAlts = db.child(r.title).child("alts");
                        newRecipeAlts.setValue(oldAlts.getValue());
                        newRecipeAlts.child(r.title).setValue(null);
                        newRecipeAlts.child(oldName).setValue(dataSnapshot.child(oldName).getValue());
                        newRecipeAlts.child(oldName).child("alts").setValue(null);
                        Log.w("replacing", oldName);
                        db.child(oldName).setValue(null);

                        ChooseAlternative.spoonacularArgs = new String[]{rcAdapter.selected + "", r.date, "true", "false", ""};
                        //Spoonacular.updateShopList(r, r.date, true, false, "");
                        Spoonacular.skip = true;
                        RecipesRecyclerViewAdapter.deleteIngreds(ChooseAlternative.this, oldRecipe);
                        Log.w("deleting ingreds", oldRecipe.extendedIngredients.toString());
                        new Spoonacular(ChooseAlternative.this).execute("updateShop", "alts");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                finish();
            }
        });
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
