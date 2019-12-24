package ru.shtrm.gosport.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.FragmentEditUserBinding;
import ru.shtrm.gosport.db.adapters.AmpluaAdapter;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserSport;
import ru.shtrm.gosport.model.AuthorizedUser;
import ru.shtrm.gosport.ui.MainActivity;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentEditUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private Context mainActivityConnector = null;

    private ImageView iView;
    private TextView birthDate;
    private EditText name, phone, vk;
    private String image_name;
    private Realm realmDB;
    Sport hockey, football;
    private Spinner hockey_amplua_spinner, football_amplua_spinner;
    private Spinner hockey_team_spinner, football_team_spinner;
    private Spinner hockey_level_spinner, football_level_spinner;
    private TeamAdapter teamHockeyAdapter, teamFootballAdapter;
    private AmpluaAdapter ampluaHockeyAdapter, ampluaFootballAdapter;
    private LevelAdapter levelFootballAdapter, levelHockeyAdapter;

    private Calendar calendar;
    int selectedYear, selectedDay, selectedMonth;

    public FragmentEditUser() {
        // Required empty public constructor
    }

    public static FragmentEditUser newInstance() {
        return (new FragmentEditUser());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEditUserBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_edit_user, container, false);
        View view = binding.getRoot();

        realmDB = Realm.getDefaultInstance();
        final User user = realmDB.where(User.class).equalTo("uuid",
                AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user == null) {
            Toast.makeText(mainActivityConnector.getApplicationContext(),
                    "Пользователь не выбран, пожалуйста выберите или содайте профиль",
                    Toast.LENGTH_LONG).show();
            ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                    replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
        } else {
            binding.setUser(user);
        }

        //TODO переделать в binding
        iView = view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this);

        Button one = view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this);
        Button delete = view.findViewById(R.id.profile_button_delete);
        delete.setOnClickListener(this);

        name = view.findViewById(R.id.profile_add_name);
        phone = view.findViewById(R.id.profile_add_phone);
        vk = view.findViewById(R.id.profile_add_vk);
        birthDate = view.findViewById(R.id.profile_add_age);

        hockey_amplua_spinner = view.findViewById(R.id.profile_hockey_amplua);
        hockey_level_spinner = view.findViewById(R.id.profile_hockey_level);

        hockey_team_spinner = view.findViewById(R.id.profile_hockey_team);

        football_amplua_spinner = view.findViewById(R.id.profile_football_amplua);
        football_level_spinner = view.findViewById(R.id.profile_football_level);
        football_team_spinner = view.findViewById(R.id.profile_football_team);

        hockey = realmDB.where(Sport.class).
                equalTo("name","Хоккей").findFirst();
        football = realmDB.where(Sport.class).
                equalTo("name","Футбол").findFirst();
        if (user!=null) {
            SetHockeyProfile(user);
            SetFootballProfile(user);
            String path = MainFunctions.getPicturesDirectory(mainActivityConnector);
            if (user.getChangedAt() != null) {
                Bitmap myBitmap = BitmapFactory.decodeFile(path + image_name);
                if (myBitmap != null) {
                    iView.setImageBitmap(myBitmap);
                }
            }
        }

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog();
            }
        });

        return view;
    }

    public void startDatePickerDialog() {
        calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(mainActivityConnector, dateCallBack, year, month, day);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            selectedYear = year;
            selectedMonth = monthOfYear;
            selectedDay = dayOfMonth;
            calendar.set(selectedYear, selectedMonth, selectedDay);
            birthDate.setText(getResources().getString(R.string.formatted_date,
                    selectedDay, selectedMonth + 1, selectedYear));
        }
    };

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = mainActivityConnector.getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                if (myBitmap != null) {
                    image_name = java.util.UUID.randomUUID().toString() + ".jpg";
                    Bitmap myBitmap2 = MainFunctions.storeNewImage
                            (myBitmap, mainActivityConnector.getApplicationContext(),
                                    1024, image_name);
                    if (myBitmap2 != null)
                        iView.setImageBitmap(myBitmap2);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_add_image:
                // do your code
                pickImage();
                break;

            case R.id.profile_button_submit:
                if (name.getText().toString().length() < 2 ||
                        phone.getText().toString().length() < 2) {
                    Toast.makeText(mainActivityConnector.getApplicationContext(),
                            "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                    break;
                }
                User user = realmDB.where(User.class).equalTo("active", true).findFirst();
                realmDB.beginTransaction();
                user.setName(name.getText().toString());
                user.setImage(image_name);
                user.setVk(MainFunctions.getVkProfile(vk.getText().toString()));
                user.setPhone(phone.getText().toString());
                if (calendar != null)
                    user.setBirthDate(calendar.getTime());
                user.setChangedAt(new Date());
                realmDB.commitTransaction();

                UserSport userSport = realmDB.where(UserSport.class).
                        equalTo("user.uuid", user.getUuid()).
                        equalTo("sport.uuid", hockey.getUuid()).findFirst();
                if (userSport != null) {
                    realmDB.beginTransaction();
                    userSport.setChangedAt(new Date());
                    userSport.setAmplua(ampluaHockeyAdapter.
                            getItem(hockey_amplua_spinner.getSelectedItemPosition()));
                    userSport.setLevel(levelHockeyAdapter.
                            getItem(hockey_level_spinner.getSelectedItemPosition()));
                    if (hockey_team_spinner.getSelectedItemPosition() >= 0)
                        userSport.setTeam(teamHockeyAdapter.
                                getItem(hockey_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                } else {
                    realmDB.beginTransaction();
                    userSport = realmDB.createObject(UserSport.class);
                    userSport.setSport(hockey);
                    userSport.setUuid(java.util.UUID.randomUUID().toString());
                    userSport.setUser(user);
                    userSport.setChangedAt(new Date());
                    userSport.setAmplua(ampluaHockeyAdapter.
                            getItem(hockey_amplua_spinner.getSelectedItemPosition()));
                    userSport.setLevel(levelHockeyAdapter.
                            getItem(hockey_level_spinner.getSelectedItemPosition()));
                    if (hockey_team_spinner.getSelectedItemPosition() >= 0)
                        userSport.setTeam(teamHockeyAdapter.
                                getItem(hockey_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                }

                UserSport userSport2 = realmDB.where(UserSport.class).
                        equalTo("user.uuid", user.getUuid()).
                        equalTo("sport.uuid", football.getUuid()).findFirst();
                if (userSport2 != null) {
                    realmDB.beginTransaction();
                    userSport2.setAmplua(ampluaFootballAdapter.
                            getItem(football_amplua_spinner.getSelectedItemPosition()));
                    userSport2.setLevel(levelFootballAdapter.
                            getItem(football_level_spinner.getSelectedItemPosition()));
                    if (football_team_spinner.getSelectedItemPosition() >= 0)
                        userSport2.setTeam(teamFootballAdapter.
                                getItem(football_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                } else {
                    realmDB.beginTransaction();
                    userSport2 = realmDB.createObject(UserSport.class);
                    userSport2.setSport(football);
                    userSport2.setUuid(java.util.UUID.randomUUID().toString());
                    userSport2.setUser(user);
                    userSport2.setChangedAt(new Date());
                    userSport2.setAmplua(ampluaFootballAdapter.
                            getItem(football_amplua_spinner.getSelectedItemPosition()));
                    userSport2.setLevel(levelFootballAdapter.
                            getItem(football_level_spinner.getSelectedItemPosition()));
                    if (football_team_spinner.getSelectedItemPosition() >= 0)
                        userSport2.setTeam(teamFootballAdapter.
                                getItem(football_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                }

                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                break;

            case R.id.profile_button_delete:
                user = realmDB.where(User.class).equalTo("active", true).findFirst();
                ((MainActivity) mainActivityConnector).deleteProfile(user.getid());
                realmDB.beginTransaction();
                user.deleteFromRealm();
                realmDB.commitTransaction();
                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
                break;

            default:
                break;
        }

    }

    private void SetHockeyProfile(User user) {
        UserSport userSportHockey = realmDB.where(UserSport.class).
                equalTo("user.uuid", user.getUuid()).
                equalTo("sport.uuid", hockey.getUuid()).
                findFirst();
        RealmResults<Level> hockeyLevel;
        RealmResults<Amplua> hockeyAmplua;
        RealmResults<Team> hockeyTeam = realmDB.where(Team.class).findAll();
        teamHockeyAdapter = new TeamAdapter(mainActivityConnector, hockeyTeam);
        hockey_team_spinner.setAdapter(teamHockeyAdapter);

        hockey = realmDB.where(Sport.class).equalTo("name", "Хоккей").findFirst();
        if (hockey != null) {
            hockeyLevel = realmDB.where(Level.class).
                    equalTo("sport.uuid", hockey.getUuid()).findAll();
            levelHockeyAdapter = new LevelAdapter(mainActivityConnector, hockeyLevel, hockey);
            hockey_level_spinner.setAdapter(levelHockeyAdapter);
            hockeyAmplua = realmDB.where(Amplua.class).
                    equalTo("sport.uuid", hockey.getUuid()).findAll();
            ampluaHockeyAdapter = new AmpluaAdapter(mainActivityConnector, hockeyAmplua, hockey);
            hockey_amplua_spinner.setAdapter(ampluaHockeyAdapter);
            // TODO не нравится мне такой метод, но все таки
            if (userSportHockey != null) {
                for (int r = 0; r < hockeyLevel.size(); r++) {
                    if (userSportHockey.getLevel().getUuid().equals(hockeyLevel.get(r).getUuid()))
                        hockey_level_spinner.setSelection(r);
                }
                for (int r = 0; r < hockeyAmplua.size(); r++) {
                    if (userSportHockey.getAmplua().getUuid().equals(hockeyAmplua.get(r).getUuid()))
                        hockey_amplua_spinner.setSelection(r);
                }
                for (int r = 0; r < hockeyTeam.size(); r++) {
                    if (userSportHockey.getTeam() != null &&
                            userSportHockey.getTeam().getUuid().equals(hockeyTeam.get(r).getUuid()))
                        hockey_team_spinner.setSelection(r);
                }
            }
        } else {
            Toast toast = Toast.makeText(mainActivityConnector,
                    mainActivityConnector.getResources().getString(R.string.message_upgrade_references),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    private void SetFootballProfile(User user) {
        UserSport userSportFootball = realmDB.where(UserSport.class).
                equalTo("user.uuid", user.getUuid()).
                equalTo("sport.uuid", football.getUuid()).
                findFirst();

        RealmResults<Level> footballLevel;
        RealmResults<Amplua> footballAmplua;
        RealmResults<Team> footballTeam = realmDB.where(Team.class).findAll();

        football = realmDB.where(Sport.class).equalTo("name", "Футбол").findFirst();
        teamFootballAdapter = new TeamAdapter(mainActivityConnector, footballTeam);
        football_team_spinner.setAdapter(teamFootballAdapter);

        if (football != null) {
            footballLevel = realmDB.where(Level.class).
                    equalTo("sport.uuid", football.getUuid()).findAll();
            levelFootballAdapter = new LevelAdapter(mainActivityConnector.getApplicationContext(),
                    footballLevel, football);
            football_level_spinner.setAdapter(levelFootballAdapter);

            footballTeam = realmDB.where(Team.class).
                    equalTo("sport.uuid", football.getUuid()).findAll();
            teamFootballAdapter = new TeamAdapter(mainActivityConnector.getApplicationContext(),
                    footballTeam);
            football_team_spinner.setAdapter(teamFootballAdapter);

            footballAmplua = realmDB.where(Amplua.class).
                    equalTo("sport.uuid", football.getUuid()).findAll();
            ampluaFootballAdapter = new AmpluaAdapter(mainActivityConnector.getApplicationContext(),
                    footballAmplua, football);
            football_amplua_spinner.setAdapter(ampluaFootballAdapter);

            if (userSportFootball != null) {
                for (int r = 0; r < footballLevel.size(); r++) {
                    if (userSportFootball.getLevel().getUuid().equals(footballLevel.get(r).getUuid()))
                        football_level_spinner.setSelection(r);
                }
                for (int r = 0; r < footballAmplua.size(); r++) {
                    if (userSportFootball.getAmplua().getUuid().equals(footballAmplua.get(r).getUuid()))
                        football_amplua_spinner.setSelection(r);
                }
                for (int r = 0; r < footballTeam.size(); r++) {
                    if (userSportFootball.getTeam() != null &&
                            userSportFootball.getTeam().getUuid().equals(footballTeam.get(r).getUuid()))
                        football_team_spinner.setSelection(r);
                }
            }
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    mainActivityConnector.getResources().getString(R.string.message_upgrade_references),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityConnector = getActivity();
        // TODO решить что делать если контекст не приехал
        if (mainActivityConnector == null)
            onDestroyView();
    }
}