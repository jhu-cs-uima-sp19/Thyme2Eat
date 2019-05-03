package com.example.homepage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.homepage.dummy.DummyContent.DummyItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class FavoritesRcViewAdapter extends RecyclerView.Adapter<FavoritesRcViewAdapter.ViewHolder> {

    public FavoritesRcViewAdapter() {

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ((ViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return Favorites.favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
        public final TextView title;
        public final FloatingActionButton addButton;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.favImage);
            addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                    final NumberFormat f = new DecimalFormat("00");
                                    final Recipe r = Favorites.favoritesList.get(getAdapterPosition());
                                    r.date = year + ";" +
                                            String.valueOf(f.format(month)) + ";" + String.valueOf(f.format(day));
                                    r.dateText = Recipe.makeDateText(r.date);
                                    final Calendar c = Calendar.getInstance();
                                    final int hour = c.get(Calendar.HOUR_OF_DAY);
                                    final int min = c.get(Calendar.MINUTE);
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
                                                    final DatabaseReference db = MainActivity.mDatabase;
                                                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Spoonacular.skip = true;
                                                            db.child("plan").child(r.date).child(r.title).setValue(dataSnapshot.child("favs").child(r.title).getValue());
                                                            MainActivity.mDatabase.child("plan").child(r.date).child(r.title).child("time").setValue(r.time);
                                                            new Spoonacular(mView.getContext()).execute("updateShop", "favs", getAdapterPosition() + "");
                                                            Toast.makeText(mView.getContext(), "Recipe added to the meal plan!",
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }, hour, min, true);
                                    timePickerDialog.show();
                                }
                            }, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                    datePickerDialog.show();
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mView.getContext(), ViewRecipe.class);
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("array", 2);
                    mView.getContext().startActivity(intent);
                }
            });
            title = (TextView) view.findViewById(R.id.favTitle);
        }

        public void bindView(int position) {

            String cache = "/data/user/0/com.example.homepage/cache";
            Recipe recipe = Favorites.favoritesList.get(position);
            File file = new File(cache,
                    recipe.image.substring((recipe.image.lastIndexOf('/') + 1)));
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                image.setImageBitmap(bitmap);
            }

            String name = Favorites.favoritesList.get(position).title;
            //name = name.substring(10);
            if (name.length() > 20) {
                name = name.substring(0, 20) + "...";
            }
            title.setText(name);
        }
    }
}
