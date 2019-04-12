package com.example.homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

public class MealPlan extends Fragment {
    public RecyclerView recyclerView;
    private RecipesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public MealPlan() {
        // Required empty public constructor
    }

    public static MealPlan newInstance(String param1, String param2) {
        MealPlan fragment = new MealPlan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
