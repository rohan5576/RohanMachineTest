package tk.androidtechnical.rohanmachinetest.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

import tk.androidtechnical.rohanmachinetest.model.Event;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "db_employee";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL("create table Events(id integer primary key autoincrement, agenda text not null, participants text not null, date text not null, time text not null)");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS Employee");
        // Create tables again
        onCreate(db);
    }

    public boolean addEvents(ArrayList<Event> events) {

        for (int i=0; i<events.size();i++){
            Event event = events.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("agenda", event.getAgenda());
            values.put("participants", event.getParticipants());
            values.put("date", event.getDate());
            values.put("time", event.getTime());

            // insert row
            long id = db.insert("Events", null, values);

            // close db connection
            db.close();
        }

        // return newly inserted row id
        return true ;
    }

    public ArrayList<Event> getEvents() {
        ArrayList<Event> events = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("Events", null, null, null, null, null, "id desc");

        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(cursor.getInt(0));
            event.setAgenda(cursor.getString(1));
            event.setParticipants(cursor.getString(2));
            event.setDate(cursor.getString(3));
            event.setTime(cursor.getString(4));
            events.add(event);
        }

        // close cursor
        cursor.close();

        // close db connection
        db.close();

        // return employees list
        return events;
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete("Events", null, null);
        return count > 0;
    }



}
