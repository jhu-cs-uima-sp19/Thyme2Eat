package com.example.homepage;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class MyTimeSettingsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MyTimeSettingsFragmentRecyclerViewAdapter.ViewHolder> {

    public MyTimeSettingsFragmentRecyclerViewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_daysettingsfragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        (holder).bindView(position);
    }

    @Override
    public int getItemCount() {
      return TimeSettingsFragment.mealTimes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { ;
        public final TextView mealInfo;


        public ViewHolder(View view) {
            super(view);
            final View mView = view;
            mealInfo = view.findViewById(R.id.mealInfo);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mView.getContext(), v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().toString().equals("Edit Time")) {
                                final NumberFormat f = new DecimalFormat("00");
                                final Calendar c = Calendar.getInstance();
                                final int hour = c.get(Calendar.HOUR_OF_DAY);
                                final int min = c.get(Calendar.MINUTE);
                                Dialog dialog = new Dialog(mView.getContext());
                                dialog.setContentView(R.layout.edit_time);
                                final MealTime r = TimeSettingsFragment.mealTimes.get(getAdapterPosition());
                                final TextView fromText = (TextView) dialog.findViewById(R.id.fromText);
                                fromText.setText("From: " + r.startTime);
                                final TextView toText = (TextView) dialog.findViewById(R.id.toText);
                                toText.setText("To: " + r.endTime);
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
                                                        if (amPm.equals("pm") && hour != 12) {
                                                            hour = hour - 12;
                                                        }
                                                        r.startTime = hour + ":" + f.format(min) + amPm;
                                                        MainActivity.editor.putString("Meal " + r.mealNumber + " start", r.startTime);
                                                        MainActivity.editor.commit();
                                                        fromText.setText("From: " + r.startTime + amPm);
                                                        mealInfo.setText(r.mealType + " " + r.startTime + "-" + r.endTime);
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
                                                        if (amPm.equals("pm") && hour != 12) {
                                                            hour = hour - 12;
                                                        }

                                                        r.endTime = hour + ":" + f.format(min) + amPm;
                                                        MainActivity.editor.putString("Meal " + r.mealNumber + " end", r.endTime);
                                                        MainActivity.editor.commit();
                                                        toText.setText("To: " + r.endTime + amPm);
                                                        mealInfo.setText(r.mealType + " " + r.startTime + "-" + r.endTime);
                                                    }
                                                }, hour, min, false);
                                        timePickerDialog.show();
                                    }
                                });
                                dialog.show();
                            }
                            return true;
                        }
                    });
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.inflate(R.menu.popup_time);
                    popupMenu.show();
                    return false;
                }
            });
        }

        public void bindView(int position) {
            MealTime meal = TimeSettingsFragment.mealTimes.get(position);
            mealInfo.setText(meal.mealType + " " + meal.startTime + "-" + meal.endTime);
        }

    }

    public String amOrpm (int hour) {
        if (hour < 12) {
            return "am";
        } else {
            return "pm";
        }
    }
}
