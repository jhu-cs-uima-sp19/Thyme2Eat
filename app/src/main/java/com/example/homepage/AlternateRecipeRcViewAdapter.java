package com.example.homepage;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homepage.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

public class AlternateRecipeRcViewAdapter extends RecyclerView.Adapter<AlternateRecipeRcViewAdapter.ViewHolder> {
    private ArrayList<Recipe> alternatives;

    public AlternateRecipeRcViewAdapter(ArrayList<Recipe> alts) {
        alternatives = alts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alternative_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ((ViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return alternatives.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
        public final CheckBox checkbox;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.altImage);
            checkbox = (CheckBox) view.findViewById(R.id.alternate_choose);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("alternate", "Clicking on " + alternatives.get(getAdapterPosition()).title);
                }
            });
        }

        public void bindView(int position) {

        }
    }
}
