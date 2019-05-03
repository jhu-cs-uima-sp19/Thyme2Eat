package com.example.homepage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class ViewRecipeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private TextView recipe_text;
    private String recipe = "";
    private Bitmap image;
    private ImageView imageView;
    private TextView title;
    private FloatingActionButton favButton;

    public ViewRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewRecipeFragment newInstance(String param1, String param2) {
        ViewRecipeFragment fragment = new ViewRecipeFragment();
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
    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            recipe = (String)dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_recipe, container, false);
        imageView = view.findViewById(R.id.recipeImage);
        favButton = (FloatingActionButton) view.findViewById(R.id.fav_button);
        final Recipe r = ViewRecipe.r;
        if (ViewRecipe.fav) {
            favButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.favd_star));
        } else {
            favButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star));
        }
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewRecipe.fav) {
                    favButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star));
                    ViewRecipe.fav = false;
                } else {
                    favButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.favd_star));
                    ViewRecipe.fav = true;
                }
            }
        });

        title = view.findViewById(R.id.recipeTitle);
        title.setText(ViewRecipe.title);
        File file = new File(getContext().getCacheDir(), ViewRecipe.imageUrl.substring(ViewRecipe.imageUrl.lastIndexOf('/') + 1));
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
            imageView.setImageBitmap(bitmap);

        }
        recipe_text = (TextView)view.findViewById(R.id.recipe_text);
        ViewRecipe.instructions = ViewRecipe.instructions.replace(".", ".\n\n");
        recipe_text.setText(ViewRecipe.instructions);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
