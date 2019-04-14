package com.example.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;

public class CreateMealPlan extends AppCompatActivity{

    private static final String TAG = "Calendar";
    CompactCalendarView calendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM-yyyy",
            Locale.getDefault());
    int cDate;
    Button confirmBtn;
    TextView planTextView;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Date> selectedDates = new ArrayList<>();
    List<Date> datesPicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal_plan);

        calendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        calendar.setUseThreeLetterAbbreviation(true);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        confirmBtn.setText("Confirm");
        planTextView = (TextView) findViewById(R.id.planTextView);
        planTextView.setText("Tap the dates to plan for!");
        Calendar calendarDate = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendarDate.getTime());
        String currDate = currentDate.substring(4,6);
        cDate = Integer.parseInt(currDate);
        datesPicked = new ArrayList<>();
        myPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = myPreferences.edit();
        final Date todayDate = new Date();


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dates =  "";
                for (Date d: selectedDates) {
                    dates+=convertDate(d);
                }
                new Spoonacular().execute("search", myPreferences.getString("cuisineUrl", ""),
                        myPreferences.getString("dietUrl", ""), myPreferences.getString("includeUrl", ""),
                        myPreferences.getString("excludeUrl", ""), myPreferences.getString("intoleranceUrl", ""),
                        "&type=main+course", String.valueOf(selectedDates.size()), dates);
                Intent myIntent = new Intent(CreateMealPlan.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                long epoch = dateClicked.getTime();
                Event ev = new Event(Color.BLACK, epoch, "Meal");
                if (!todayDate.after(dateClicked) && !datesPicked.contains(dateClicked)) {
                    selectedDates.add(dateClicked);
                    calendar.addEvent(ev);
                    //Log.d(TAG, todayDate + " " + dateClicked);
                }else if (!todayDate.after(dateClicked) && datesPicked.contains(dateClicked)){
                    datesPicked.remove(dateClicked);
                    calendar.removeEvent(ev);
                }
                else {
                    Log.d(TAG, "error: past date");
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "nothing");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_meal_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.back_button){
            startActivity(new Intent(this, MainActivity.class));
        }
        return true;
    }

    public String convertDate(Date dateClicked){
        String date = dateClicked.toString();
        String convertedDate = date.substring(24);
        convertedDate += ";";
        String month = date.substring(4,7);
        switch(month){
            case "Jan":
                convertedDate += "01;";
                break;
            case "Feb":
                convertedDate += "02;";
                break;
            case "Mar":
                convertedDate += "03;";
                break;
            case "Apr":
                convertedDate += "04;";
                break;
            case "May":
                convertedDate += "05;";
                break;
            case "Jun":
                convertedDate += "06;";
                break;
            case "Jul":
                convertedDate += "07;";
                break;
            case "Aug":
                convertedDate += "08;";
                break;
            case "Sep":
                convertedDate += "09;";
                break;
            case "Oct":
                convertedDate += "10;";
                break;
            case "Nov":
                convertedDate += "11;";
                break;
            case "Dec":
                convertedDate += "12;";
                break;
        }
        convertedDate += date.substring(8,10);
        return convertedDate;
    }

}