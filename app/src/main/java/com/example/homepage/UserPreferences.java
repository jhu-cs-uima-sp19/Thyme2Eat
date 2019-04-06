package com.example.homepage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import java.util.ArrayList;

public class UserPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        Spinner cuisine_spinner = (Spinner) findViewById(R.id.cuisine_spinner);
        final String[] select_cuisine = {
                "Select Cuisine(s)", "african", "chinese", "japanese", "korean", "vietnamese", "thai", "indian", "british", "irish", "french", "italian",
                "mexican", "spanish", "middle eastern", "jewish", "american", "cajun", "southern", "greek", "german", "nordic",
                "eastern european", "caribbean", "latin american"};

        ArrayList<StateVO> cuisine_listVOs = new ArrayList<>();
        for (int i = 0; i < select_cuisine.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(select_cuisine[i]);
            stateVO.setSelected(false);
            cuisine_listVOs.add(stateVO);
        }
        PreferenceAdapter cuisine_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                cuisine_listVOs);
        cuisine_spinner.setAdapter(cuisine_adapter);

        Spinner diet_spinner = (Spinner) findViewById(R.id.diet_spinner);
        final String[] diet = {"Select diet(s)", "pescetarian", "lacto vegetarian", "ovo vegetarian", "vegan", "paleo", "primal", "vegetarian"};

        ArrayList<StateVO> diet_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(diet[i]);
            stateVO.setSelected(false);
            diet_listVOs.add(stateVO);
        }
        PreferenceAdapter diet_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                diet_listVOs);
        diet_spinner.setAdapter(diet_adapter);

        Spinner allergies_spinner = (Spinner) findViewById(R.id.allergies_spinner);
        final String[] allergies = {
                "Select allergies(s)", "peanut", "soy", "egg", "tree nuts", "wheat", "fish", "shellfish"};

        ArrayList<StateVO> allergies_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(allergies[i]);
            stateVO.setSelected(false);
            allergies_listVOs.add(stateVO);
        }
        PreferenceAdapter allergies_adapter = new PreferenceAdapter(UserPreferences.this, 0,
                allergies_listVOs);
        allergies_spinner.setAdapter(allergies_adapter);

    }
}
