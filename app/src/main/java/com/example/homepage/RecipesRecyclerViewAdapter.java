package com.example.homepage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {


    public RecipesRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w("myApp", "atList");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recipeschedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.w("myApp", "atBindView");
        ((ViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return RecipeFragment.mealList.size();
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
                                        Recipe r = RecipeFragment.mealList.get(getAdapterPosition());
                                        MainActivity.mDatabase.child("plan").child(r.getDate()).setValue(null);
                                        RecipeFragment.mealList.remove(getAdapterPosition());
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

                            } else if (item.getTitle().toString().equals("Edit Date")) {
                                final Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH);
                                int day = c.get(Calendar.DAY_OF_MONTH);


                                DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(),
                                        new DatePickerDialog.OnDateSetListener() {

                                            @Override
                                            public void onDateSet(DatePicker view, int year,
                                                                  int month, int day) {
                                                //Month is 0-indexed.
                                                month++;
                                                Log.w("Month", "" + month);
                                                NumberFormat f = new DecimalFormat("00");
                                                Recipe r = RecipeFragment.mealList.get(getAdapterPosition());
                                                String oldDate = r.date;
                                                r.date = year + ";" +
                                                        String.valueOf(f.format(month)) + ";" + String.valueOf(f.format(day));
                                                r.dateText = Recipe.makeDateText(r.date);
                                                r.moveRecipeInFirebase(MainActivity.mDatabase.child("plan"), oldDate);
                                                Collections.sort(RecipeFragment.mealList, new CustomComparator());
                                                RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
                                            }
                                        }, year, month, day);
                                datePickerDialog.show();
                            } else if (item.getTitle().toString().equals("Edit Time")) {
                                final NumberFormat f = new DecimalFormat("00");
                                final Calendar c = Calendar.getInstance();
                                final int hour = c.get(Calendar.HOUR_OF_DAY);
                                final int min = c.get(Calendar.MINUTE);
                                final Recipe r = RecipeFragment.mealList.get(getAdapterPosition());
                                Dialog dialog = new Dialog(mView.getContext());
                                dialog.setContentView(R.layout.edit_time);
                                final TextView fromText = (TextView) dialog.findViewById(R.id.fromText);
                                Log.w("Index", "" + r.time.substring(0, r.time.indexOf('-')));
                                fromText.setText("From: " + r.time.substring(0, r.time.indexOf('-')));
                                final TextView toText = (TextView) dialog.findViewById(R.id.toText);
                                toText.setText("To: " + r.time.substring(r.time.indexOf('-') + 1));
                                Button fromButton = (Button) dialog.findViewById(R.id.fromButton);
                                Button toButton = (Button) dialog.findViewById(R.id.toButton);
                                fromButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hour,
                                                                  int min) {
                                                String amPm = amOrpm(hour);
                                                if (amPm.equals("pm")) {
                                                    hour = hour - 12;
                                                }

                                                r.time = hour + ":" + f.format(min) + r.time.substring(r.time.indexOf('-'));
                                                MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
                                                fromText.setText("From: " + r.time.substring(0, r.time.indexOf('-')));
                                                RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
                                            }
                                        }, hour, min, false);
                                timePickerDialog.show();
                                    }
                                });
                                toButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
                                                new TimePickerDialog.OnTimeSetListener() {

                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hour,
                                                                          int min) {
                                                        String amPm = amOrpm(hour);
                                                        if (amPm.equals("pm")) {
                                                            hour = hour - 12;
                                                        }

                                                        r.time = r.time.substring(0, r.time.indexOf('-') + 1) + hour + ":" + f.format(min) + amPm;
                                                        MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
                                                        toText.setText("To: " + r.time.substring(r.time.indexOf('-') + 1));
                                                        RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
                                                    }
                                                }, hour, min, false);
                                        timePickerDialog.show();
                                    }
                                });
                                dialog.show();
                                // DON'T DELETE. WILL USE FOR 2ND SPRINT
//                                Recipe r = RecipeFragment.mealList.get(getAdapterPosition());
//                                AlertDialog.Builder alert = new AlertDialog.Builder(mView.getContext());
//                                LinearLayout vertical= new LinearLayout(mView.getContext());
//                                vertical.setOrientation(LinearLayout.VERTICAL);
//                                LinearLayout fromLayout = new LinearLayout(mView.getContext());
//                                fromLayout.setOrientation(LinearLayout.HORIZONTAL);
//                                LinearLayout toLayout = new LinearLayout(mView.getContext());
//                                toLayout.setOrientation(LinearLayout.HORIZONTAL);
//                                final TextView from = new TextView(mView.getContext());
//                                final Button fromButton = new Button(mView.getContext());
//                                from.setText("From: " +r.time.substring(0, r.time.indexOf('-')) + " " + r.time.substring(r.time.length() - 2));
//                                fromLayout.addView(from);
//                                fromLayout.addView(fromButton);
//                                final Button toButton = new Button(mView.getContext());
//                                final TextView to = new TextView(mView.getContext());
//                                fromLayout.addView(to);
//                                fromLayout.addView(toButton);
//                                vertical.addView(fromLayout);
//                                vertical.addView(toLayout);
//                                alert.setView(vertical);
//                                alert.show();

//                                final Calendar c = Calendar.getInstance();
//                                int hour = c.get(Calendar.HOUR_OF_DAY);
//                                int min = c.get(Calendar.MINUTE);
//
//                                // Launch Time Picker Dialog
//                                TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
//                                        new TimePickerDialog.OnTimeSetListener() {
//
//                                            @Override
//                                            public void onTimeSet(TimePicker view, int hour,
//                                                                  int min) {
//                                                String amPm = amOrpm(hour);
//                                                if (amPm.equals("pm")) {
//                                                    hour = hour - 12;
//                                                }
//                                                /*
//                                                RecipeFragment.mealList.get(getAdapterPosition()).date = ;
//                                                Collections.sort(RecipeFragment.mealList, new CustomComparator());
//                                                RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
//                                                */
//                                            }
//                                        }, hour, min, false);
//                                timePickerDialog.show();
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
            if (RecipeFragment.mealList.get(getAdapterPosition()).withPrev) {
                dateView.setVisibility(View.GONE);
            } else {
                dateView.setVisibility(View.VISIBLE);
                dateView.setText(RecipeFragment.mealList.get(getAdapterPosition()).dateText);
            }
            timeView.setText(RecipeFragment.mealList.get(position).getTime());
            String cache = "/data/user/0/com.example.homepage/cache";
            Recipe recipe = RecipeFragment.mealList.get(getAdapterPosition());
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

    public class CustomComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe r1, Recipe r2) {
            return r1.getDate().compareTo(r2.getDate());
        }
    }

    public String amOrpm (int hour) {
        if (hour <= 12) {
            return "am";
        } else {
            return "pm";
        }
    }
}
