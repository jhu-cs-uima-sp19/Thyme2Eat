package com.example.homepage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    Button confirm;
    EditText exclude;
    EditText include;
    EditText dependents;
    SharedPreferences myPreferences;
    SharedPreferences.Editor editor;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        myPreferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = myPreferences.edit();
        exclude = view.findViewById(R.id.editExclusions);
        include = view.findViewById(R.id.editInclusions);
        dependents = view.findViewById(R.id.editDependents);


        Spinner cuisine_spinner = (Spinner) view.findViewById(R.id.cuisine_spinner);
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
        PreferenceAdapter cuisine_adapter = new PreferenceAdapter(getContext(), 0,
                cuisine_listVOs);
        cuisine_spinner.setAdapter(cuisine_adapter);

        for (StateVO s: cuisine_listVOs) {
            if (myPreferences.contains(s.getTitle())) {
                if (myPreferences.getBoolean(s.getTitle(), false)) {
                    s.setSelected(true);
                }
            }
        }

        final Spinner diet_spinner = (Spinner) view.findViewById(R.id.diet_spinner);
        final String[] diet = {"Select diet(s)", "pescetarian", "lacto vegetarian", "ovo vegetarian", "vegan", "paleo", "primal", "vegetarian"};

        final ArrayList<StateVO> diet_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(diet[i]);
            stateVO.setSelected(false);
            diet_listVOs.add(stateVO);
        }
        PreferenceAdapter diet_adapter = new PreferenceAdapter(getContext(), 0,
                diet_listVOs);
        diet_spinner.setAdapter(diet_adapter);

        for (StateVO s: diet_listVOs) {
            if (myPreferences.contains(s.getTitle())) {
                if (myPreferences.getBoolean(s.getTitle(), false)) {
                    s.setSelected(true);
                }
            }
        }

        Spinner allergies_spinner = (Spinner) view.findViewById(R.id.allergies_spinner);
        final String[] allergies = {
                "Select allergies(s)", "peanut", "soy", "egg", "tree nuts", "wheat", "fish", "shellfish"};

        final ArrayList<StateVO> allergies_listVOs = new ArrayList<>();
        for (int i = 0; i < diet.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(allergies[i]);
            stateVO.setSelected(false);
            allergies_listVOs.add(stateVO);
        }
        PreferenceAdapter allergies_adapter = new PreferenceAdapter(getContext(), 0,
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

        confirm = view.findViewById(R.id.button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuisineUrl = "cuisine=";
                for (StateVO s: cuisine_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                    if (s.isSelected()) {
                        cuisineUrl += s.getTitle() + "%2C";
                    }
                }
                cuisineUrl = cuisineUrl.replace(" ", "+");
                if (cuisineUrl.lastIndexOf('%') != -1)
                    cuisineUrl = cuisineUrl.substring(0, cuisineUrl.lastIndexOf('%'));
                if (!cuisineUrl.equals("cuisine="))
                    editor.putString("cuisineUrl", cuisineUrl);
                else
                    editor.putString("cuisineUrl", "");
                String dietUrl = "&diet=";
                for (StateVO s: diet_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                    if (s.isSelected()) {
                        dietUrl += s.getTitle() + "%2C";
                    }
                }
                dietUrl = dietUrl.replace(" ", "+");
                if (dietUrl.lastIndexOf('%') != -1)
                    dietUrl = dietUrl.substring(0, dietUrl.lastIndexOf('%'));
                if (!dietUrl.equals("&diet="))
                    editor.putString("dietUrl", dietUrl);
                else
                    editor.putString("dietUrl", "");
                String intoleranceUrl = "&intolerances=";
                for (StateVO s: allergies_listVOs) {
                    editor.putBoolean(s.getTitle(), s.isSelected());
                    if (s.isSelected()) {
                        intoleranceUrl += s.getTitle() + "%2C";
                    }
                }
                intoleranceUrl = intoleranceUrl.replace(" ", "+");
                if (intoleranceUrl.lastIndexOf('%') != -1)
                    intoleranceUrl = intoleranceUrl.substring(0, intoleranceUrl.lastIndexOf('%'));
                if (intoleranceUrl != "&intolerances=")
                    editor.putString("intoleranceURL", intoleranceUrl);
                else
                    editor.putString("intoleranceURL", "");
                String includeUrl = "&includeIngredients=" + include.getText().toString();
                if (include.getText().toString() != "") {
                    editor.putString("include", include.getText().toString());
                }
                includeUrl = includeUrl.replace(",", "%2C");
                includeUrl = includeUrl.replace(" ", "+");
                if (!include.toString().equals(""))
                    editor.putString("includeUrl", includeUrl);
                else
                    editor.putString("includeUrl","");
                System.out.println(includeUrl);
                String excludeUrl = "&excludeIngredients=" + exclude.getText().toString();
                if (exclude.getText().toString() != "") {
                    editor.putString("exclude", exclude.getText().toString());
                }
                excludeUrl = excludeUrl.replace(",", "%2C");
                excludeUrl = excludeUrl.replace(" ", "+");
                if (!exclude.toString().equals(""))
                    editor.putString("excludeUrl", excludeUrl);
                else
                    editor.putString("excludeUrl", "");
                System.out.println(excludeUrl);
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
                Toast.makeText(getContext(), "Preferences Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}