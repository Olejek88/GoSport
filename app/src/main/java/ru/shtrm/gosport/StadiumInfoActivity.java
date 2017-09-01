package ru.shtrm.gosport;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.shtrm.gosport.db.realm.User;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class StadiumInfoActivity extends AppCompatActivity {
    private final static String TAG = "StadiumInfoActivity";
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    FloatingActionButton fab5;
    CoordinatorLayout rootLayout;
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;
    Animation show_fab_4;

    Animation hide_fab_4;
    Animation show_fab_5;
    Animation hide_fab_5;
    private Realm realmDB;

    private boolean FAB_Status = false;

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

    /**
     * Показываем документ из базы во внешнем приложении.
     *
     * @param file - файл
     */
    private void showDocument(File file) {
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        String[] patternList = file.getName().split("\\.");
        String extension = patternList[patternList.length - 1];

        if (mt.hasExtension(extension)) {
            String mimeType = mt.getMimeTypeFromExtension(extension);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), mimeType);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent viewFileIntent = Intent.createChooser(target, "Open File");
            try {
                startActivity(viewFileIntent);
            } catch (ActivityNotFoundException e) {
                // сообщить пользователю установить подходящее приложение
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        //stadium_uuid = b.getString("equipment_uuid");

        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initView();
    }

    private void initView() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab_4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab_5);

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);
        show_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab4_show);
        hide_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab4_hide);
        show_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab5_show);
        hide_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab5_hide);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!FAB_Status) {
                    expandFAB();
                    FAB_Status = true;
                } else {
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.stadium_layout);
        AccountHeader headerResult;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle("Обслуживание и ремонт");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.login_header)
                .withSavedInstance(savedInstanceState)
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_INFO) {
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
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);
        }
    }

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
    }

    private void expandFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 2.3);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.05);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (fab2.getWidth() * 2);
        layoutParams2.bottomMargin += (fab2.getHeight() * 2);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.05);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 2.3);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.rightMargin += (int) (fab4.getWidth() * 1.7);
        layoutParams4.bottomMargin += (int) (fab4.getHeight() * 0.9);
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(show_fab_4);
        fab4.setClickable(true);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.rightMargin += (int) (fab5.getWidth() * 0.9);
        layoutParams5.bottomMargin += (int) (fab5.getHeight() * 1.7);
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(show_fab_5);
        fab5.setClickable(true);
    }

    private void hideFAB() {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 2.3);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.05);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (fab2.getWidth() * 2);
        layoutParams2.bottomMargin -= (fab2.getHeight() * 2);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.05);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 2.3);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.rightMargin -= (int) (fab4.getWidth() * 1.7);
        layoutParams4.bottomMargin -= (int) (fab4.getHeight() * 0.9);
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(hide_fab_4);
        fab4.setClickable(false);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.rightMargin -= (int) (fab5.getWidth() * 0.9);
        layoutParams5.bottomMargin -= (int) (fab5.getHeight() * 1.7);
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(hide_fab_5);
        fab5.setClickable(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }
}
