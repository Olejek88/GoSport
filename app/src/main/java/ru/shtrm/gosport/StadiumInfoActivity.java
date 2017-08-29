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
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagStructure;
import ru.toir.mobile.utils.DataUtils;

import static ru.toir.mobile.utils.MainFunctions.getEquipmentImage;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

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
                showDialogDefect(notification, (ViewGroup) v.getParent());
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogStatus(notification, (ViewGroup) v.getParent());
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
                readRFIDTag(notification);
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeRFIDTag(notification);
            }
        });

    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.equipment_layout);
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
                .withHeaderBackground(R.drawable.login_header)
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

    public void showDialogDefect(final ru.toir.mobile.db.realm.Notification notification, ViewGroup parent) {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.add_defect_dialog, parent, false);

        RealmResults<DefectType> defectType = realmDB.where(DefectType.class).findAll();
        final Spinner defectTypeSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_defects);
        final DefectTypeAdapter typeSpinnerAdapter = new DefectTypeAdapter(this, defectType);
        defectTypeSpinner.setAdapter(typeSpinnerAdapter);

        defectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DefectType typeSelected = (DefectType) defectTypeSpinner
                        .getSelectedItem();
                if (typeSelected != null) {
                    DefectType currentDefectType = typeSpinnerAdapter.getItem(defectTypeSpinner.getSelectedItemPosition());
                    if (currentDefectType != null) {
                        RealmResults<ru.toir.mobile.db.realm.Event> events = realmDB.where(ru.toir.mobile.db.realm.Event.class).equalTo("DefectType.uuid", currentDefectType.getUuid()).findAll();
                        Spinner defectSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_defects);
                        ru.toir.mobile.db.adapters.EventAdapter eventAdapter = new ru.toir.mobile.db.adapters.EventAdapter(getApplicationContext(), events);
                        defectSpinner.setAdapter(eventAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Укажите дефект");
        alert.setView(alertLayout);
        alert.setIcon(R.drawable.ic_icon_warnings);
        alert.setCancelable(false);
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView newDefect = (TextView) alertLayout.findViewById(R.id.add_new_comment);
                DefectType currentDefectType = null;
                if (defectTypeSpinner.getSelectedItemPosition() >= 0) {
                    currentDefectType = typeSpinnerAdapter.getItem(defectTypeSpinner.getSelectedItemPosition());
                }
                if (newDefect != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    final ru.toir.mobile.db.realm.Event event = realmDB.createObject(ru.toir.mobile.db.realm.Event.class);
                    UUID uuid = UUID.randomUUID();
                    long next_id = realm.where(ru.toir.mobile.db.realm.Event.class).max("_id").intValue() + 1;
                    event.set_id(next_id);
                    event.setUuid(uuid.toString().toUpperCase());
                    event.setDate(new Date());
                    event.setComment(newDefect.getText().toString());
                    event.setProcess(false);
                    event.setCreatedAt(new Date());
                    event.setChangedAt(new Date());
                    event.setEquipment(notification);
                    if (currentDefectType != null) {
                        event.setDefectType(currentDefectType);
                    } else {
                        event.setDefectType(null);
                    }

                    AuthorizedUser authUser = AuthorizedUser.getInstance();
                    User user = realmDB.where(User.class).equalTo("tagId", authUser.getTagId()).findFirst();
                    if (user != null) {
                        event.setUser(user);
                    } else {
                        event.setUser(null);
                    }

                    event.setTask(null);
                    realm.commitTransaction();
                    realm.close();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }

    public void showDialogStatus(final ru.toir.mobile.db.realm.Notification notification, ViewGroup parent) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_status_dialog, parent, false);
        RealmResults<EquipmentStatus> equipmentStatus = realmDB.where(EquipmentStatus.class).findAll();
        final Spinner statusSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_status);
        final EquipmentStatusAdapter equipmentStatusAdapter = new EquipmentStatusAdapter(this, R.id.spinner_status, equipmentStatus);
        statusSpinner.setAdapter(equipmentStatusAdapter);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Статус оборудования");
        alert.setView(alertLayout);
        alert.setIcon(R.drawable.ic_icon_tools);
        alert.setCancelable(false);
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                notification.setEquipmentStatus(equipmentStatusAdapter.getItem(statusSpinner.getSelectedItemPosition()));
                realm.commitTransaction();
                realm.close();
            }
        });

        TextView statusCurrent = (TextView) alertLayout.findViewById(R.id.current_status);
        statusCurrent.setText(notification.getEquipmentStatus().getTitle());

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_icon_tools);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String path;
            String objUuid;
            Documentation documentation = (Documentation) parent.getItemAtPosition(position);
            // TODO: как все таки пути к файлу формируем
            if (documentation.getEquipment() != null && documentation.getEquipmentModel() == null) {
                objUuid = documentation.getEquipment().getUuid();
            } else if (documentation.getEquipment() == null && documentation.getEquipmentModel() != null) {
                objUuid = documentation.getEquipmentModel().getUuid();
            } else if (documentation.getEquipment() != null && documentation.getEquipmentModel() != null) {
                objUuid = documentation.getEquipment().getUuid();
            } else {
                return;
            }

            path = "/documentation/" + objUuid + "/";
            File file = new File(getExternalFilesDir(path), documentation.getPath());
            if (file.exists()) {
                showDocument(file);
            } else {
                // либо сказать что файла нет, либо предложить скачать с сервера
                Log.d(TAG, "Получаем файл документации.");
//				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getApplicationContext(),
//						ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);
//				registerReceiver(mReceiverGetDocumentationFile, mFilterGetDocumentationFile);
//				rsh.getDocumentationFile(new String[] { documentation.getUuid() });

                // запускаем поток получения файла с сервера
                AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String fileElements[] = params[0].split("/");
                        String url = GoSportApplication.serverUrl + "/storage/" + params[0];
                        Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().getFile(url);
                        try {
                            Response<ResponseBody> r = call1.execute();
                            ResponseBody trueImgBody = r.body();
                            if (trueImgBody == null) {
                                return null;
                            }

                            File file = new File(getApplicationContext().getExternalFilesDir("/documentation/" + fileElements[0]), fileElements[1]);
                            if (!file.getParentFile().exists()) {
                                if (!file.getParentFile().mkdirs()) {
                                    Log.e(TAG, "Не удалось создать папку " +
                                            file.getParentFile().toString() +
                                            " для сохранения файла изображения!");
                                    return null;
                                }
                            }

                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(trueImgBody.bytes());
                            fos.close();
                            return file.getAbsolutePath();
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String filePath) {
                        super.onPostExecute(filePath);
                        loadDocumentationDialog.dismiss();
                        if (filePath != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Файл загружен успешно и готов к просмотру.",
                                    Toast.LENGTH_LONG).show();
                            showDocument(new File(filePath));
                        } else {
                            // сообщаем описание неудачи
                            Toast.makeText(getApplicationContext(), "Ошибка при получении файла.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                task.execute(objUuid + "/" + documentation.getPath());

                // показываем диалог получения наряда
                loadDocumentationDialog = new ProgressDialog(ru.shtrm.aggrra.StadiumInfoActivity.this);
                loadDocumentationDialog.setMessage("Получаем файл документации");
                loadDocumentationDialog.setIndeterminate(true);
                loadDocumentationDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                loadDocumentationDialog.setCancelable(false);
                loadDocumentationDialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//								unregisterReceiver(mReceiverGetDocumentationFile);
                                Toast.makeText(getApplicationContext(),
                                        "Получение файла отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                loadDocumentationDialog.show();
            }
        }

    }
}
