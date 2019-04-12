package com.example.homepage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecipeFragment extends Fragment {

    public static ArrayList<Recipe> mealList;
    final RecipesRecyclerViewAdapter rcAdapter = new RecipesRecyclerViewAdapter();
/*
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }
    /*
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeFragment newInstance(int columnCount) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    */

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
                    date = date.replace(";","/");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    date = convertedDate.toString().replace("00:00:00 EDT ", "");
                    for (DataSnapshot meal : dates.getChildren()) {
                        title = meal.getKey();
                        time = "0:00-23:59pm";
                        if (meal.child("time").exists())
                            time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + date);
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
                            new getImage().execute(image);
                        Recipe r = new Recipe(title, date, time, instruct, ingreds, image);
                        mealList.add(r);
                        break;
                    }
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
        /*
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new RecipesRecyclerViewAdapter());
        }
        return view;
        */
    }


/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
    */
    public class getImage extends AsyncTask<String, Bitmap, Bitmap> {

        private Exception exception;
        private Bitmap myBitmap;

        protected Bitmap doInBackground(String... urls) {
            try {
                java.net.URL url = new java.net.URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                String filename = urls[0].substring(urls[0].lastIndexOf('/')+1);
                File file = new File("/data/user/0/com.example.homepage/cache", filename);
                FileOutputStream outputStream = new FileOutputStream(file);
                try {
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            System.out.print("onpost");
            rcAdapter.notifyDataSetChanged();
        }
    }

}
