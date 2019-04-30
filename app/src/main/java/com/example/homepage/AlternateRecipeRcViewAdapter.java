package com.example.homepage;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homepage.dummy.DummyContent.DummyItem;

import java.io.File;
import java.util.ArrayList;

public class AlternateRecipeRcViewAdapter extends RecyclerView.Adapter<AlternateRecipeRcViewAdapter.ViewHolder> {
    public static ArrayList<Recipe> alternatives;
    public static int selected;
    private boolean onBind = true;

    public AlternateRecipeRcViewAdapter(ArrayList<Recipe> alts) {
        selected = -1;
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
        onBind = true;
        ((ViewHolder) holder).bindView(position);
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return alternatives.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
        public final CheckBox checkbox;
        public final TextView title;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.altImage);

            checkbox = (CheckBox) view.findViewById(R.id.alternate_choose);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!onBind) {
                        if (isChecked) {
                            selected = getAdapterPosition();
                        } else {
                            selected = -1;
                        }
                        AlternateRecipeRcViewAdapter.this.notifyDataSetChanged();
                        if (selected == -1) {
                            ChooseAlternative.swap.setEnabled(false);
                            ChooseAlternative.swap.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                        } else {
                            ChooseAlternative.swap.setEnabled(true);
                            ChooseAlternative.swap.setBackgroundTintList(ColorStateList.valueOf(mView.getContext().getResources().getColor(R.color.colorPrimary)));
                        }
                    }
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("alternate ingreds", alternatives.get(getAdapterPosition()).extendedIngredients.toString());
                    Intent intent = new Intent(mView.getContext(), ViewRecipe.class);
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("array", 1);
                    mView.getContext().startActivity(intent);
                }
            });
            title = (TextView) view.findViewById(R.id.titleText);
        }

        public void bindView(int position) {
            onBind = true;
            if (position == selected) {
                this.checkbox.setChecked(true);
            } else {
                this.checkbox.setChecked(false);
            }

            String cache = "/data/user/0/com.example.homepage/cache";
            Recipe recipe = alternatives.get(position);
            File file = new File(cache,
                    recipe.image.substring((recipe.image.lastIndexOf('/') + 1)));
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                image.setImageBitmap(bitmap);
            }

            String name = recipe.title;
            //name = name.substring(10);
            if (name.length() > 12) {
                name = name.substring(0, 10) + "...";
            }
            title.setText(name);
        }
    }
}
