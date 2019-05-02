package com.example.homepage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    public static double convertedAmount = -1;

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
        public final FloatingActionButton alt;
        public Bitmap image;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateView = (TextView) view.findViewById(R.id.dateText);
            timeView = (TextView) view.findViewById(R.id.timeText);
            imageView = (ImageView) view.findViewById(R.id.image);
            alt = (FloatingActionButton) view.findViewById(R.id.choose_alternate);

            alt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Recipe r = RecipeFragment.mealList.get(getAdapterPosition());
                    Intent intent = new Intent(mView.getContext(), ChooseAlternative.class);
                    intent.putExtra("date", r.date);
                    intent.putExtra("name", r.title);
                    intent.putExtra("pos", getAdapterPosition());
                    System.out.println(r.title);
                    System.out.println(r.date);
                    if (r.hasAlts) {
                        mView.getContext().startActivity(intent);
                    } else {
                        new Spoonacular(mView.getContext()).execute("getSimilar", String.valueOf(r.id), r.title, r.date);
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), ViewRecipe.class);
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("array", 0);
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
                                        final Recipe r;
                                        if (getAdapterPosition() == -1) {
                                            r = RecipeFragment.mealList.get(0);
                                        } else {
                                            r = RecipeFragment.mealList.get(getAdapterPosition());
                                        }
                                        //final DatabaseReference shop = MainActivity.mDatabase.child("shop");
                                        MainActivity.mDatabase.child("plan").child(r.getDate()).child(r.title).setValue(null);
                                        RecipeFragment.mealList.remove(getAdapterPosition());
                                        RecipesRecyclerViewAdapter.deleteIngreds(mView.getContext(), r);
//                                        shop.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot shopSnap) {
//                                                for (Ingredient i : r.extendedIngredients) {
//                                                    if (shopSnap.child(i.name).exists()) {
//                                                        DataSnapshot ingred = shopSnap.child(i.name);
//                                                        double subVal;
//                                                        if (!i.unit.equals(ingred.child("unit").getValue().toString())) {
//                                                            Spoonacular.convert = true;
//                                                            new Spoonacular(mView.getContext()).execute("convert", String.valueOf(i.amount), i.unit, ingred.child("unit").getValue().toString());
//                                                            subVal = convertedAmount;
//                                                        } else {
//                                                            subVal = i.amount;
//                                                        }
//                                                        double newAmount = Double.parseDouble(ingred.child("amount").getValue().toString()) - subVal;
//                                                        if (newAmount > 0) {
//                                                            shop.child(i.name).child("amount").setValue(newAmount);
//                                                        } else {
//                                                            shop.child(i.name).setValue(null);
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
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
                                c.add(Calendar.DATE, 0);
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
                                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                                datePickerDialog.show();
                            } else if (item.getTitle().toString().equals("Edit Start Time")) {
                                final NumberFormat f = new DecimalFormat("00");
                                final Calendar c = Calendar.getInstance();
                                final int hour = c.get(Calendar.HOUR_OF_DAY);
                                final int min = c.get(Calendar.MINUTE);
                                final Recipe r = RecipeFragment.mealList.get(getAdapterPosition());

                                TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hour,
                                                                  int min) {
                                                int addHour = 0;
                                                int addMin = r.readyInMinutes;
                                                int newMin = addMin + min;
                                                int newHour = hour + addHour;
                                                if (newMin >= 60) {
                                                    newHour += newMin / 60;
                                                    newMin = newMin % 60;
                                                }
                                                if (newHour >= 24) {
                                                    newHour -= 24;
                                                }
                                                r.time = f.format(hour) + ":" + f.format(min) + "-" + f.format(newHour) + ":" + f.format(newMin);
                                                Log.w("new time", r.time);
                                                MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
                                                RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
                                                Collections.sort(RecipeFragment.mealList, new CustomComparator());
                                            }
                                        }, hour, min, true);
                                timePickerDialog.show();
//
//                                Dialog dialog = new Dialog(mView.getContext());
//                                dialog.setContentView(R.layout.edit_time);
//                                final TextView fromText = (TextView) dialog.findViewById(R.id.fromText);
//                                Log.w("Index", "" + r.time.substring(0, r.time.indexOf('-')));
//                                fromText.setText("From: " + r.time.substring(0, r.time.indexOf('-')));
//                                final TextView toText = (TextView) dialog.findViewById(R.id.toText);
//                                toText.setText("To: " + r.time.substring(r.time.indexOf('-') + 1));
//                                Button fromButton = (Button) dialog.findViewById(R.id.fromButton);
//                                Button toButton = (Button) dialog.findViewById(R.id.toButton);
//                                fromButton.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
//                                        new TimePickerDialog.OnTimeSetListener() {
//
//                                            @Override
//                                            public void onTimeSet(TimePicker view, int hour,
//                                                                  int min) {
//                                                String amPm = amOrpm(hour);
//                                                if (amPm.equals("pm")) {
//                                                    hour = hour - 12;
//                                                }
//
//                                                r.time = f.format(hour) + ":" + f.format(min) + r.time.substring(r.time.indexOf('-'));
//                                                MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
//                                                fromText.setText("From: " + r.time.substring(0, r.time.indexOf('-')));
//                                                RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
//                                                Collections.sort(RecipeFragment.mealList, new CustomComparator());
//                                            }
//                                        }, hour, min, false);
//                                        timePickerDialog.setCustomTitle(fromText);
//                                        timePickerDialog.show();
//                                    }
//                                });
//                                toButton.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        TimePickerDialog timePickerDialog = new TimePickerDialog(mView.getContext(),
//                                                new TimePickerDialog.OnTimeSetListener() {
//
//                                                    @Override
//                                                    public void onTimeSet(TimePicker view, int hour,
//                                                                          int min) {
//                                                        String amPm = amOrpm(hour);
//                                                        if (amPm.equals("pm")) {
//                                                            hour = hour - 12;
//                                                        }
//
//                                                        r.time = r.time.substring(0, r.time.indexOf('-') + 1) + hour + ":" + f.format(min) + amPm;
//                                                        MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
//                                                        toText.setText("To: " + r.time.substring(r.time.indexOf('-') + 1));
//                                                        RecipesRecyclerViewAdapter.this.notifyDataSetChanged();
//                                                        Collections.sort(RecipeFragment.mealList, new CustomComparator());
//                                                    }
//                                                }, hour, min, false);
//                                        timePickerDialog.show();
//                                    }
//                                });
//                                dialog.show();
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

    public static class CustomComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe r1, Recipe r2) {
            if (!r1.date.equals(r2.date)) {
                return r1.getDate().compareTo(r2.getDate());
            } else {
                return r1.time.compareTo(r2.time);
            }
        }
    }

    public String amOrpm (int hour) {
        if (hour <= 11) {
            return "am";
        } else {
            return "pm";
        }
    }

    public static void deleteIngreds(final Context context, final Recipe r) {
        Log.w("delete", r.extendedIngredients.toString());
        final DatabaseReference shop = MainActivity.mDatabase.child("shop");
        shop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot shopSnap) {
                for (Ingredient i : r.extendedIngredients) {
                    if (shopSnap.child(i.name).exists()) {
                        DataSnapshot ingred = shopSnap.child(i.name);
                        double subVal;
                        String ingredientUnit = ingred.child("unit").getValue().toString();
                        if (!i.unit.equals(ingredientUnit) || !i.unit.contains(ingredientUnit) || !ingredientUnit.contains(i.unit)){
                            if (shopSnap.child(i.name + " :" + i.unit).exists()) {
                                ingred = shopSnap.child(i.name + " :" + i.unit);
                                subVal = i.amount;
                            } else {
                                Spoonacular.skip = true;
                                new Spoonacular(context).execute("convert", String.valueOf(i.amount), i.unit, ingred.child("unit").getValue().toString(), i.name);

                                subVal = convertedAmount;
                            }
                        } else {
                            subVal = i.amount;
                        }
                        double newAmount = Double.parseDouble(ingred.child("amount").getValue().toString()) - subVal;
                        if (newAmount > 0) {
                            shop.child(i.name).child("amount").setValue(newAmount);
                        } else {
                            shop.child(i.name).setValue(null);
                        }
                    } else if(shopSnap.child(i.name + " :" + i.unit).exists()) {
                        DataSnapshot ingred = shopSnap.child(i.name + " :" + i.unit);
                        double newAmount = Double.parseDouble(ingred.child("amount").getValue().toString()) - i.amount;
                        if (newAmount > 0)
                            shop.child(i.name + " :" + i.unit).child("amount").setValue(newAmount);
                        else
                            shop.child(i.name + " :" + i.unit).setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
