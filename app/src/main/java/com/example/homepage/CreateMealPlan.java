package com.example.homepage;
//package com.example.compactcalendartrial;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ArrayList<String> selectedDates = new ArrayList<>();

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

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Spoonacular().execute("search", myPreferences.getString("cuisineUrl", ""),
                        myPreferences.getString("dietUrl", ""), myPreferences.getString("includeUrl", ""),
                        myPreferences.getString("excludeUrl", ""), myPreferences.getString("intoleranceUrl", ""),
                        "&type=main+course", String.valueOf(selectedDates.size()), selectedDates.toString());*/
                List<String> dates = new ArrayList<String>(Arrays.asList(selectedDates.toString().split(",")));
                Intent myIntent = new Intent(CreateMealPlan.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                String selectedDateStr = dateClicked.toString().substring(8, 10);
                int selectedDate = Integer.parseInt(selectedDateStr);
                long epoch = dateClicked.getTime();
                if (cDate <= selectedDate) {
                    Log.d(TAG, dateClicked + "");
                    selectedDates.add(dateClicked.toString());
                    Event ev = new Event(Color.BLACK, epoch, "Meal");
                    calendar.addEvent(ev);
                } else {
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

}