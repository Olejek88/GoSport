package ru.shtrm.gosport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Journal;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.LocalFiles;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.model.AuthorizedUser;

import static android.content.Context.LOCATION_SERVICE;

public class MainFunctions {

    private static final String TAG = "Func";

    public static void checkRealmNew() {
        final Realm realmDB = Realm.getDefaultInstance();
        final Level level = realmDB.where(Level.class).findFirst();
        if (level == null) {
            LoadTestData.LoadAllTestData();
        }
        realmDB.close();
    }

    public static String getUserImagePath(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "Android" + File.separator + "data" + File.separator + context.getPackageName()
                + File.separator + "files" + File.separator;
    }

    public static void addToJournal(final String description) {
        final Realm realmDB = Realm.getDefaultInstance();
        final User user = realmDB.where(User.class).
                equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
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
        final User user = realmDB.where(User.class).
                equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user != null) {
            count = realmDB.where(Training.class)
                    .equalTo("user.uuid", user.getUuid())
                    .findAll().size();
        }
        realmDB.close();
        return count;
    }

    public static String getPicturesDirectory(Context context) {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files"
                + File.separator;
    }

    public static Bitmap getBitmapByPath(String path, String filename) {
        File imageFull = new File(path + filename);
        Bitmap bmp = BitmapFactory.decodeFile(imageFull.getAbsolutePath());
        if (bmp != null) {
            return bmp;
        } else return null;
    }

    public static Bitmap storeNewImage(Bitmap image, Context context, int width, String image_name) {
        final String imageName = image_name;
        final Realm realmDB = Realm.getDefaultInstance();
        File mediaStorageDir = new File(getPicturesDirectory(context));

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                if (isExternalStorageWritable()) {
                    Toast.makeText(context, "Нет разрешений на запись данных",
                            Toast.LENGTH_LONG).show();
                    return null;
                }
            }
        }
        if (image_name.equals("")) {
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.US).format(new Date());
            image_name = "file_" + timeStamp + ".jpg";
        }
        // Create a media file name
        File pictureFile;
        pictureFile = new File(mediaStorageDir.getPath() + File.separator + image_name);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            if (image != null) {
                int height = (int) (width * (float) image.getHeight() / (float) image.getWidth());
                if (height > 0) {
                    Bitmap myBitmap = Bitmap.createScaledBitmap(image, width, height, false);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            User user = realmDB.where(User.class).equalTo("uuid",
                                    AuthorizedUser.getInstance().getUuid()).findFirst();
                            String uuid = java.util.UUID.randomUUID().toString();
                            LocalFiles localFile = realmDB.createObject(LocalFiles.class, uuid);
                            localFile.setUser(user);
                            localFile.setSent(false);
                            localFile.setObject("file");
                            localFile.setUuid(uuid);
                            localFile.setFileName(imageName);
                            localFile.setChangedAt(new Date());
                            localFile.setCreatedAt(new Date());
                        }
                    });
                    return myBitmap;
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return null;
    }

    public static String getVkProfile(String vk_input) {
        String vk_output = "http://vk.com/undefined";
        if (vk_input.contains("vk") && !vk_input.contains("http"))
            vk_output = "http://".concat(vk_input);
        if (!vk_input.contains("vk") && !vk_input.contains("http"))
            vk_output = "http://vk.com/".concat(vk_input);
        if (vk_input.contains("vk") && vk_input.contains("http"))
            vk_output = vk_input;
        return vk_output;
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager;
        Location location = null;
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (mLocationManager == null) return null;
        List<String> providers = mLocationManager.getProviders(true);
        if (providers == null) return null;
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                location = mLocationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                Toast.makeText(context, "Нет разрешений на определение местоположения",
                        Toast.LENGTH_SHORT).show();
            }

            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 5;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void toast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static GeoPoint getCurrentGeoPoint(Context context) {
        Location location = null;
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        // дефолтные координаты - центр Челябинска
        // это на тот случай, если никак нельзя определить последние координаты
        double curLatitude=55.175708, curLongitude=61.390594;
        if (lm != null) {
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.message_no_gps_permission),
                        Toast.LENGTH_SHORT).show();
            }

            if (location == null) {
                location = MainFunctions.getLastKnownLocation(context);
            }
            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
            }
        }
        return new GeoPoint(curLatitude, curLongitude);
    }
}

