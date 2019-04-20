package com.example.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;

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
    TextView monthTV;

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
        myPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = myPreferences.edit();
        final Date todayDate = new Date();
        monthTV = (TextView) findViewById(R.id.monthTV);
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        String month = convertMonth(m);
        monthTV.setText(month + " " + y);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dates =  "";
                for (Date d: selectedDates) {
                    dates+=convertDate(d);
                }
                new Spoonacular(CreateMealPlan.this).execute("search", myPreferences.getString("cuisineUrl", ""),
                        myPreferences.getString("dietUrl", ""), myPreferences.getString("includeUrl", ""),
                        myPreferences.getString("excludeUrl", ""), myPreferences.getString("intoleranceUrl", ""),
                        "&type=main+course", "50", dates);
            }
        });

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                long epoch = dateClicked.getTime();
                Event ev = new Event(Color.BLACK, epoch, "Meal");
                if (!todayDate.after(dateClicked) && !selectedDates.contains(dateClicked)) {
                    selectedDates.add(dateClicked);
                    calendar.addEvent(ev);
                }else if (!todayDate.after(dateClicked) && selectedDates.contains(dateClicked)){
                    selectedDates.remove(dateClicked);
                    calendar.removeEvent(ev);
                }
                else {
                    Toast.makeText(context, "Error: Past Date", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                int m = firstDayOfNewMonth.getMonth();
                monthTV.setText(convertMonth(m) + " " + (firstDayOfNewMonth.getYear() + 1900));
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

    public String convertMonth(int m){
        m += 1;
        String month = "";
        switch(m){
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return month;

    }

}