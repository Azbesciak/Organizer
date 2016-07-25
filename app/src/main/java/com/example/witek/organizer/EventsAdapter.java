package com.example.witek.organizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Witek on 26.05.2016.
 */
public class EventsAdapter {
    private static final String DEBUG_TAG = "SqLiteEventsManager";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";


    private static final String DB_EVENTS_TABLE = "events";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_NAME = "name";
    public static final String NAME_OPTIONS = "TEXT NOT NULL";
    public static final int NAME_COLUMN = 1;

    public static final String KEY_DATE = "date";
    public static final String DATE_OPTIONS = "TEXT NOT NULL";
    public static final int DATE_COLUMN = 2;

    public static final String KEY_TIME = "time";
    public static final String TIME_OPTIONS = "TEXT_NOT_NULL";
    public static final int TIME_COLUMN = 3;

    public static final String KEY_KCAL_PER_UNIT = "kcalPerUnit";
    public static final String KCAL_PER_UNIT_OPTIONS = "TEXT";
    public static final int KCAL_PER_UNIT_COLUMN = 4;

    public static final String KEY_BALANCE = "balance";
    public static final String BALANCE_OPTIONS = "TEXT";
    public static final int BALANCE_COLUMN = 5;

    public static final String KEY_WEIGHT = "weight";
    public static final String WEIGHT_OPTIONS = "TEXT";
    public static final int WEIGHT_COLUMN = 6;

    public static final String KEY_DURATION = "duration";
    public static final String DURATION_OPTIONS = "TEXT";
    public static final int DURATION_COLUMN = 7;

    private static final String DB_CREATE_EVENTS_TABLE =
            "CREATE TABLE " + DB_EVENTS_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_NAME + " " + NAME_OPTIONS + ", " +
                    KEY_DATE + " " + DATE_OPTIONS + ", " +
                    KEY_TIME + " " + TIME_OPTIONS + ", " +
                    KEY_KCAL_PER_UNIT + " " + KCAL_PER_UNIT_OPTIONS + ", " +
                    KEY_BALANCE + " " + BALANCE_OPTIONS + ", " +
                    KEY_WEIGHT + " " + WEIGHT_OPTIONS + ", " +
                    KEY_DURATION + " " + DURATION_OPTIONS + ");";

    private static final String DROP_EVENTS_TABLE =
            "DROP TABLE IF EXISTS " + DB_EVENTS_TABLE;

    //BALANCE TABLE
    private static final String DB_DAILY_BALANCE_TABLE = "dailyBalance";

    public static final String KEY_KCAL_LIMIT = "kcalLimit";
    public static final String KCAL_LIMIT_OPTIONS = "TEXT";
    public static final int KCAL_LIMIT_COLUMN = 1;

    public static final String KEY_REACHED_KCAL = "reachedKcal";
    public static final String REACHED_KCAL_OPTIONS = "TEXT";
    public static final int REACHED_KCAL_COLUMN = 3;

    private static final String DB_CREATE_DAILY_BALANCE_TABLE =
            "CREATE TABLE " + DB_DAILY_BALANCE_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_KCAL_LIMIT + " " + KCAL_LIMIT_OPTIONS + ", " +
                    KEY_DATE + " " + DATE_OPTIONS + ", " +
                    KEY_REACHED_KCAL + " " + REACHED_KCAL_OPTIONS + ");";

    private static final String DROP_DAILY_BALANCE_TABLE =
            "DROP TABLE IF EXISTS " + DB_DAILY_BALANCE_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_EVENTS_TABLE);
            db.execSQL(DB_CREATE_DAILY_BALANCE_TABLE);
            Log.d(DEBUG_TAG, "Database creating . . .");
            Log.d(DEBUG_TAG, "Table " + DB_EVENTS_TABLE + " ver." + DB_VERSION + " created");
            Log.d(DEBUG_TAG, "Table " + DB_CREATE_DAILY_BALANCE_TABLE + " ver." + DB_VERSION + " created");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_EVENTS_TABLE);
            db.execSQL(DB_CREATE_DAILY_BALANCE_TABLE);
            Log.d(DEBUG_TAG, "Database updating . . . ");
            Log.d(DEBUG_TAG, "Table " + DB_EVENTS_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "Table " + DB_CREATE_DAILY_BALANCE_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost");

            onCreate(db);
        }
    }
    public EventsAdapter(Context context){
        this.context = context;
    }
    public EventsAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try{
            db = dbHelper.getWritableDatabase();
        }catch(SQLException e){
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }
    public void close(){
        dbHelper.close();
    }

    //INSERT'Y
    public long insertActivityFormEvent(ActivityFormEvent activityFormEvent){
        ContentValues newActivityFormEventValues = new ContentValues();
        newActivityFormEventValues.put(KEY_NAME,activityFormEvent.getName());
        newActivityFormEventValues.put(KEY_DATE,activityFormEvent.getDate());
        newActivityFormEventValues.put(KEY_TIME,activityFormEvent.getTime());
        newActivityFormEventValues.put(KEY_KCAL_PER_UNIT,activityFormEvent.getKcalPerUnit());
        newActivityFormEventValues.put(KEY_BALANCE,activityFormEvent.getKcalBalance());
        newActivityFormEventValues.put(KEY_DURATION,activityFormEvent.getDuration());
        return db.insert(DB_EVENTS_TABLE,null,newActivityFormEventValues);
    }
    public long insertActivityFormEvent(String name, String date, String time, String kcalPerUnit,
                                        String balance, String duration){
        ContentValues newActivityFormEventValues = new ContentValues();
        newActivityFormEventValues.put(KEY_NAME,name);
        newActivityFormEventValues.put(KEY_DATE,date);
        newActivityFormEventValues.put(KEY_TIME,time);
        newActivityFormEventValues.put(KEY_KCAL_PER_UNIT,kcalPerUnit);
        newActivityFormEventValues.put(KEY_BALANCE,balance);
        newActivityFormEventValues.put(KEY_DURATION,duration);



        return db.insert(DB_EVENTS_TABLE,null,newActivityFormEventValues);
    }
    public long insertFoodFormEvent(FoodFormEvent foodFormEvent){
        ContentValues newFoodFormEventValues = new ContentValues();
        newFoodFormEventValues.put(KEY_NAME, foodFormEvent.getName());
        newFoodFormEventValues.put(KEY_DATE, foodFormEvent.getDate());
        newFoodFormEventValues.put(KEY_TIME, foodFormEvent.getTime());
        newFoodFormEventValues.put(KEY_KCAL_PER_UNIT, foodFormEvent.getKcalPerUnit());
        newFoodFormEventValues.put(KEY_BALANCE, foodFormEvent.getKcalBalance());
        newFoodFormEventValues.put(KEY_WEIGHT, foodFormEvent.getWeight());
        return db.insert(DB_EVENTS_TABLE, null, newFoodFormEventValues);
    }
    public long insertFoodFormEvent(String name, String date, String time, String kcalPerUnit,
                                        String balance, String weight){
        ContentValues newFoodFormEventValues = new ContentValues();
        newFoodFormEventValues.put(KEY_NAME, name);
        newFoodFormEventValues.put(KEY_DATE, date);
        newFoodFormEventValues.put(KEY_TIME, time);
        newFoodFormEventValues.put(KEY_KCAL_PER_UNIT, kcalPerUnit);
        newFoodFormEventValues.put(KEY_BALANCE, balance);
        newFoodFormEventValues.put(KEY_WEIGHT, weight);
        return db.insert(DB_EVENTS_TABLE, null, newFoodFormEventValues);
    }

    public long insertDailyBalance(String kcalLimit, String date, String reachedKcal) {
        ContentValues newDailyBalanceValues = new ContentValues();
        newDailyBalanceValues.put(KEY_KCAL_LIMIT, kcalLimit);
        newDailyBalanceValues.put(KEY_DATE, date);
        newDailyBalanceValues.put(KEY_REACHED_KCAL, reachedKcal);
        return db.insert(DB_DAILY_BALANCE_TABLE, null, newDailyBalanceValues);
    }
    public long insertDailyBalance(DailyBalance dailyBalance) {
        ContentValues newDailyBalanceValues = new ContentValues();
        newDailyBalanceValues.put(KEY_KCAL_LIMIT, dailyBalance.getKcalLimit());
        newDailyBalanceValues.put(KEY_DATE, dailyBalance.getDate());
        newDailyBalanceValues.put(KEY_REACHED_KCAL, dailyBalance.getReachedKcal());
        return db.insert(DB_DAILY_BALANCE_TABLE, null, newDailyBalanceValues);
    }
    //UPDATE'Y
    public boolean updateActivityFormEvent(ActivityFormEvent activityFormEvent) {
        long id = activityFormEvent.getId();
        String name = activityFormEvent.getName();
        String date = activityFormEvent.getDate();
        String time = activityFormEvent.getTime();
        String kcalPerUnit = activityFormEvent.getKcalPerUnit();
        String kcalBalance = activityFormEvent.getKcalBalance();
        String duration = activityFormEvent.getDuration();
        return updateActivityFormEvent(id,name,date,time,kcalPerUnit,kcalBalance,duration);
    }

    public boolean updateActivityFormEvent(long id,String name,String date,String time, String kcalPerUnit,
                                           String kcalBalance,String duration) {
        String where = KEY_ID + "=" + id;
        ContentValues updateActivityFormEvent = new ContentValues();
        updateActivityFormEvent.put(KEY_NAME, name);
        updateActivityFormEvent.put(KEY_DATE, date);
        updateActivityFormEvent.put(KEY_TIME, time);
        updateActivityFormEvent.put(KEY_KCAL_PER_UNIT,kcalPerUnit);
        updateActivityFormEvent.put(KEY_BALANCE,kcalBalance);
        updateActivityFormEvent.put(KEY_DURATION,duration);
        return db.update(DB_EVENTS_TABLE,updateActivityFormEvent,where,null) > 0;
    }

    public boolean updateFoodFormEvent(FoodFormEvent foodFormEvent) {
        long id = foodFormEvent.getId();
        String name = foodFormEvent.getName();
        String date = foodFormEvent.getDate();
        String time = foodFormEvent.getTime();
        String kcalPerUnit = foodFormEvent.getKcalPerUnit();
        String kcalBalance = foodFormEvent.getKcalBalance();
        String weight = foodFormEvent.getWeight();
        return updateFoodFormEvent(id,name,date,time,kcalPerUnit,kcalBalance,weight);
    }

    public boolean updateFoodFormEvent(long id,String name,String date, String time, String kcalPerUnit,
                                           String kcalBalance,String weight) {
        String where = KEY_ID + "=" + id;
        ContentValues updateFoodFormEvent = new ContentValues();
        updateFoodFormEvent.put(KEY_NAME, name);
        updateFoodFormEvent.put(KEY_DATE, date);
        updateFoodFormEvent.put(KEY_TIME, time);
        updateFoodFormEvent.put(KEY_KCAL_PER_UNIT,kcalPerUnit);
        updateFoodFormEvent.put(KEY_BALANCE,kcalBalance);
        updateFoodFormEvent.put(KEY_WEIGHT,weight);
        return db.update(DB_EVENTS_TABLE,updateFoodFormEvent,where,null) > 0;
    }

//    public boolean updateDailyBalance(DailyBalance dailyBalance) {
//        long id = dailyBalance.getId();
//        String kcalLimit = dailyBalance.getKcalLimit();
//        String date = dailyBalance.getDate();
//        String reachedKcal = dailyBalance.getReachedKcal();
//        return updateDailyBalance(id,kcalLimit,date,reachedKcal);
//    }
//
//    public boolean updateDailyBalance(long id, String kcalLimit, String date, String reachedKcal) {
//        String where = KEY_ID + "=" + id;
//        ContentValues updateDailyBalance = new ContentValues();
//        updateDailyBalance.put(KEY_KCAL_LIMIT, kcalLimit);
//        updateDailyBalance.put(KEY_DATE, date);
//        updateDailyBalance.put(KEY_REACHED_KCAL, reachedKcal);
//        return db.update(DB_DAILY_BALANCE_TABLE,updateDailyBalance,where,null) > 0;
//
//    }
    public boolean updateDailyBalance(DailyBalance dailyBalance) {
        String where = KEY_DATE + "='" + dailyBalance.getDate() + "'";
        ContentValues updateDailyBalance = new ContentValues();
        updateDailyBalance.put(KEY_KCAL_LIMIT, dailyBalance.getKcalLimit());
//        updateDailyBalance.put(KEY_DATE, dailyBalance.getDate());
        updateDailyBalance.put(KEY_REACHED_KCAL, dailyBalance.getReachedKcal());
        return db.update(DB_DAILY_BALANCE_TABLE,updateDailyBalance,where,null) > 0;

    }

    //DELETE
    public boolean deleteEvent(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(DB_EVENTS_TABLE,where,null) > 0;
    }
    public boolean deleteDailyBalance(long id) {
        String where = KEY_ID + "=" + id;
        return db.delete(DB_DAILY_BALANCE_TABLE,where,null) > 0;
    }
    //GETY
    public Cursor getAllEvents(){
        String[] colums = {KEY_ID,KEY_NAME,KEY_DATE,KEY_TIME,KEY_BALANCE,KEY_DURATION,KEY_WEIGHT};
        return db.query(DB_EVENTS_TABLE, colums, null, null, null, null, null);
    }

    public Map<String, List<Event>> getDayEvents(String day) {
        String[] colums = {KEY_ID, KEY_NAME, KEY_DATE, KEY_TIME, KEY_KCAL_PER_UNIT, KEY_BALANCE, KEY_WEIGHT, KEY_DURATION};
        String where = KEY_DATE + "='" + day + "'";
        Cursor cursor = db.query(DB_EVENTS_TABLE, colums, where, null, null, null, null);
        Map<String, List<Event>> map = new TreeMap<>();


        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(ID_COLUMN);
                String name = cursor.getString(NAME_COLUMN);
                String date = cursor.getString(DATE_COLUMN);
                String time = cursor.getString(TIME_COLUMN);
                String kcalPerUnit = cursor.getString(KCAL_PER_UNIT_COLUMN);
                String balance = cursor.getString(BALANCE_COLUMN); // na wszelki wypadek
                String duration = cursor.getString(DURATION_COLUMN);
                String weight = cursor.getString(WEIGHT_COLUMN);

                if (duration != null) {
                    Event event = new ActivityFormEvent(name, kcalPerUnit, date, time, duration);

                    event.setId(id);
                    map = dontWantToRepeat(time,event,map);

                } else if (weight != null) {
                    Event event = new FoodFormEvent(name, kcalPerUnit, date, time, weight);
                    event.setId(id);
                    map = dontWantToRepeat(time,event,map);

                } else {
                    Event event = new Event(name, kcalPerUnit, date, time);
                    event.setId(id);
                    map = dontWantToRepeat(time,event,map);

                }

            } while (cursor.moveToNext());
        }
        Log.d(DEBUG_TAG,"download day events for " + day + " from database...");
        return map;
    }
    private Map<String, List<Event>> dontWantToRepeat(String time, Event event, Map<String, List<Event>> map ) {
        if(map.containsKey(time)) {
            map.get(time).add(event);
        }else {
            List<Event> list = new ArrayList();
            list.add(event);
            map.put(time,list);
        }
        return map;
    }

    public Map<String, DailyBalance> getDailyBalanceList() {
        String[] colums = {KEY_ID, KEY_KCAL_LIMIT, KEY_DATE, KEY_REACHED_KCAL};
        Cursor cursor = db.query(DB_DAILY_BALANCE_TABLE, colums, null, null, null, null, null);
        Map<String, DailyBalance> map = new TreeMap<>();


        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(ID_COLUMN);
                String kcalLimit = cursor.getString(KCAL_LIMIT_COLUMN);
                String date = cursor.getString(DATE_COLUMN);
                String reachedKcal = cursor.getString(REACHED_KCAL_COLUMN);

                    DailyBalance dailyBalance = new DailyBalance(date,kcalLimit);
                    dailyBalance.setId(id);
                    dailyBalance.setReachedKcal(reachedKcal);
                    map.put(date,dailyBalance);
            } while (cursor.moveToNext());
        }
        return map;
    }
    public DailyBalance getDailyBalance(String day) {
        String[] colums = {KEY_ID, KEY_KCAL_LIMIT, KEY_DATE, KEY_REACHED_KCAL};
        String where = KEY_DATE + "='" + day + "'";
        Cursor cursor = db.query(DB_DAILY_BALANCE_TABLE, colums, where, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(ID_COLUMN);
                String kcalLimit = cursor.getString(KCAL_LIMIT_COLUMN);
                String date = cursor.getString(DATE_COLUMN);
                String reachedKcal = cursor.getString(REACHED_KCAL_COLUMN);

                DailyBalance dailyBalance = new DailyBalance(date,kcalLimit);
                dailyBalance.setId(id);
                dailyBalance.setReachedKcal(reachedKcal);
            return dailyBalance;
            } else {
            return null;
        }

    }



}
