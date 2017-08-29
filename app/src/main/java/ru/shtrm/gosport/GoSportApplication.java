package ru.shtrm.gosport;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.shtrm.gosport.db.ToirRealm;

@SuppressWarnings("unused")
public class GoSportApplication extends Application {

	public static String serverUrl = "";

	@Override
	public void onCreate() {
		super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = preferences.getString(getString(R.string.serverUrl), "");

        // инициализируем базу данных Realm
        ToirRealm.init(this);
    }
}
