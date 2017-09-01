package ru.shtrm.gosport.db;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexFile;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmException;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

class ToirRealmMigration implements RealmMigration {
    private final String TAG = this.getClass().getName();
    private Context context;

    ToirRealmMigration(Context context) {
        this.context = context;
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d(TAG, "oldVersion = " + oldVersion);
        Log.d(TAG, "newVersion = " + newVersion);

        if (oldVersion == newVersion) {
            if (!testPropsFields(realm)) {
                throw new RealmException("Классы и схема не идентичны!!!");
            }

            return;
        }
        if (oldVersion == 0) {
            Log.d(TAG, "from version 0");
            oldVersion++;
        }

        if (oldVersion == 1) {
            Log.d(TAG, "from version 1");

            schema.create("Journal")
                    .addField("_id", long.class)
                    .addField("description", String.class)
                    .addField("userUuid", String.class)
                    .addField("date", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Sport")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Amplua")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Event")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addField("description", String.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addField("date", Date.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Level")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addField("icon", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Stadium")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addField("description", String.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addField("longitude", double.class)
                    .addField("latitude", double.class)
                    .addField("photo", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Team")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addField("description", String.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addField("photo", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("User")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("name", String.class)
                    .addField("login", String.class)
                    .addField("pass", String.class)
                    .addField("type", Integer.class)
                    .addField("age", Integer.class)
                    .addField("image", String.class)
                    .addField("phone", String.class)
                    .addField("vk", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("UserSport")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addRealmObjectField("user", schema.get("User"))
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addRealmObjectField("amplua", schema.get("Amplua"))
                    .addRealmObjectField("team", schema.get("Team"))
                    .addRealmObjectField("level", schema.get("Level"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Training")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("user", schema.get("User"))
                    .addRealmObjectField("team", schema.get("Team"))
                    .addField("comment", String.class)
                    .addField("cost", Integer.class)
                    .addRealmObjectField("sport", schema.get("Sport"))
                    .addRealmObjectField("level", schema.get("Level"))
                    .addRealmObjectField("stadium", schema.get("Stadium"))
                    .addField("date", Date.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Notification")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addRealmObjectField("training", schema.get("Training"))
                    .addField("date", Date.class)
                    .addField("view", boolean.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("UserTraining")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addRealmObjectField("user", schema.get("User"))
                    .addRealmObjectField("training", schema.get("Training"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            oldVersion++;
        }

        if (oldVersion == 2) {
            Log.d(TAG, "from version 2");

            schema.get("User")
                    .removeField("type")
                    .removeField("age");

            schema.get("User")
                    .addField("type", int.class)
                    .addField("age", int.class);

            schema.get("Training")
                    .removeField("cost");

            schema.get("Training")
                    .addField("cost", int.class);

            oldVersion++;
        }

        testPropsFields(realm);
    }

    private boolean testPropsFields(DynamicRealm realm) {
        RealmSchema schema = realm.getSchema();

        // проверяем соответствие схемы базы со свойствами классов
        Set<RealmObjectSchema> realmObjects = schema.getAll();
        Set<String> tableList = new LinkedHashSet<>();
        for (RealmObjectSchema realmObject : realmObjects) {
            String tableName = realmObject.getClassName();
            Log.d(TAG, "Class name = " + tableName);
            tableList.add(tableName);
            Field[] classProps;
            Set<String> props = new HashSet<>();
            Map<String, String> propsType = new HashMap<>();
            try {
                Class<?> c = Class.forName("ru.shtrm.gosport.db.realm." + tableName);
                classProps = c.getDeclaredFields();
                for (Field prop : classProps) {
                    props.add(prop.getName());
                    propsType.put(prop.getName(), prop.getType().getName());
//                    propsType.put(prop.getName(), prop.getGenericType().toString());
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            // проверяем количество и названия полей и свойств
            Set<String> fieldNames = realmObject.getFieldNames();
            Set<String> backProps = new HashSet<>(props);
            props.removeAll(fieldNames);
            fieldNames.removeAll(backProps);
            if (props.size() == 0 && fieldNames.size() == 0) {
                Log.d(TAG, "Список полей идентичен.");
            } else {
                StringBuilder b = new StringBuilder();
                if (props.size() > 0) {
                    for (String item : props) {
                        b.append(item).append(", ");
                    }

                    Log.e(TAG, "Список свойств класса без соответствующих полей в таблице: " + b.toString());
                }

                if (fieldNames.size() > 0) {
                    b.setLength(0);
                    for (String item : fieldNames) {
                        b.append(item).append(", ");
                    }

                    Log.e(TAG, "Список полей таблицы без соответствующих свойств класса: " + b.toString());
                }

                return false;
            }

            // сравниваем типы свойств и полей
            for (String fieldName : fieldNames) {
                String realmType = realmObject.getFieldType(fieldName).name();
                String propType = propsType.get(fieldName);
                if (!realmType.equals(getType(propType))) {
                    Log.e(TAG, "Type not same (fName = " + fieldName + "): fType = " + realmType + ", pType = " + propType);
                    return false;
                }
            }
        }

        // получаем список классов объектов которые выступают в роли таблиц
        Set<String> classList = new HashSet<>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> iter = df.entries();
            while (iter.hasMoreElements()) {
                String classPath = iter.nextElement();
                if (classPath.contains("ru.shtrm.gosport.db.realm") && !classPath.contains("$")) {
                    try {
                        Class<?> driverClass;
                        driverClass = Class.forName(classPath);
                        Log.e(TAG,"driverClass=" + driverClass.getName());
                        Constructor<?> constructor = driverClass.getConstructor();
                        RealmObject o = (RealmObject) constructor.newInstance();
                        o.getClass().getMethod("deleteFromRealm");
                        classList.add(classPath.substring(classPath.lastIndexOf('.') + 1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // проверяем соответствие полученых списков классов и таблиц
        Set<String> backTableList = new HashSet<>(tableList);
        tableList.removeAll(classList);
        classList.removeAll(backTableList);
        if (tableList.size() == 0 && classList.size() == 0) {
            Log.d(TAG, "Список классов соответствует списку таблиц.");
        } else {
            StringBuilder b = new StringBuilder();
            if (tableList.size() > 0) {
                for (String item : tableList) {
                    b.append(item).append(", ");
                }

                Log.e(TAG, "Список таблиц без соответствующих классов: " + b.toString());
            }

            if (classList.size() > 0) {
                b.setLength(0);
                for (String item : classList) {
                    b.append(item).append(", ");
                }

                Log.e(TAG, "Список классов без соответствующих таблиц: " + b.toString());
            }

            return false;
        }

        return true;
    }

    private String getType(String type) {
        String result = type.substring(type.lastIndexOf('.') + 1).toUpperCase();

        switch (result) {
            case "INT":
            case "LONG":
                result = "INTEGER";
                break;
            case "STRING":
            case "DOUBLE":
            case "DATE":
            case "FLOAT":
            case "BOOLEAN":
                break;
            case "REALMLIST":
                result = "LIST";
                break;
            default:
                result = "OBJECT";
        }

        return result;
    }

}
