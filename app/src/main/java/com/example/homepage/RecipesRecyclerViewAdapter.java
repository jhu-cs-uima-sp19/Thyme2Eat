package com.example.homepage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    public RecipesRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "atList");
        /*
        mealList = new ArrayList<Meal>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("plan");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date;
                String time;
                for (DataSnapshot dates : dataSnapshot.getChildren()) {
                    date = dates.getKey();
                    for (DataSnapshot meal : dates.getChildren()) {
                        time = meal.child("time").getValue().toString();
                        Log.w("myApp", time + date);
                        Meal m = new Meal(date, time);
                        mealList.add(m);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipeschedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w("myApp", "atBindView");
        ((ViewHolder) holder).bindView(position);

        /*
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        Log.w("here", "" + MainActivity.mealList.size());
        return MainActivity.mealList.size();
        /*
        if (mealList != null) {
            Log.w("myApp", "atItemCount");
            return mealList.size();
        } else {
            Log.w("myApp", "atItemCount Zero");
            return 1;
        }
        */
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public final TextView dateView;
        public final TextView timeView;
        public final ImageView imageView;
        public Bitmap image;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateView = (TextView) view.findViewById(R.id.dateText);
            timeView = (TextView) view.findViewById(R.id.timeText);
            imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), ViewRecipe.class);
                    intent.putExtra("index", getAdapterPosition());
                    mView.getContext().startActivity(intent);
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mView.getContext(), v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().toString().equals("Delete")) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
                                alert.setMessage("Are you sure you want to delete this recipe from your meal plan?");
                                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick (DialogInterface dialog, int which) {
                                        Recipe r = MainActivity.mealList.get(getAdapterPosition());
                                        MainActivity.mDatabase.child(r.getDate()).setValue(null);
                                        MainActivity.mealList.remove(getAdapterPosition());
                                        RecipesRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                                        dialog.cancel();
                                    }
                                });
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick (DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                                /*
                                new AlertDialog.Builder(mView.getContext())
                                        .setMessage(
                                                "Are you sure you want to delete this recipe from your meal plan?")
                                        .setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick (DialogInterface dialog, int which) {
                                                        Recipe r = MainActivity.mealList.get(getAdapterPosition());
                                                        MainActivity.mDatabase.child(r.getDate()).setValue(null);
                                                        MainActivity.mealList.remove(getAdapterPosition());
                                                        RecipesRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                                */
                                /*
                                Recipe r = MainActivity.mealList.get(getAdapterPosition());
                                MainActivity.mDatabase.child(r.getDate()).setValue(null);
                                MainActivity.mealList.remove(getAdapterPosition());
                                RecipesRecyclerViewAdapter.this.notifyItemRemoved(getAdapterPosition());
                                */
                            }
                            return true;
                        }
                    });
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.show();

                    return false;
                }
            });
            view.setOnClickListener(this);
        }

        public void bindView(int position) {
            dateView.setText(MainActivity.mealList.get(position).getDate());
            timeView.setText(MainActivity.mealList.get(position).getTime());
            String cache = "/data/user/0/com.example.homepage/cache";
            Recipe recipe = MainActivity.mealList.get(getAdapterPosition());
            File file = new File(cache,
                    recipe.image.substring((recipe.image.lastIndexOf('/') + 1)));
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                imageView.setImageBitmap(bitmap);
            }
        }

        public void onClick(View view) {

        }

    }

}
