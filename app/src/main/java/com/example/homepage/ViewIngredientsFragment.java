package com.example.homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class ViewIngredientsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String ingredients_string = "";
    private HashMap<String, String> ingredients;
    private TextView ingredients_text;
    private View view;

    private DatabaseReference mDatabase;
    private DataSnapshot dataSnapshot;

    public ViewIngredientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewIngredientsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewIngredientsFragment newInstance(String param1, String param2) {
        ViewIngredientsFragment fragment = new ViewIngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ingredients = (HashMap<String, String>)dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_ingredients, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ingredients_text = (TextView)view.findViewById(R.id.ingredients_text);
        if (ingredients_string == "") {
            for (Ingredient i : ViewRecipe.ingredients) {
                ingredients_string += i.toString();
            }
        }
        ingredients_text.setText(ingredients_string);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}