package ru.shtrm.gosport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
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
import android.widget.TextView;
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
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.fragments.FragmentAddStadium;
import ru.shtrm.gosport.fragments.FragmentAddUser;
import ru.shtrm.gosport.fragments.FragmentEditUser;
import ru.shtrm.gosport.fragments.MapFragment;
import ru.shtrm.gosport.fragments.TeamsFragment;
import ru.shtrm.gosport.fragments.TrainingsFragment;
import ru.shtrm.gosport.fragments.UserInfoFragment;
import ru.shtrm.gosport.utils.LoadTestData;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.MainFunctions.checkRealmNew;
import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_ADD = 1;
    private static final int PROFILE_SETTINGS = 2;
    private static final int MAX_USER_PROFILE = 5;

    private static final int NO_FRAGMENT = 0;
    private static final int FRAGMENT_USER = 1;
    private static final int FRAGMENT_SPORTS = 2;
    private static final int FRAGMENT_MAP = 3;
    private static final int FRAGMENT_STADIUMS = 4;
    private static final int FRAGMENT_TRAININGS = 5;
    private static final int FRAGMENT_CALENDAR = 6;
    private static final int FRAGMENT_MYTRAININGS = 7;
    private static final int FRAGMENT_EVENTS = 8;
    private static final int FRAGMENT_TEAMS = 9;

    private static final int FRAGMENT_ADDTRAINING = 10;
    private static final int FRAGMENT_ADDSTADIUM = 11;
    private static final int FRAGMENT_SETTRAINING = 12;

    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    private static final String TAG = "MainActivity";
    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    int activeUserID = 0;
    ProgressDialog mProgressDialog;

    private boolean isLogged = false;
    private AccountHeader headerResult = null;
    private ArrayList<IProfile> iprofilelist;
    private RealmResults<User> profilesList;
    private long users_id[];
    private int cnt = 0;
    private ProgressDialog authorizationDialog;
    private boolean splashShown = false;

    private Realm realmDB;
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
        //LoadTestData.LoadAllTestData2();
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
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.menu_mytrainings:
                        //getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, MyTrainingsFragment.newInstance()).commit();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                        break;
                    case R.id.menu_calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TrainingsFragment.newInstance()).commit();
                        break;
                    case R.id.menu_user:
                        if (activeUserID>0) getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                        break;
                    case R.id.menu_maps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, MapFragment.newInstance()).commit();
                        break;
                }
            }
        });

        bottomBar.setActiveTabColor(getResources().getColor(R.color.larisaBlueColor));
        int new_trainings = MainFunctions.getActiveTrainingsCount();
        if (new_trainings > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_trainings);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                        new ProfileSettingDrawerItem().withName("Добавить пользователя").withDescription("Добавить пользователя").withIcon(String.valueOf(GoogleMaterial.Icon.gmd_plus)).withIdentifier(PROFILE_ADD),
                        new ProfileSettingDrawerItem().withName("Редактировать пользователя").withIcon(String.valueOf(GoogleMaterial.Icon.gmd_settings)).withIdentifier(PROFILE_SETTINGS)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_ADD) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentAddUser.newInstance()).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTINGS) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentEditUser.newInstance("EditProfile")).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() > PROFILE_SETTINGS) {
                            int profileId = profile.getIdentifier() - 2;
                            int profile_pos;
                            for (profile_pos = 0; profile_pos < iprofilelist.size(); profile_pos++)
                                if (users_id[profile_pos] == profileId) break;

                            changeActiveProfile(profilesList.get(profile_pos));
                            AuthorizedUser.getInstance().setUuid(profilesList.get(profile_pos).getUuid());
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        fillProfileList();

        PrimaryDrawerItem taskPrimaryDrawerItem;
        if (new_trainings > 0) {
            taskPrimaryDrawerItem = new PrimaryDrawerItem().withName(R.string.menu_mytrainings).withDescription("Текущие тренировки").withIcon(GoogleMaterial.Icon.gmd_calendar).withIdentifier(FRAGMENT_MYTRAININGS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)).withBadge("" + new_trainings).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red));
        } else {
            taskPrimaryDrawerItem = new PrimaryDrawerItem().withName(R.string.menu_mytrainings).withDescription("Текущие тренировки").withIcon(GoogleMaterial.Icon.gmd_calendar).withIdentifier(FRAGMENT_MYTRAININGS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor));
        }

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_users).withDescription("Информация о игроке").withIcon(R.drawable.menu_user).withIdentifier(FRAGMENT_USER).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName(R.string.menu_calendar).withDescription("Записаться на игру").withIcon(R.drawable.menu_hockey).withIdentifier(FRAGMENT_CALENDAR).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        taskPrimaryDrawerItem,
                        new PrimaryDrawerItem().withName("Карта").withDescription("площадок").withIcon(R.drawable.menu_football).withIdentifier(FRAGMENT_MAP).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName("Площадку").withDescription("добавить").withIcon(R.drawable.menu_ring).withIdentifier(FRAGMENT_STADIUMS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName("Соревнования").withDescription("ближайшие").withIcon(R.drawable.menu_competition).withIdentifier(FRAGMENT_EVENTS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Команды").withDescription("спортивные").withIcon(R.drawable.menu_hockey).withIdentifier(FRAGMENT_TEAMS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == FRAGMENT_STADIUMS) {
                                currentFragment = FRAGMENT_STADIUMS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentAddStadium.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_MAP) {
                                currentFragment = FRAGMENT_MAP;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, MapFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_TRAININGS) {
                                currentFragment = FRAGMENT_TRAININGS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TrainingsFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_CALENDAR) {
                                currentFragment = FRAGMENT_CALENDAR;
                                //getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, SetTrainingFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_TEAMS) {
                                currentFragment = FRAGMENT_TEAMS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TeamsFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_USER) {
                                currentFragment = FRAGMENT_USER;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                startAboutDialog();
                            } else if (drawerItem.getIdentifier() == DRAWER_EXIT) {
                                System.exit(0);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
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

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
            users_id[cnt] = item.get_id();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void addProfile(User item) {
        IProfile new_profile;
        Bitmap myBitmap;
        if (item.getChangedAt() != null)
            myBitmap = getResizedBitmap(MainFunctions.getUserImagePath(getApplicationContext()), item.getImage(), 0, 600, item.getChangedAt().getTime());
        else
            myBitmap = getResizedBitmap(MainFunctions.getUserImagePath(getApplicationContext()), item.getImage(), 0, 600, new Date().getTime());

        if (myBitmap != null) {
            new_profile = new ProfileDrawerItem().withName(item.getName()).withEmail(item.getPhone()).withIcon(myBitmap).withIdentifier((int) item.get_id() + 2).withOnDrawerItemClickListener(onDrawerItemClickListener);
        } else
            new_profile = new ProfileDrawerItem().withName(item.getName()).withEmail(item.getPhone()).withIcon(R.drawable.profile_default_small).withIdentifier((int) item.get_id() + 2).withOnDrawerItemClickListener(onDrawerItemClickListener);
        iprofilelist.add(new_profile);
        headerResult.addProfile(new_profile, headerResult.getProfiles().size());
    }

    public void refreshProfileList() {
        profilesList = realmDB.where(User.class).findAll();
        cnt = 0;
        for (User item : profilesList) {
            users_id[cnt] = item.get_id();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void deleteProfile(int id) {
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
                    if (users_id[cnt] == user.get_id()) {
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

    public void ShowSettings() {
        TextView system_server;
    }

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
        //super.onSaveInstanceState(outState);
    }
}
