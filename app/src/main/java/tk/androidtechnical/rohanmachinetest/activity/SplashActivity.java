package tk.androidtechnical.rohanmachinetest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import tk.androidtechnical.rohanmachinetest.R;
import tk.androidtechnical.rohanmachinetest.util.SharedPreference;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    SharedPreference sharedPreferences;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        sharedPreferences = new SharedPreference(SplashActivity.this);
        handler();
    }

    private void handler() {
        handler.postDelayed(mUpdateTimeTask, 4000);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (sharedPreferences.getUser() != null){
                    Intent in = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}