package ru.shtrm.gosport.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.model.AuthorizedUser;
import ru.shtrm.gosport.serverapi.DataManager;
import ru.shtrm.gosport.ui.fragments.FragmentAddStadium;
import ru.shtrm.gosport.ui.fragments.FragmentAddUser;
import ru.shtrm.gosport.ui.fragments.FragmentEditUser;
import ru.shtrm.gosport.ui.fragments.FragmentEvents;
import ru.shtrm.gosport.ui.fragments.MapFragment;
import ru.shtrm.gosport.ui.fragments.MyTrainingsFragment;
import ru.shtrm.gosport.ui.fragments.TeamsFragment;
import ru.shtrm.gosport.ui.fragments.TrainingsFragment;
import ru.shtrm.gosport.ui.fragments.UserEmptyFragment;
import ru.shtrm.gosport.ui.fragments.UserInfoFragment;
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
    private static final int FRAGMENT_MAP = 3;
    private static final int FRAGMENT_STADIUMS = 4;
    private static final int FRAGMENT_CALENDAR = 6;
    private static final int FRAGMENT_MY_TRAININGS = 7;
    private static final int FRAGMENT_EVENTS = 8;
    private static final int FRAGMENT_TEAMS = 9;
    private static final int FRAGMENT_ADD_STADIUM = 11;

    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    private static final String TAG = "MainActivity";
    public static String NO_SYNC = "no_sync";

    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    int activeUserID = 0;

    private boolean isLogged = false;
    private AccountHeader headerResult = null;
    private ArrayList<IProfile> iProfileList = new ArrayList<>();
    private RealmResults<User> profilesList;
    private long users_id[] = new long[MAX_USER_PROFILE];
    private boolean splashShown = false;

    private Realm realmDB = null;
    private BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;

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

        if (!splashShown) {
            // показываем приветствие
            setContentView(R.layout.start_screen);

            // запускаем таймер для показа экрана входа
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    splashShown = true;
                    setMainLayout(savedInstance);
                    CheckPermission();
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
            finish();
        }
        //LoadTestData.DeleteSomeData();
        //checkRealmNew();
        //LoadTestData.LoadAllTestData();
    }

    public boolean initDB() {
        boolean success = false;
        try {
            realmDB = Realm.getDefaultInstance();
            Log.d(TAG, "Realm DB schema version = " + realmDB.getVersion());
            if (realmDB.getVersion() == 0) {
                MainFunctions.toast(this, getResources().getString(R.string.message_db_is_not_actual));
            } else {
                MainFunctions.toast(this, getResources().getString(R.string.message_db_is_actual));
            }
            success = true;
        } catch (Exception e) {
            MainFunctions.toast(this, getResources().getString(R.string.message_db_cannot_open));
        }

        return success;
    }

    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        bottomBar = setBottomBar();
        int newTrainings = MainFunctions.getActiveTrainingsCount();
        if (newTrainings > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(newTrainings);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getResources().getString(R.string.application_declaration));
        toolbar.setTitleTextColor(Color.WHITE);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = createAccountHeader(savedInstanceState);

        fillProfileList();

        PrimaryDrawerItem taskPrimaryDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.menu_mytrainings)
                .withDescription("Текущие тренировки")
                .withIcon(R.drawable.menu_calendar)
                .withIdentifier(FRAGMENT_MY_TRAININGS)
                .withSelectable(false)
                .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor));

        if (newTrainings > 0) {
            taskPrimaryDrawerItem.
                    withBadge(Integer.toString(newTrainings)).
                    withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red));
        }

        Drawer result = createDrawer(toolbar, taskPrimaryDrawerItem);

        if (savedInstanceState == null && activeUserID == 0) {
            result.setSelection(21, false);
            // TODO понять зачем это было сделано
            int cnt = 0;
            for (User user : profilesList) {
                if (user.getActive()) {
                    // TODO понять зачем это было сделано
                    //headerResult.setActiveProfile();
                    // !!!
                    AuthorizedUser.getInstance().setUuid(user.getUuid());
                    activeUserID = cnt + 1;
                }
            }

            for (IProfile iProfile : iProfileList) {
                if (activeUserID > 0 && iProfile.getIdentifier() == activeUserID + 2)
                    headerResult.setActiveProfile(iProfile);
                }
        }
        changeUserFragment();

        if (activeUserID <= 0) {
            MainFunctions.toast(getApplicationContext(),
                    getResources().getString(R.string.message_please_register));
        }
    }

        /**
         * Обработчик клика меню обновления приложения
         *
         * @param menuItem Элемент меню
         */
        public void onActionUpdate (MenuItem menuItem){
            //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            getReferences();
        }


        public void onActionSettings (MenuItem menuItem){
            Log.d(TAG, "onActionSettings");
            // TODO нормальные пацаны на фрагменте делают
            // https://developer.android.com/guide/topics/ui/settings?hl=ru
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        public void onActionAbout (MenuItem menuItem){
            Log.d(TAG, "onActionAbout");
            AboutDialog about = new AboutDialog(this);
            about.setTitle("О программе");
            about.show();
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.action_about) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        /* функция заполняет массив профилей - пользователей */
        public void fillProfileList () {
            profilesList = realmDB.where(User.class).findAll();
            int cnt = 0;
            for (User item : profilesList) {
                addProfile(item);
                users_id[cnt] = item.getid();
                cnt = cnt + 1;
                if (cnt > MAX_USER_PROFILE) break;
            }
        }

        public void addProfile (User item) {
            IProfile newProfile;
            Bitmap myBitmap;
            if (item.getChangedAt() != null)
                myBitmap = getResizedBitmap(MainFunctions.getUserImagePath(getApplicationContext()),
                        item.getImage(), 0, 600, item.getChangedAt().getTime());
            else
                myBitmap = getResizedBitmap(MainFunctions.getUserImagePath(getApplicationContext()),
                        item.getImage(), 0, 600, new Date().getTime());

            newProfile = new ProfileDrawerItem().withName(item.getName())
                    .withEmail(item.getPhone())
                    .withIdentifier((int) item.getid() + 2)
                    .withOnDrawerItemClickListener(onDrawerItemClickListener);

            if (myBitmap != null) {
                newProfile.withIcon(myBitmap);
            } else
                newProfile.withIcon(R.drawable.profile_default_small);

            iProfileList.add(newProfile);
            headerResult.addProfile(newProfile, headerResult.getProfiles().size());
        }

        public void refreshProfileList () {
            profilesList = realmDB.where(User.class).findAll();
            int cnt = 0;
            for (User item : profilesList) {
                users_id[cnt] = item.getid();
                if (cnt++ > MAX_USER_PROFILE) break;
            }
        }

        public void deleteProfile (long id){
            int id_remove;
            for (int cnt = 0; cnt < iProfileList.size(); cnt++) {
                if (users_id[cnt] == id) {
                    iProfileList.remove(cnt);
                    //headerResult.removeProfile(cnt);
                    id_remove = (int) (users_id[cnt] + 2);
                    headerResult.removeProfileByIdentifier(id_remove);
                }
            }
            refreshProfileList();
        }

        public void mOnClickMethod (View view){
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        public void changeActiveProfile (final User user) {
            if (iProfileList != null) {
                if (iProfileList.size() > 0) {
                    for (int cnt = 0; cnt < iProfileList.size(); cnt++) {
                        if (users_id[cnt] == user.getid()) {
                            headerResult.setActiveProfile(iProfileList.get(cnt));

                            RealmResults<User> users = realmDB.where(User.class).findAll();
                            for (final User userInList : users) {
                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        userInList.setActive(false);
                                    }
                                });

                                if (profilesList != null && profilesList.get(cnt) != null) {
                                    profilesList.get(cnt).setActive(true);
                                } else {
                                    MainFunctions.toast(getApplicationContext(),
                                            getResources().getString(R.string.message_no_such_user));
                                }

                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        user.setActive(true);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void onDestroy () {
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
        protected void onSaveInstanceState (Bundle outState){
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
                    DataManager.updateReferences();
                    return null;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }
            };

            aTask.execute();
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        public void changeUserFragment () {
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
        public void onBackPressed () {
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
        public void onRequestPermissionsResult ( int requestCode,
        @NonNull String permissions[], @NonNull int[] grantResults){
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

        private Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                Log.d(TAG, "onDrawerItemClick");
                // TODO переход на редактирование профиля
                return false;
            }
        };

        private BottomBar setBottomBar () {
            BottomBar mBottomBar = findViewById(R.id.bottomBar);
            assert mBottomBar != null;
            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    switch (tabId) {
                        case R.id.menu_mytrainings:
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.frame_container, MyTrainingsFragment.newInstance()).
                                    commit();
                            break;
                        case R.id.menu_calendar:
                            getSupportFragmentManager().beginTransaction().
                                    replace(R.id.frame_container, TrainingsFragment.newInstance()).
                                    commit();
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

            mBottomBar.setActiveTabColor(getResources().getColor(R.color.larisaBlueColor));
            return mBottomBar;
        }

        private AccountHeader createAccountHeader (Bundle savedInstanceState){
            return new AccountHeaderBuilder()
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
                            if (profile instanceof IDrawerItem) {
                                if (profile.getIdentifier() == PROFILE_ADD) {
                                    currentFragment = FRAGMENT_USER;
                                    getSupportFragmentManager().beginTransaction().
                                            replace(R.id.frame_container, FragmentAddUser.newInstance()).
                                            commit();
                                }
                                if (profile.getIdentifier() == PROFILE_SETTINGS) {
                                    currentFragment = FRAGMENT_USER;
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_container, FragmentEditUser.newInstance()).
                                            commit();
                                }
                                if (profile.getIdentifier() > PROFILE_SETTINGS) {
                                    long profileId = profile.getIdentifier() - 2;
                                    int position;
                                    for (position = 0; position < iProfileList.size(); position++)
                                        if (users_id[position] == profileId) break;
                                    changeActiveProfile(profilesList.get(position));
                                    AuthorizedUser.getInstance().setUuid(profilesList.get(position).getUuid());
                                    currentFragment = FRAGMENT_USER;
                                    changeUserFragment();
                                }
                            }
                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .build();
        }

        private Drawer createDrawer(Toolbar toolbar, PrimaryDrawerItem taskPrimaryDrawerItem) {
            int larisaBlueColor = ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor);
            return new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withHasStableIds(true)
                    .withAccountHeader(headerResult)
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
                            new PrimaryDrawerItem().withName("Карта площадок")
                                    .withDescription("Расположение")
                                    .withIcon(R.drawable.menu_location)
                                    .withIdentifier(FRAGMENT_MAP)
                                    .withSelectable(false)
                                    .withIconColor(larisaBlueColor),
                            new PrimaryDrawerItem().withName("Добавить площадку")
                                    .withDescription("Стадион или корт")
                                    .withIcon(R.drawable.menu_ring)
                                    .withIdentifier(FRAGMENT_ADD_STADIUM)
                                    .withSelectable(false)
                                    .withIconColor(larisaBlueColor),
                            new PrimaryDrawerItem().withName("Соревнования")
                                    .withDescription("Туриниры и чемпионаты")
                                    .withIcon(R.drawable.menu_competition)
                                    .withIdentifier(FRAGMENT_EVENTS)
                                    .withSelectable(false)
                                    .withIconColor(larisaBlueColor),
                            new DividerDrawerItem(),
                            new PrimaryDrawerItem().withName("Команды")
                                    .withDescription("По видам спорта")
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
                                    case FRAGMENT_MY_TRAININGS:
                                        currentFragment = FRAGMENT_MY_TRAININGS;
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
                                    case FRAGMENT_ADD_STADIUM:
                                        currentFragment = FRAGMENT_ADD_STADIUM;
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.frame_container,
                                                        FragmentAddStadium.newInstance()).
                                                addToBackStack(null).commit();
                                        break;
                                    case FRAGMENT_EVENTS:
                                        currentFragment = FRAGMENT_EVENTS;
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.frame_container,
                                                        FragmentEvents.newInstance()).
                                                addToBackStack(null).commit();
                                        break;
                                    case FRAGMENT_USER:
                                        currentFragment = FRAGMENT_USER;
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.frame_container,
                                                        UserInfoFragment.newInstance()).commit();
                                        bottomBar.selectTabWithId(R.id.menu_user);
                                        break;
                                    case DRAWER_INFO:
                                        AboutDialog about = new AboutDialog(getApplicationContext());
                                        about.setTitle("О программе");
                                        about.show();
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
                    .withSavedInstance(savedInstance)
                    .withShowDrawerOnFirstLaunch(true)
                    .build();
        }
    }
