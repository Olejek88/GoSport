package ru.shtrm.gosport;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
//import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.LocalFiles;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.ReferenceUpdate;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.fragments.EventsFragment;
import ru.shtrm.gosport.fragments.FragmentAddStadium;
import ru.shtrm.gosport.fragments.FragmentAddUser;
import ru.shtrm.gosport.fragments.FragmentEditUser;
import ru.shtrm.gosport.fragments.MapFragment;
import ru.shtrm.gosport.fragments.MyTrainingsFragment;
import ru.shtrm.gosport.fragments.TeamsFragment;
import ru.shtrm.gosport.fragments.TrainingsFragment;
import ru.shtrm.gosport.fragments.UserEmptyFragment;
import ru.shtrm.gosport.fragments.UserInfoFragment;
import ru.shtrm.gosport.rest.GSportAPIFactory;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_GPS_ACCESS = 1;
    private static final int REQUEST_WRITE_STORAGE = 2;

    private static final int PROFILE_ADD = 1;
    private static final int PROFILE_SETTINGS = 2;
    private static final int MAX_USER_PROFILE = 5;

    private static final int NO_FRAGMENT = 0;
    private static final int FRAGMENT_USER = 1;
    //private static final int FRAGMENT_SPORTS = 2;
    private static final int FRAGMENT_MAP = 3;
    private static final int FRAGMENT_STADIUMS = 4;
    private static final int FRAGMENT_TRAININGS = 5;
    private static final int FRAGMENT_CALENDAR = 6;
    private static final int FRAGMENT_MYTRAININGS = 7;
    private static final int FRAGMENT_EVENTS = 8;
    private static final int FRAGMENT_TEAMS = 9;

    //private static final int FRAGMENT_ADDTRAINING = 10;
    private static final int FRAGMENT_ADDSTADIUM = 11;
    //private static final int FRAGMENT_SETTRAINING = 12;

    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    private static final String TAG = "MainActivity";

    public static String NO_SYNC = "no_sync";
    public static String UserAge = "";

    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    int activeUserID = 0;
    //ProgressDialog mProgressDialog;

    private boolean isLogged = false;
    private AccountHeader headerResult = null;
    private ArrayList<IProfile> iprofilelist;
    private RealmResults<User> profilesList;
    private long users_id[];
    private int cnt = 0;
    //private ProgressDialog authorizationDialog;
    private boolean splashShown = false;

    private Realm realmDB;
    private BottomBar bottomBar;

    private Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
            Log.d(TAG, "onDrawerItemClick");
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;

        // инициализация приложения
        init();

        Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
        if (savedInstanceState != null) {
            isLogged = savedInstanceState.getBoolean("isLogged");
            Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
            splashShown = savedInstanceState.getBoolean("splashShown");
            AuthorizedUser aUser = AuthorizedUser.getInstance();
            aUser.setToken(savedInstanceState.getString("token"));
            aUser.setUuid(savedInstanceState.getString("userUuid"));
        }

        Log.d(TAG, "onCreate");

        if (!splashShown) {
            // показываем приветствие
            setContentView(R.layout.start_screen);

            // запускаем таймер для показа экрана входа
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    splashShown = true;
                    setMainLayout(savedInstance);
                    CheckPermission ();
                }
            }, 5000);
        } else {
            setMainLayout(savedInstanceState);
        }
    }

    /**
     * Инициализация приложения при запуске
     */
    public void init() {
        if (!initDB()) {
            // принудительное обновление приложения
            finish();
        }
        //LoadTestData.DeleteSomeData();
        //checkRealmNew();
        //LoadTestData.LoadAllTestData();
    }

    public boolean initDB() {
        boolean success = false;
        try {
            // получаем базу realm
            realmDB = Realm.getDefaultInstance();
            Log.d(TAG, "Realm DB schema version = " + realmDB.getVersion());
            Log.d(TAG, "db.version=" + realmDB.getVersion());
            if (realmDB.getVersion() == 0) {
                Toast toast = Toast.makeText(this, "База данных не актуальна!",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            } else {
                Toast toast = Toast.makeText(this, "База данных актуальна!",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this,
                    "Не удалось открыть/обновить базу данных!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }

        return success;
    }

    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        int larisaBlueColor = ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor);
        bottomBar = findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.menu_mytrainings:
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.frame_container, MyTrainingsFragment.newInstance()).commit();
                        break;
                    case R.id.menu_calendar:
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.frame_container, TrainingsFragment.newInstance()).commit();
                        break;
                    case R.id.menu_user:
                        changeUserFragment();
                        break;
                    case R.id.menu_maps:
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.frame_container, MapFragment.newInstance()).commit();
                        break;
                }
            }
        });

        bottomBar.setActiveTabColor(getResources().getColor(R.color.larisaBlueColor));
        int new_trainings = MainFunctions.getActiveTrainingsCount();
        if (new_trainings > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_trainings);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Организовываем и тренируемся!");
        toolbar.setTitleTextColor(Color.WHITE);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        iprofilelist = new ArrayList<>();
        users_id = new long[MAX_USER_PROFILE];

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.hockey_header)
                .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almostblack))
                .addProfiles(
                        new ProfileSettingDrawerItem().withName("Добавить пользователя")
                                .withDescription("Добавить пользователя")
                                .withIcon(String.valueOf(GoogleMaterial.Icon.gmd_plus))
                                .withIdentifier(PROFILE_ADD),
                        new ProfileSettingDrawerItem().withName("Редактировать пользователя")
                                .withIcon(String.valueOf(GoogleMaterial.Icon.gmd_settings))
                                .withIdentifier(PROFILE_SETTINGS)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_ADD) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_container, FragmentAddUser.newInstance()).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTINGS) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_container, FragmentEditUser.newInstance()).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() > PROFILE_SETTINGS) {
                            long profileId = profile.getIdentifier() - 2;
                            int profile_pos;
                            for (profile_pos = 0; profile_pos < iprofilelist.size(); profile_pos++)
                                if (users_id[profile_pos] == profileId) break;

                            changeActiveProfile(profilesList.get(profile_pos));
                            AuthorizedUser.getInstance().setUuid(profilesList.get(profile_pos).getUuid());
                            currentFragment = FRAGMENT_USER;
                            changeUserFragment();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        fillProfileList();

        PrimaryDrawerItem taskPrimaryDrawerItem;
        if (new_trainings > 0) {
            taskPrimaryDrawerItem = new PrimaryDrawerItem()
                    .withName(R.string.menu_mytrainings)
                    .withDescription("Текущие тренировки")
                    .withIcon(R.drawable.menu_calendar)
                    .withIdentifier(FRAGMENT_MYTRAININGS)
                    .withSelectable(false)
                    .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor))
                    .withBadge("" + new_trainings)
                    .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red));
        } else {
            taskPrimaryDrawerItem = new PrimaryDrawerItem()
                    .withName(R.string.menu_mytrainings)
                    .withDescription("Текущие тренировки")
                    .withIcon(R.drawable.menu_calendar)
                    .withIdentifier(FRAGMENT_MYTRAININGS)
                    .withSelectable(false)
                    .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor));
        }

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_users)
                                .withDescription("Информация о игроке")
                                .withIcon(R.drawable.menu_user)
                                .withIdentifier(FRAGMENT_USER)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new PrimaryDrawerItem().withName(R.string.menu_calendar)
                                .withDescription("Записаться на игру")
                                .withIcon(R.drawable.menu_hockey)
                                .withIdentifier(FRAGMENT_CALENDAR)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        taskPrimaryDrawerItem,
                        new PrimaryDrawerItem().withName("Карта")
                                .withDescription("площадок")
                                .withIcon(R.drawable.menu_location)
                                .withIdentifier(FRAGMENT_MAP)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new PrimaryDrawerItem().withName("Площадку")
                                .withDescription("добавить")
                                .withIcon(R.drawable.menu_ring)
                                .withIdentifier(FRAGMENT_STADIUMS)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new PrimaryDrawerItem().withName("Соревнования")
                                .withDescription("ближайшие")
                                .withIcon(R.drawable.menu_competition)
                                .withIdentifier(FRAGMENT_EVENTS)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Команды")
                                .withDescription("спортивные")
                                .withIcon(R.drawable.user_team)
                                .withIdentifier(FRAGMENT_TEAMS)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("О программе")
                                .withDescription("Информация о версии")
                                .withIcon(FontAwesome.Icon.faw_info)
                                .withIdentifier(DRAWER_INFO)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor),
                        new PrimaryDrawerItem().withName("Выход")
                                .withIcon(FontAwesome.Icon.faw_undo)
                                .withIdentifier(DRAWER_EXIT)
                                .withSelectable(false)
                                .withSelectable(false)
                                .withIconColor(larisaBlueColor)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            int fragmentClick = ((int) drawerItem.getIdentifier());
                            switch (fragmentClick) {
                                case FRAGMENT_STADIUMS:
                                    currentFragment = FRAGMENT_STADIUMS;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    FragmentAddStadium.newInstance()).commit();
                                    bottomBar.selectTabWithId(R.id.menu_maps);
                                    break;
                                case FRAGMENT_MAP:
                                    currentFragment = FRAGMENT_MAP;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    MapFragment.newInstance()).commit();
                                    bottomBar.selectTabWithId(R.id.menu_maps);
                                    break;
                                case FRAGMENT_TRAININGS:
                                    currentFragment = FRAGMENT_TRAININGS;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    MyTrainingsFragment.newInstance()).commit();
                                    bottomBar.selectTabWithId(R.id.menu_mytrainings);
                                    break;
                                case FRAGMENT_CALENDAR:
                                    currentFragment = FRAGMENT_CALENDAR;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    TrainingsFragment.newInstance()).commit();
                                    bottomBar.selectTabWithId(R.id.menu_calendar);
                                    break;
                                case FRAGMENT_TEAMS:
                                    currentFragment = FRAGMENT_TEAMS;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    TeamsFragment.newInstance()).commit();
                                    break;
                                case FRAGMENT_ADDSTADIUM:
                                    currentFragment = FRAGMENT_ADDSTADIUM;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    FragmentAddStadium.newInstance()).commit();
                                    break;
                                case FRAGMENT_EVENTS:
                                    currentFragment = FRAGMENT_EVENTS;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    EventsFragment.newInstance()).commit();
                                    break;
                                case FRAGMENT_USER:
                                    currentFragment = FRAGMENT_USER;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container,
                                                    UserInfoFragment.newInstance()).commit();
                                    bottomBar.selectTabWithId(R.id.menu_user);
                                    break;
                                case DRAWER_INFO:
                                    startAboutDialog();
                                    break;
                                case DRAWER_EXIT:
                                    System.exit(0);
                                    break;
                                default:
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        if (savedInstanceState == null && activeUserID==0 ) {
            result.setSelection(21, false);
            for (cnt = 0; cnt < profilesList.size(); cnt++) {
                if (profilesList.get(cnt).getActive()) {
                    headerResult.setActiveProfile(iprofilelist.get(cnt));
                    // !!!
                    AuthorizedUser.getInstance().setUuid(profilesList.get(cnt).getUuid());
                    activeUserID = cnt+1;
                }
            }

            if (iprofilelist.size() > 0) {
                for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
                    if (activeUserID > 0 && iprofilelist.get(cnt).getIdentifier() == activeUserID + 2)
                        headerResult.setActiveProfile(iprofilelist.get(cnt));
                }
            }
        }
        changeUserFragment();

        if (activeUserID <= 0) {
            Toast.makeText(getApplicationContext(),
                    "Пожалуйста зарегистрируйтесь", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Обработчик клика меню обновления приложения
     *
     * @param menuItem Элемент меню
     */
    public void onActionUpdate(MenuItem menuItem) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        getReferences ();
    }


    public void onActionSettings(MenuItem menuItem) {
        Log.d(TAG, "onActionSettings");
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void onActionAbout(MenuItem menuItem) {
        Log.d(TAG, "onActionAbout");
        startAboutDialog();
    }


    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			 return true;
//		}
        return super.onKeyDown(keyCode, event);
    }

    /* функция заполняет массив профилей - пользователей */

    public void fillProfileList() {
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getApplicationContext()));
        //User users = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findAll();
        profilesList = realmDB.where(User.class).findAll();
        cnt = 0;
        for (User item : profilesList) {
            addProfile(item);
            users_id[cnt] = item.getid();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void addProfile(User item) {
        IProfile new_profile;
        Bitmap myBitmap;
        if (item.getChangedAt() != null)
            myBitmap = getResizedBitmap(MainFunctions.
                    getUserImagePath(getApplicationContext()),
                    item.getImage(), 0, 600, item.getChangedAt().getTime());
        else
            myBitmap = getResizedBitmap(MainFunctions.
                    getUserImagePath(getApplicationContext()),
                    item.getImage(), 0, 600, new Date().getTime());

        if (myBitmap != null) {
            new_profile = new ProfileDrawerItem().withName(item.getName())
                    .withEmail(item.getPhone())
                    .withIcon(myBitmap)
                    .withIdentifier((int) item.getid() + 2)
                    .withOnDrawerItemClickListener(onDrawerItemClickListener);
        } else
            new_profile = new ProfileDrawerItem().withName(item.getName())
                    .withEmail(item.getPhone())
                    .withIcon(R.drawable.profile_default_small)
                    .withIdentifier((int) item.getid() + 2)
                    .withOnDrawerItemClickListener(onDrawerItemClickListener);
        iprofilelist.add(new_profile);
        headerResult.addProfile(new_profile, headerResult.getProfiles().size());
    }

    public void refreshProfileList() {
        profilesList = realmDB.where(User.class).findAll();
        cnt = 0;
        for (User item : profilesList) {
            users_id[cnt] = item.getid();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void deleteProfile(long id) {
        int id_remove;
        for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
            if (users_id[cnt] == id) {
                iprofilelist.remove(cnt);
                //headerResult.removeProfile(cnt);
                id_remove = (int) (users_id[cnt] + 2);
                headerResult.removeProfileByIdentifier(id_remove);
            }
        }
        refreshProfileList();
    }

    public void mOnClickMethod(View view) {
        //openOptionsMenu();
        //Intent i = new Intent(MainActivity.this, ToirPreferences.class);
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void changeActiveProfile (User user) {
        if (iprofilelist != null) {
            if (iprofilelist.size() > 0) {
                for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
                    if (users_id[cnt] == user.getid()) {
                        headerResult.setActiveProfile(iprofilelist.get(cnt));

                        realmDB.beginTransaction();
                        RealmResults<User> users = realmDB.where(User.class).findAll();
                        for (int i = 0; i < users.size(); i++)
                            users.get(i).setActive(false);

                        if (profilesList != null && profilesList.get(cnt) != null) {
                            profilesList.get(cnt).setActive(true);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Непредвиденная ошибка: нет такого пользователя", Toast.LENGTH_LONG).show();
                        }
                        realmDB.commitTransaction();
                        realmDB.beginTransaction();
                        user.setActive(true);
                        realmDB.commitTransaction();
                    }
                }
            }
        }
    }

    /*public void ShowSettings() {
        TextView system_server;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        //ShowSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmDB != null)
            realmDB.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putBoolean("splashShown", splashShown);
        outState.putString("uuid", AuthorizedUser.getInstance().getUuid());
        outState.putString("token", AuthorizedUser.getInstance().getToken());
        outState.putString("userUuid", AuthorizedUser.getInstance().getUuid());
        super.onSaveInstanceState(outState);
    }
    
    /**
     * Получение справочников
     */
    private static void getReferences () {
        AsyncTask<String[], Integer, Void> aTask = new AsyncTask<String[], Integer, Void>() {
            @Override
            protected Void doInBackground(String[]... params) {
                // обновляем справочники
                updateReferences();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        };

        //String[] statusArray = s.toArray(new String[]{});
        aTask.execute();
    }

    public static void updateReferences() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;

                // Sport
                referenceName = Sport.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Sport>> response = GSportAPIFactory.getSportService().sport(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Sport> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Amplua
                referenceName = Amplua.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Amplua>> response = GSportAPIFactory.getAmpluaService().amplua(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Amplua> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Level
                referenceName = Level.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Level>> response = GSportAPIFactory.getLevelService().level(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Level> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Stadium
                referenceName = Stadium.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Stadium>> response = GSportAPIFactory.getStadiumService().stadium(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Stadium> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Team
                referenceName = Team.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Team>> response = GSportAPIFactory.getTeamService().team(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Team> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Players
                referenceName = User.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<User>> response = GSportAPIFactory.getUserService().user(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<User> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        });
        thread.start();
    }

    /**
     * Получение объектов с сервера
     */
    private void getObjects() {
        AsyncTask<String[], Integer, List<Training>> aTask = new AsyncTask<String[], Integer, List<Training>>() {
            @Override
            protected List<Training> doInBackground(String[]... params) {
                // обновляем справочники
                updateReferences();
                List<String> args = java.util.Arrays.asList(params[0]);

                // запрашиваем тренировки
                Call<List<Training>> call = GSportAPIFactory.getTrainingService().training();
                List<Training> result;
                try {
                    Response<List<Training>> response = call.execute();
                    result = response.body();
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    return null;
                }
                // список файлов для загрузки
                List<FilePath> files = new ArrayList<>();
                // строим список изображений для загрузки
                if (result!=null) {
                    for (Training training : result) {
                        Stadium stadium = training.getStadium();
                        // общий путь до файлов на сервере
                        String basePath = "/storage/";
                        // общий путь до файлов локальный
                        String basePathLocal = "/files/";
                        if (stadium != null) {
                            // урл изображения стадиона
                            files.add(new FilePath(stadium.getImage(), basePath, basePathLocal));
                        }
                        Team team = training.getTeam();
                        if (team != null) {
                            // урл изображения команды
                            files.add(new FilePath(team.getPhoto(), basePath, basePathLocal));
                        }
                    }
                }

                // загружаем файлы
                int filesCount = 0;
                for (FilePath path : files) {
                    Call<ResponseBody> call1 = GSportAPIFactory.getFileDownload().getFile(GoSportApplication.serverUrl + path.urlPath + path.fileName);
                    try {
                        Response<ResponseBody> r = call1.execute();
                        ResponseBody trueImgBody = r.body();
                        if (trueImgBody == null) {
                            continue;
                        }
                        filesCount++;
                        publishProgress(filesCount);
                        File file = new File(getApplicationContext().getExternalFilesDir(path.localPath), path.fileName);
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                Log.e(TAG, "Не удалось создать папку " +
                                        file.getParentFile().toString() +
                                        " для сохранения файла изображения!");
                                continue;
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(trueImgBody.bytes());
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<Training> trainings) {
                super.onPostExecute(trainings);
                if (trainings == null) {
                    // сообщаем описание неудачи
                    Toast.makeText(getApplication(), "Ошибка при получении тренировок", Toast.LENGTH_LONG).show();
                } else {
                    int count = trainings.size();
                    // собщаем количество полученных нарядов
                    if (count > 0) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(trainings);
                        realm.commitTransaction();
                        realm.close();
                        //addToJournal("Клиент успешно получил " + count + " нарядов");
                        Toast.makeText(getApplicationContext(), "Количество тренировок " + count, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplication(), "Тренировок нет.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        };

        aTask.execute();
    }

    /**
     * Метод для отправки файлов загруженных пользователем
     */
    private void sendFiles(List<LocalFiles> files) {

        AsyncTask<LocalFiles[], Void, List<String>> task = new AsyncTask<LocalFiles[], Void, List<String>>() {
            @NonNull
            private RequestBody createPartFromString(String descriptionString) {
                return RequestBody.create(MultipartBody.FORM, descriptionString);
            }

            @NonNull
            private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
                File file = new File(fileUri.getPath());
                String type = null;
                String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }

                MediaType mediaType = MediaType.parse(type);
                RequestBody requestFile = RequestBody.create(mediaType, file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
                return part;
            }

            @Override
            protected List<String> doInBackground(LocalFiles[]... lists) {
                List<String> sendFiles = new ArrayList<>();
                for (LocalFiles file : lists[0]) {
                    RequestBody descr = createPartFromString("Photos due execution operation.");
                    Uri uri = null;
                    try {
                        uri = Uri.fromFile(new File(
                                getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                file.getFileName()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    List<MultipartBody.Part> list = new ArrayList<>();
                    String fileUuid = file.getUuid();
                    String formId = "file[" + fileUuid + "]";
                    list.add(prepareFilePart(formId, uri));
                    list.add(MultipartBody.Part.createFormData(formId + "[_id]", String.valueOf(file.get_id())));
                    list.add(MultipartBody.Part.createFormData(formId + "[uuid]", file.getUuid()));
                    list.add(MultipartBody.Part.createFormData(formId + "[userUuid]", file.getUser().getUuid()));
                    list.add(MultipartBody.Part.createFormData(formId + "[object]", file.getObject()));
                    list.add(MultipartBody.Part.createFormData(formId + "[fileName]", file.getFileName()));
                    list.add(MultipartBody.Part.createFormData(formId + "[createdAt]", String.valueOf(file.getCreatedAt())));
                    list.add(MultipartBody.Part.createFormData(formId + "[changedAt]", String.valueOf(file.getChangedAt())));
                    // запросы делаем по одному, т.к. может сложиться ситуация когда будет попытка отправить
                    // объём данных превышающий ограничения на отправку POST запросом на сервере
                    Call<ResponseBody> call = GSportAPIFactory.getFileDownload().uploadFiles(descr, list);
                    try {
                        Response response = call.execute();
                        ResponseBody result = (ResponseBody) response.body();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "result" + result.contentType());
                            sendFiles.add(file.getFileName());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return sendFiles;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                // TODO: нужно придумать более правильный механизм передачи данных для отправки и обработки результата
                // пока сделано по тупому
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (String item : strings) {
                    LocalFiles file = realm.where(LocalFiles.class).equalTo("filename", item).findFirst();
                    file.setSent(true);
                }

                realm.commitTransaction();
                realm.close();
            }
        };

        LocalFiles[] sendFiles = files.toArray(new LocalFiles[]{});
        task.execute(sendFiles);
    }

    private void sendObjects(List<Training> trainings) {
        AsyncTask<Training[], Void, String> task = new AsyncTask<Training[], Void, String>() {
            @Override
            protected String doInBackground(Training[]... lists) {
                List<Training> args = Arrays.asList(lists[0]);
                Call<ResponseBody> call = GSportAPIFactory.getTrainingService().sendTraining(args);
                try {
                    Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };

        Training[] trainingsArray = trainings.toArray(new Training[]{});
        task.execute(trainingsArray);
    }

    private void sendUser() {
        AsyncTask<User[], Void, String> task = new AsyncTask<User[], Void, String>() {
            @Override
            protected String doInBackground(User[]... users) {
                AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
                User user = realmDB.where(User.class).equalTo("uuid", authorizedUser.getUuid()).findFirst();
                Call<ResponseBody> call = GSportAPIFactory.getUserService().sendUser(user);
                try {
                    Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };

        task.execute();
    }

    private class FilePath {
        String fileName;
        String urlPath;
        String localPath;

        FilePath(String name, String url, String local) {
            fileName = name;
            urlPath = url;
            localPath = local;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void changeUserFragment() {
        final User user = realmDB.where(User.class).equalTo("uuid",
                AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, UserEmptyFragment.newInstance()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
        }
    }
    @Override
    public void onBackPressed() {
    }

    private void CheckPermission () {
        // Create the storage directory if it does not exist
        if (!MainFunctions.isExternalStorageWritable()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS_ACCESS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            getResources().getString(R.string.message_no_gps_permission),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            getResources().getString(R.string.message_no_write_permission),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
