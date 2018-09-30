package tk.androidtechnical.rohanmachinetest.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.androidtechnical.rohanmachinetest.R;
import tk.androidtechnical.rohanmachinetest.model.Event;
import tk.androidtechnical.rohanmachinetest.util.Constant;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();
    @BindView(R.id.edt_agenda) EditText edt_agenda;
    @BindView(R.id.edt_participants) EditText edt_participants;
    @BindView(R.id.txt_date) TextView txt_date;
    @BindView(R.id.txt_time) TextView txt_time;
    @BindView(R.id.txt_error_message) TextView txt_error_message;
    @BindView(R.id.btn_submit) Button btn_submit;

    private String userId;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Event");
        setSupportActionBar(toolbar);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("events");


        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        txt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String agenda = edt_agenda.getText().toString();
                    String participants = edt_participants.getText().toString();
                    String date = txt_date.getText().toString();
                    String time = txt_time.getText().toString();

                    if (agenda.isEmpty()){
                        Snackbar.make(view, "Please enter agenda", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }

                    if (participants.isEmpty()){
                        Snackbar.make(view, "Please enter participant email", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }

//                    if (!Constant.emailCheck(participants)){
//                        Snackbar.make(view, "Please enter a valid participant email address", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                        return;
//                    }

                    if (date.isEmpty()){
                        Snackbar.make(view, "Please select date", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }

                    if (time.isEmpty()){
                        Snackbar.make(view, "Please select time", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }

                    createUser(agenda, participants, date, time);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }



    private void createUser(String agenda, String participants, String date, String time) {
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        Event event = new Event(agenda, participants, date, time);
        mFirebaseDatabase.child(userId).setValue(event);
        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Toast.makeText(AddEventActivity.this, "Add Event Successfully", Toast.LENGTH_LONG).show();
                finish();

                Log.e(TAG, "User data is changed!" + event.getAgenda() + ", " + event.participants);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(AddEventActivity.this,
                        new CalenderSelectDateListener(),
                        year,
                        month,
                        date);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hourOfDay = c.get(Calendar.HOUR);

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(AddEventActivity.this, new TimeListener(), hourOfDay, minute, false);
        timePickerDialog.show();
    }

    //DatePicker class
    class CalenderSelectDateListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            txt_date.setText(convertDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
        }
    }

    class TimeListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            txt_time.setText(convertTimeFormat(hourOfDay + ":" + minute));
        }
    }


    public static String convertDate(String d_date){
        String result = null;
        try {
            DateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
            Date date = (Date) formatter.parse(d_date);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            result = format.format(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static String convertTimeFormat(String d_date){
        String result = null;
        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = (Date) formatter.parse(d_date);
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
            result = format.format(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
