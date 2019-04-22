package com.example.homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TimeSettingsFragment extends Fragment {

    final MyTimeSettingsFragmentRecyclerViewAdapter rvAdapter = new MyTimeSettingsFragmentRecyclerViewAdapter();
    public static ArrayList<MealTime> mealTimes = new ArrayList<>();

    public TimeSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealTimes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String start = MainActivity.myPreferences.getString("Meal " + i + " start", "2:00");
            String end = MainActivity.myPreferences.getString("Meal " + i + " end", "3:00");
            mealTimes.add(new MealTime(i, start, end, "main+course"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daysettingsfragment_list, container, false);
        RecyclerView rcView = (RecyclerView) view.findViewById(R.id.time_list);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(rvAdapter);
        return view;
    }

}
