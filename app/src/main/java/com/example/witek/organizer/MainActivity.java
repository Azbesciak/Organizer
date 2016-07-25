package com.example.witek.organizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
    private String selectedDate;
    private String currentDay;
    private static FloatingActionMenu menuRed;
    private Handler mUiHandler = new Handler();
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private EventsAdapter eventsAdapter;
    private RecyclerView recycler;
    static MainActivityAdapter adapter;
    static Map<String,Boolean> isChagnedMap = new HashMap();
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Date currentDayDate;
    EditText reachedKcalToday;
    EditText kcalLimitToday;
    View headerView;
    int recyclerViewState;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDate();
        initializeNavigationDrawer();
        initializeSelectedDayEventsRecycler();
        initializeFab();
        initializeCalendarWidget();
    }



    private void initializeNavigationDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        //editText at navigation drawer top with actual variable
        reachedKcalToday = (EditText) headerView.findViewById(R.id.reachedKcalToday);
        kcalLimitToday = (EditText) headerView.findViewById(R.id.kcalLimitToday);

    }

    private void initializeSelectedDayEventsRecycler() {
        recycler = (RecyclerView) findViewById(R.id.main_recycler);
        adapter = new MainActivityAdapter(this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        eventsAdapter = new EventsAdapter(getApplicationContext());
        eventsAdapter.open();
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = newState;
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        displayFab(handler);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if(!menuRed.isMenuButtonHidden()) {
                            menuRed.hideMenuButton(true);
                        }
                        break;
                    }
                    case RecyclerView.SCROLL_STATE_SETTLING: {
                        break;
                    }
                }
            }
        });
    }
    private void initializeDate() {
        dateFormat = new SimpleDateFormat("E, dd MMMM yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        currentDayDate = new Date();
        selectedDate = dateFormat.format( new Date());
        currentDay = dateFormat.format( new Date());
    }

    private void initializeFab() {
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        menuRed = (FloatingActionMenu) findViewById(R.id.menu_red);
        menuRed.hideMenuButton(false);

        menuRed.setClosedOnTouchOutside(true);
        final int delay = 400;
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menuRed.showMenuButton(true);
            }
        }, delay);
        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuRed.toggle(true);
            }
        });
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
    }

    private void displayFab (final Handler handler) {
        if(menuRed.isMenuButtonHidden()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(menuRed.isMenuButtonHidden() && recyclerViewState == RecyclerView.SCROLL_STATE_IDLE) {
                        menuRed.showMenuButton(true);
                    } else {
                        displayFab(handler);
                    }
                }
            }, 2000);
        }

    }
    private void initializeCalendarWidget() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                adapter.saveSelectedLimit(selectedDate,adapter.selectedDayKcalLimitValue.getText().toString());
                Date dateTemp = new Date(year-1900,month,dayOfMonth);
                selectedDate = dateFormat.format(dateTemp);
                updateListViewColor(dateTemp);
                Snackbar.make(view,"Wybrano date : " + selectedDate, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                adapter.updateRecycler(selectedDate);

                adapter.updateDailyLimit(selectedDate);

            }
        });
    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:{
                    Intent intent = new Intent(v.getContext(), AddActivityFormEvent.class);
                    if(selectedDate == null) {

                    }

                    intent.putExtra("date",selectedDate);
                    intent.putExtra("time", timeFormat.format(System.currentTimeMillis()));
                    startActivity(intent);
                    break;
                }
                case R.id.fab2: {
                    Intent intent = new Intent(v.getContext(),AddFoodFormEvent.class);
                    intent.putExtra("date",selectedDate);
                    intent.putExtra("time", timeFormat.format(System.currentTimeMillis()));
                    startActivity(intent);
                    break;
                }
            }
        }
    };


    public void updateListViewColor(Date dateTemp) {
        DailyBalance dailyBalance = adapter.dailyBalanceMap.get(selectedDate);
        double limit = 0;
        double reachedKcal = 0;
        try {
            limit = Double.valueOf(dailyBalance.getKcalLimit());
        }catch (Exception exc) {}
        try {
            reachedKcal = Double.valueOf(dailyBalance.getReachedKcal());
        }catch (Exception exc) {}

        if(selectedDate.equals(currentDay)) {
            switch (changeColorMaker(limit,reachedKcal)) {
                case 0: {
                    headerView.setBackgroundResource(R.drawable.default_nav_bar);
                    break;
                }
                case 1: {
                    headerView.setBackgroundResource(R.drawable.unefficient_nav_bar);
                    break;
                }
                case 2: {
                    headerView.setBackgroundResource(R.drawable.under_limit_nav_bar);
                    break;
                }
                case 3: {
                    headerView.setBackgroundResource(R.drawable.in_limit_nav_bar);
                    break;
                }
                case 4: {
                    headerView.setBackgroundResource(R.drawable.above_limit_nav_bar);
                    break;
                }
                case 5: {
                    headerView.setBackgroundResource(R.drawable.exceeded_limit_nav_bar);
                    break;
                }
                default: {
                    headerView.setBackgroundResource(R.drawable.default_nav_bar);
                    break;
                }
            }
        }else if(currentDayDate.after(dateTemp)) {
            changeColorMaker(limit,reachedKcal);
        }else {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

        }
    }
    public int changeColorMaker(double limit, double reached) {
        if(limit == 0) {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightGray));
            return 0;
        }else if(reached <0.8*limit) {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightBlue));
            return 1;
        }else if(reached <0.95*limit) {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightTeal));
            return 2;
        }else if(reached <1.05*limit) {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightGreen));
            return 3;
        }
        else if(reached <1.2*limit) {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightOrange));
            return 4;
        }else {
            adapter.summaryLabel.setBackgroundColor(getResources().getColor(R.color.lightRed));
            return 5;
        }

    }


    public String getSelectedDate() {
        return selectedDate;
    }
    public String getCurrentDay() {
        return currentDay;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                Toast.makeText(getApplicationContext(), "nie dodano " + result + " wartosci",Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "cancel",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static void closeFab() {
        menuRed.close(false);
    }

}
