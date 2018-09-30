package tk.androidtechnical.rohanmachinetest.util;

import android.app.Activity;
import android.content.Context;


public class SharedPreference {

    public static final String PREFS_NAME = "AppPreference";

    private String EMAIL = "email";

    private Context context;

	public SharedPreference(Context context) {
		super();
		this.context = context;
	}



    public boolean addUser(String email){
        android.content.SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = settings.edit();
        editor.putString(EMAIL, email);
        return editor.commit();
    }

    public String getUser(){
        android.content.SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        String text = pref.getString(EMAIL, null);
        return text;
    }

    public boolean clearSharedPreferences(){
        android.content.SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();
    }

}
