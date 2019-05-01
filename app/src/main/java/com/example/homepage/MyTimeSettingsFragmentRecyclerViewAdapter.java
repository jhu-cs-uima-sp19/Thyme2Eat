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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
        public final CheckBox checkBox;
        public String start;
        public String end;


        public ViewHolder(View view) {
            super(view);
            final View mView = view;
            mealInfo = view.findViewById(R.id.mealInfo);
            checkBox = view.findViewById(R.id.checkbox);

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
                                dialog.setContentView(R.layout.edit_meal_time);
                                final MealTime r = TimeSettingsFragment.mealTimes.get(getAdapterPosition());
                                final TextView fromText = (TextView) dialog.findViewById(R.id.fromText);
                                fromText.setText("From: " + start);
                                final TextView toText = (TextView) dialog.findViewById(R.id.toText);
                                toText.setText("To: " + end);
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
                                                        int temp = 0;
                                                        if (amPm.equals("pm") && hour != 12) {
                                                            temp = hour - 12;
                                                        }
                                                        r.startTime = hour + ":" + f.format(min);
                                                        MainActivity.editor.putString("Meal " + (r.mealNumber + 1) + " start", r.startTime);
                                                        MainActivity.editor.commit();
                                                        fromText.setText("From: " + r.startTime);
                                                        mealInfo.setText("Meal " + (r.mealNumber + 1) + "\n " + r.startTime + "-" + r.endTime);
                                                    }
                                                }, hour, min, true);
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
                                                        int temp = 0;
                                                        if (amPm.equals("pm") && hour != 12) {
                                                            temp = hour - 12;
                                                        }

                                                        r.endTime = hour + ":" + f.format(min);
                                                        MainActivity.editor.putString("Meal " + (r.mealNumber + 1) + " end", r.endTime);
                                                        MainActivity.editor.commit();
                                                        toText.setText("To: " + r.endTime);
                                                        mealInfo.setText("Meal " + (r.mealNumber + 1) + "\n " + r.startTime + "-" + r.endTime);
                                                    }
                                                }, hour, min, true);
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
            start = MainActivity.myPreferences.getString("Meal " + (position + 1) + " start","14:00");
            end = MainActivity.myPreferences.getString("Meal " + (position + 1) + " end","15:00");
            mealInfo.setText("Meal " + (position + 1) + "\n " + start + "-" + end);
            if (MainActivity.myPreferences.getBoolean("Meal " + (position + 1), false)) {
                System.out.println("yes");
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            final int mealNum = position + 1;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()) {
                        MainActivity.editor.putBoolean("Meal " + (mealNum), true);
                        MainActivity.editor.commit();
                    } else {
                        int count = 0;
                        for (int i = 0; i < 3; i++) {
                            if (MainActivity.myPreferences.getBoolean("Meal " + mealNum,false))
                                count++;
                        }
                        if (count == 0) {
                            Toast.makeText(view.getContext(), "You must have at least one meal selected!", Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(true);
                        }
                        else {
                            MainActivity.editor.putBoolean("Meal " + (mealNum), false);
                            MainActivity.editor.commit();
                        }
                    }
                }
            });
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
