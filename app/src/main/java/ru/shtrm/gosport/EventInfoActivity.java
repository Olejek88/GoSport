package ru.shtrm.gosport;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import ru.shtrm.gosport.db.realm.Event;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class EventInfoActivity extends AppCompatActivity {
    private final static String TAG = "EventInfoActivity";
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    CoordinatorLayout rootLayout;

    private TextView tv_event_title;
    private TextView tv_event_sport;
    private TextView tv_event_description;
    private TextView tv_event_date;
    private ImageView tv_event_image;
    private Realm realmDB;
    private String event_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        event_uuid = b.getString("event_uuid");
        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.event_layout);

        tv_event_title = (TextView) findViewById(R.id.event_text_name);
        tv_event_sport = (TextView) findViewById(R.id.event_text_sport);
        tv_event_description= (TextView) findViewById(R.id.event_text_description);
        tv_event_date =(TextView) findViewById(R.id.event_text_date);
        tv_event_image =(ImageView) findViewById(R.id.event_image);

        initView();
    }

    private void initView() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH);

        final Event event = realmDB.where(Event.class).equalTo("uuid", event_uuid).findFirst();
        if (event == null) {
            Toast.makeText(getApplicationContext(), "Неизвестное соревнование", Toast.LENGTH_LONG).show();
            return;
        }

        tv_event_title.setText(event.getTitle());
        tv_event_description.setText(event.getDescription());
        tv_event_date.setText(fmt.format(event.getDate().getTime()));
        tv_event_sport.setText(event.getSport().getTitle());

        Bitmap image_bitmap = getResizedBitmap(MainFunctions.getPicturesDirectory(getApplicationContext()), event.getImage(), 0, 300, event.getChangedAt().getTime());
        if (image_bitmap != null) {
            tv_event_image.setImageBitmap(image_bitmap);
        }
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.event_layout);
        AccountHeader headerResult;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle("Событие");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.hockey_header)
                .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almostblack))
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }
}
