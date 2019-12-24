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

		// TODO убрать после отладки
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = "http://192.168.1.94:3003";

        // инициализируем базу данных Realm
        ToirRealm.init(this);
    }
}
