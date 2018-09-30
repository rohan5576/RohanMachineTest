package tk.androidtechnical.rohanmachinetest.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tk.androidtechnical.rohanmachinetest.R;
import tk.androidtechnical.rohanmachinetest.model.User;
import tk.androidtechnical.rohanmachinetest.util.SharedPreference;

import static tk.androidtechnical.rohanmachinetest.util.Constant.emailCheck;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.edt_email)
    EditText edt_email;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.txt_error_message)
    TextView txt_error_message;

    SharedPreference sharedPreference;
    private String userId;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        sharedPreference = new SharedPreference(LoginActivity.this);


    }


    @OnClick(R.id.btn_login)
    public void onLogin() {
        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();

        if (email.isEmpty()) {
            txt_error_message.setText(R.string.error_email);
            return;
        }

        if (!emailCheck(email)) {
            txt_error_message.setText(R.string.error_valid_email_address);
            return;
        }

        if (password.isEmpty()) {
            txt_error_message.setText(R.string.error_password);
            return;
        }

        if (password.length() > 15 || password.length() < 5) {
            txt_error_message.setText("Password length should be min 5 to max 15");
            return;
        }

        createUser(email, password);
    }


    private void createUser(String email, String password) {
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        User user = new User(email, password);
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }


    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    return;
                }
                sharedPreference.addUser(user.getEmail());
                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }


}
