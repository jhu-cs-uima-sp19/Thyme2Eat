package com.example.homepage;
//package com.example.compactcalendartrial;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.util.Log;


public class CreateMealPlan extends AppCompatActivity {

    private static final String TAG = "Calendar";
    CompactCalendarView calendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMM-yyyy",
            Locale.getDefault());
    int cDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal_plan);

        calendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        calendar.setUseThreeLetterAbbreviation(true);
        Calendar calendarDate = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendarDate.getTime());
        String currDate = currentDate.substring(4,6);
        cDate = Integer.parseInt(currDate);

        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                String selectedDateStr = dateClicked.toString().substring(8, 10);
                int selectedDate = Integer.parseInt(selectedDateStr);
                long epoch = dateClicked.getTime();
                if (cDate <= selectedDate) {
                    Log.d(TAG, dateClicked + "");
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
}
