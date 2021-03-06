package com.example.witek.organizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
//TODO zmienić schemat karty
//TODO dodać imput dialog

public class AddFoodFormEvent extends AppCompatActivity {
    public static boolean isAddItemEventActive;
    private static Button dataText;
    private static Button timeText;
    private static String date;
    private static String time;
    public MenuItem addEventItem;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    RecyclerViewSwipeManager swipeManager;
    int id = 0;
    private List<FoodFormEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        dataText = (Button) findViewById(R.id.foodDateField);
        timeText = (Button) findViewById(R.id.foodTimeField);
        try {
            dataText.setText(getIntent().getExtras().getString("date"));
            timeText.setText(getIntent().getExtras().getString("time"));
            date = dataText.getText() + "";
            time = timeText.getText() + "";

        } catch (NullPointerException exc) {
            Toast.makeText(getApplicationContext(), "Wystąpił błąd: " + exc.getMessage(),
                           Toast.LENGTH_SHORT).show();
        }
        recyclerView = (RecyclerView) findViewById(R.id.foodRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        recyclerView.setItemAnimator(animator);
        swipeManager = new RecyclerViewSwipeManager();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        for (int i = 0; i < 1; i++) {
            FoodFormEvent foodFormEvent = new FoodFormEvent(new Event("", "", getDate(), getTime()),
                                                            "");
            foodFormEvent.setId(++id);
            events.add(foodFormEvent);
        }

        adapter = new FoodFormEventAdapter(events, AddFoodFormEvent.this);
        recyclerView.setAdapter(swipeManager.createWrappedAdapter(adapter));
        swipeManager.attachRecyclerView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        swipeManager.release();
        swipeManager = null;
        adapter = null;
        recyclerView.removeAllViews();
        recyclerView.destroyDrawingCache();
        recyclerView = null;
        layoutManager.removeAllViews();
        layoutManager = null;
        dataText.destroyDrawingCache();
        dataText = null;
        timeText.destroyDrawingCache();
        timeText = null;
        date = null;
        time = null;
        addEventItem = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_action_menu, menu);
        addEventItem = menu.findItem(R.id.item_add);
        isAddItemEventActive = false;

        return true;
    }


    public void showTimePickerDialog(View v) {

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onAddEventButtonClick(MenuItem item) {
        addEventItem = item;
        if (isAddItemEventActive) {
            FoodFormEvent foodFormEvent = new FoodFormEvent(new Event("", "", getDate(), getTime()),
                                                            "");
            foodFormEvent.setId(++id);
            events.add(foodFormEvent);
            adapter.notifyItemInserted(events.size() - 1);
            layoutManager.scrollToPosition(events.size() - 1);
            makeAddEventItemInactive();
        } else {
            Toast.makeText(getApplicationContext(),
                           "Zanim dodasz nową aktywność\nuzupełnij pola",
                           Toast.LENGTH_SHORT).show();
        }
    }

    public void makeAddEventItemActive() {
        isAddItemEventActive = true;
        addEventItem.setIcon(R.drawable.ic_add_active);
    }

    public void makeAddEventItemInactive() {
        isAddItemEventActive = false;
        addEventItem.setIcon(R.drawable.ic_add_inactive);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void onSaveButtonClick(MenuItem item) {
        EventsAdapter eventsAdapter = new EventsAdapter(getApplicationContext());
        eventsAdapter.open();
        for (FoodFormEvent foodFormEvent : events) {
            if (!foodFormEvent.getName().isEmpty()) {
                foodFormEvent.setDate(date);
                foodFormEvent.setTime(time);
                eventsAdapter.insertFoodFormEvent(foodFormEvent);
            }
        }
        MainActivity.isChagnedMap.put(date, true);
        MainActivity.adapter.updateRecycler(date);
        MainActivity.adapter.updateDailyLimit(date);
        MainActivity.adapter.notifyItemInserted(0);
        MainActivity.closeFab();
        eventsAdapter.close();

        finish();

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour,
                                                                     minute,
                                                                     DateFormat.is24HourFormat(
                                                                             getActivity()));
            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            timeText.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                                     .format(new Date(0, 0, 0, hour, minute)));
            time = timeText.getText() + "";
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dataText.setText(new SimpleDateFormat("E, dd MMMM yyyy", Locale.getDefault())
                                     .format(new Date(year - 1900, month, day)));
            date = dataText.getText() + "";
        }
    }


}