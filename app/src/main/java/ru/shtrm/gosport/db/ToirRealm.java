package ru.shtrm.gosport.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ToirRealm {
    // версия схемы базы данных приложения
    public static final int VERSION = 0;

    public static void init(Context context) {
        init(context, "toir.realm");
    }

    public static void init(Context context, String dbName) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .name(dbName)
                .schemaVersion(VERSION)
                .build();
        try {
            Realm.migrateRealm(realmConfig, new ToirRealmMigration(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Realm.setDefaultConfiguration(realmConfig);
    }
}
