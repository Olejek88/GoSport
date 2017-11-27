package ru.shtrm.gosport.db;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ToirRealm {
    // версия схемы базы данных приложения
    public static final int VERSION = 14;

    public static void init(Context context) {
        init(context, "gosport.realm");
    }

    public static void init(Context context, String dbName) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(dbName)
                .schemaVersion(VERSION)
                .build();
        //Realm.setDefaultConfiguration(realmConfig);
        try {
            Realm.migrateRealm(realmConfig, new ToirRealmMigration(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Realm.setDefaultConfiguration(realmConfig);

        // инициализируем интерфейс для отладки через Google Chrome
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                        .build());        
    }
}
