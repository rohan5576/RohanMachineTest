package tk.androidtechnical.rohanmachinetest.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tk.androidtechnical.rohanmachinetest.R;
import tk.androidtechnical.rohanmachinetest.adapter.EventRecyclerAdapter;
import tk.androidtechnical.rohanmachinetest.model.Event;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    ArrayList<Event> events;
    EventRecyclerAdapter adapter;
    ProgressDialog pDialog;
    private FirebaseDatabase mFirebaseInstance;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(MainActivity.this);

        setRecyclerView();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");
                String appTitle = dataSnapshot.getValue(String.class);
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        if (isNetworkAvailable(MainActivity.this)) {
            loadEvents();
        } else {
            showToast(MainActivity.this, "No Internet Connection");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        events = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearlayout = new LinearLayoutManager(MainActivity.this);
        linearlayout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearlayout);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        events.addAll(databaseHelper.getEvents());
        adapter = new EventRecyclerAdapter(MainActivity.this);
        adapter.setEvents(events);
        adapter.setType(0);
        recyclerView.setAdapter(adapter);
    }

    private void loadEvents(){
        events.clear();
        pDialog = ProgressDialog.show(MainActivity.this, null, "Loading...");
        DatabaseReference scoresRef = mFirebaseInstance.getReference("events");
        scoresRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                pDialog.dismiss();
                databaseHelper.deleteAll();
                Event event = dataSnapshot.getValue(Event.class);
                events.add(event);
                databaseHelper.addEvents(events);
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
                Log.e(TAG, "The " + dataSnapshot.getKey() + " score is " + dataSnapshot.getValue());
                System.out.println("The " + dataSnapshot.getKey() + " score is " + dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                pDialog.dismiss();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                pDialog.dismiss();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pDialog.dismiss();
            }
        });
    }

    public static String getTodaysDate(){
        String result = null;
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat day_format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            result = day_format.format(cal.getTime());
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static Boolean isCurrentMonth(String d_date){
        boolean isValid = false;
        try {
            Calendar cal = Calendar.getInstance();
            DateFormat day_format = new SimpleDateFormat("MM", Locale.ENGLISH);
            String current_month = day_format.format(cal.getTime());

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = (Date) formatter.parse(d_date);
            SimpleDateFormat format = new SimpleDateFormat("MM");
            String result = format.format(date);

            if (current_month.equals(result)){
                isValid = true;
            } else {
                isValid = false;
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return isValid;
    }


    private void loadDayView(){
        ArrayList<Event> events1 = new ArrayList<>();
        for (int i=0; i<events.size(); i++){
            if (events.get(i).getDate().equals(getTodaysDate())){
                events1.add(events.get(i));
            }
        }
        //events.clear();
        //events.addAll(events1);
        adapter.setType(1);
        adapter.setEvents(events1);
        adapter.notifyDataSetChanged();
    }

    private void loadWeekView(){
        ArrayList<Event> events1 = new ArrayList<>();
        for (int i=0; i<events.size(); i++){
            int days = getCountDays(getTodaysDate(), events.get(i).getDate());
            System.out.println("days " + days + " date " + events.get(i).getDate());
            if (days >= -7 && days <=0){
                events1.add(events.get(i));
            }
        }
        adapter.setType(2);
        adapter.setEvents(events1);
        adapter.notifyDataSetChanged();
    }


    private void loadMonthView(){
        ArrayList<Event> events1 = new ArrayList<>();
        for (int i=0; i<events.size(); i++){
            if (isCurrentMonth(events.get(i).getDate())){
                events1.add(events.get(i));
            }
        }
        adapter.setType(3);
        adapter.setEvents(events1);
        adapter.notifyDataSetChanged();
    }

    public static int getCountDays(String start_date, String end_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        try {
            Date start_dateConvertDate = sdf.parse(start_date);
            calendar1.setTime(start_dateConvertDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date end_dateConvertDate = sdf.parse(end_date);
            calendar2.setTime(end_dateConvertDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long miliSecondForDate1 = calendar1.getTimeInMillis();
        long miliSecondForDate2 = calendar2.getTimeInMillis();

        // Calculate the difference in millisecond between two dates
        long diffInMilis = miliSecondForDate2 - miliSecondForDate1;
        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);

        return (int) diffInDays;
    }

    private void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.day_view) {
            loadDayView();
            return true;
        }

        if (id == R.id.week_view) {
            loadWeekView();
            return true;
        }

        if (id == R.id.month_view) {
            loadMonthView();
            return true;
        }

        if (id == R.id.all_view){
            if (isNetworkAvailable(MainActivity.this)) {
                loadEvents();
            } else {
                events.clear();
                events.addAll(databaseHelper.getEvents());
                adapter.setEvents(events);
                adapter.setType(0);
                adapter.notifyDataSetChanged();
            }

        }

        return super.onOptionsItemSelected(item);
    }


}
