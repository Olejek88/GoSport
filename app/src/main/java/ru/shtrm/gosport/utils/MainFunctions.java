package ru.shtrm.gosport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.db.realm.Journal;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.LocalFiles;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

public class MainFunctions {

    private Realm realmDB;
    private static final String TAG = "Func";

    public static String getIMEI(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public static void checkRealmNew() {
        final Realm realmDB = Realm.getDefaultInstance();
        final Level level = realmDB.where(Level.class).findFirst();
        if (level == null) {
            LoadTestData.LoadAllTestData();
        }
        realmDB.close();
    }

    public static String getUserImagePath(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "img" + File.separator;
    }

    public static void addToJournal(final String description) {
        final Realm realmDB = Realm.getDefaultInstance();
        final User user = realmDB.where(User.class).equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user != null) {
            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Journal record = realmDB.createObject(Journal.class);
                    long next_id = realm.where(Journal.class).max("_id").intValue() + 1;
                    record.set_id(next_id);
                    record.setDate(new Date());
                    record.setDescription(description);
                    record.setUserUuid(user.getUuid());
                }
            });
        }

        realmDB.close();
    }

    public static int getActiveTrainingsCount() {
        int count = 0;
        final Realm realmDB = Realm.getDefaultInstance();
        final User user = realmDB.where(User.class).equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user != null) {
            count = realmDB.where(Training.class)
                    .equalTo("user.uuid", user.getUuid())
                    .findAll().size();
        }

        realmDB.close();
        return count;
    }

    public static String getPicturesDirectory(Context context) {
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + "Android"
                + File.separator
                + "data"
                + File.separator
                + context.getPackageName()
                + File.separator;
        return filename;
    }

    public static void storeImage(String name, String object, Context context, Bitmap bmp) throws IOException {
//        String target_filename = sd_card.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator + name;
        String target_filename = MainFunctions.getUserImagePath(context) + name;
        final Realm realmDB = Realm.getDefaultInstance();
        Log.d(TAG,target_filename);
        File target_file = new File (target_filename);
        if (!target_file.getParentFile().exists()) {
            if (!target_file.getParentFile().mkdirs())
                Toast.makeText(context, "Невозможно создать директорию!", Toast.LENGTH_LONG).show();
        }
        FileOutputStream out = new FileOutputStream(target_file);
        if (bmp!=null) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        }
        out.flush();
        out.close();

        realmDB.beginTransaction();
        Number currentIdNum = realmDB.where(LocalFiles.class).max("_id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
        User user = realmDB.where(User.class).equalTo("uuid", authorizedUser.getUuid()).findFirst();
        LocalFiles localFile = realmDB.createObject(LocalFiles.class, nextId);
        localFile.setUser(user);
        localFile.setSent(false);
        localFile.setObject(object);
        localFile.setUuid(java.util.UUID.randomUUID().toString());
        localFile.setFileName(name);
        localFile.setChangedAt(new Date());
        localFile.setCreatedAt(new Date());
        realmDB.commitTransaction();
    }

}


