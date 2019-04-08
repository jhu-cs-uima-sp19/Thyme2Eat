package com.example.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserPreferences extends AppCompatActivity {

    Button confirm;
    EditText exclude;
    EditText include;
    EditText dependents;
    SharedPreferences myPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        myPreferences = getSharedPreferences("oreferences", Context.MODE_PRIVATE);
        editor = myPreferences.edit();
        exclude = findViewById(R.id.editExclusions);
        include = findViewById(R.id.editInclusions);
        dependents = findViewById(R.id.editDependents);


        Spinner cuisine_spinner = (Spinner) findViewById(R.id.cuisine_spinner);
        final String[] select_cuisine = {
                "Select Cuisine(s)", "african", "chinese", "japanese", "korean", "vietnamese", "thai", "indian", "british", "irish", "french", "italian",
                "mexican", "spanish", "middle eastern", "jewish", "american", "cajun", "southern", "greek", "german", "nordic",
                "eastern european", "caribbean", "latin american"};

        final ArrayList<StateVO> cuisine_listVOs = new ArrayList<>();
        for (int i = 0; i < select_cuisine.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(select_cuisine[i]);
            stateVO.setSelected(false);
            cuisine_listVOs.add(stateVO);
        }
        PreferenceAdapter cuisine_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                cuisine_listVOs);
        cuisine_spinner.setAdapter(cuisine_adapter);

        for (StateVO s: cuisine_listVOs) {
            if (myPreferences.contains(s.getTitle())) {
                if (myPreferences.getBoolean(s.getTitle(), false)) {
                    s.setSelected(true);
                }
            }
        }

        final Spinner diet_spinner = (Spinner) findViewById(R.id.diet_spinner);
        final String[] diet = {"Select diet(s)", "pescetarian", "lacto vegetarian", "ovo vegetarian", "vegan", "paleo", "primal", "vegetarian"};

        final ArrayList<StateVO> diet_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(diet[i]);
            stateVO.setSelected(false);
            diet_listVOs.add(stateVO);
        }
        PreferenceAdapter diet_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                diet_listVOs);
        diet_spinner.setAdapter(diet_adapter);

        for (StateVO s: diet_listVOs) {
            if (myPreferences.contains(s.getTitle())) {
                if (myPreferences.getBoolean(s.getTitle(), false)) {
                    s.setSelected(true);
                }
            }
        }

        Spinner allergies_spinner = (Spinner) findViewById(R.id.allergies_spinner);
        final String[] allergies = {
                "Select allergies(s)", "peanut", "soy", "egg", "tree nuts", "wheat", "fish", "shellfish"};

        final ArrayList<StateVO> allergies_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(allergies[i]);
            stateVO.setSelected(false);
            allergies_listVOs.add(stateVO);
        }
        PreferenceAdapter allergies_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                allergies_listVOs);
        allergies_spinner.setAdapter(allergies_adapter);

        for (StateVO s: allergies_listVOs) {
            if (myPreferences.contains(s.getTitle())) {
                if (myPreferences.getBoolean(s.getTitle(), false)) {
                    s.setSelected(true);
                }
            }
        }

        if (myPreferences.contains("include")) {
            include.setText(myPreferences.getString("include", ""), TextView.BufferType.EDITABLE);
        }

        if (myPreferences.contains("exclude")) {
            exclude.setText(myPreferences.getString("exclude", ""), TextView.BufferType.EDITABLE);
        }

        if (myPreferences.contains("dependents")) {
            dependents.setText(String.valueOf(myPreferences.getInt("dependents", 0)), TextView.BufferType.EDITABLE);
        }

        confirm = findViewById(R.id.button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (StateVO s: cuisine_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                }
                for (StateVO s: diet_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                }
                for (StateVO s: allergies_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                }
                if (include.getText().toString() != "") {
                    editor.putString("include", include.getText().toString());
                }
                if (exclude.getText().toString() != "") {
                    editor.putString("exclude", exclude.getText().toString());
                }
                if (dependents.getText().toString() != "") {
                    int x = 0;
                    try {
                        x = Integer.parseInt(dependents.getText().toString());
                    } catch (NumberFormatException e) {
                        editor.putInt("dependents", 0);
                    }
                    editor.putInt("dependents", x);
                }
                editor.commit();
                Toast.makeText(UserPreferences.this, "Preferences Saved!", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
