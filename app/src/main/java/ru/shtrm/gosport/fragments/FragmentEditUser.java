package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.AmpluaAdapter;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserSport;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class FragmentEditUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private ImageView iView;
    private EditText name, vk, phone;
    private TextView age;
    private String image_name;
    private Realm realmDB;
    private DatePickerDialog dpd;
    private Amplua hockey_amplua, football_amplua;
    private Team hockey_team, football_team;
    private Level hockey_level, football_level;
    Sport hockey, football;
    private Spinner hockey_amplua_spinner, football_amplua_spinner;
    private Spinner hockey_team_spinner, football_team_spinner;
    private Spinner hockey_level_spinner, football_level_spinner;
    private TeamAdapter teamHockeyAdapter, teamFootballAdapter;
    private AmpluaAdapter ampluaHockeyAdapter, ampluaFootballAdapter;
    private LevelAdapter levelFootballAdapter, levelHockeyAdapter;
    private Calendar dateAndTime = Calendar.getInstance();

    public FragmentEditUser() {
        // Required empty public constructor
    }

    public static FragmentEditUser newInstance(String title) {
        return (new FragmentEditUser());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        realmDB = Realm.getDefaultInstance();
        iView = (ImageView) view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this); // calling onClick() method

        Button one = (Button) view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this); // calling onClick() method
        Button delete = (Button) view.findViewById(R.id.profile_button_delete);
        delete.setOnClickListener(this); // calling onClick() method

        name = (EditText) view.findViewById(R.id.profile_add_name);
        //age = (EditText) view.findViewById(R.id.profile_add_age);
        //age = (DatePicker) view.findViewById(R.id.profile_add_age);
        age = (TextView) view.findViewById(R.id.profile_add_age);

        phone = (EditText) view.findViewById(R.id.profile_add_phone);
        vk = (EditText) view.findViewById(R.id.profile_add_vk);

        hockey_amplua_spinner = (Spinner) view.findViewById(R.id.profile_hockey_amplua);
        hockey_level_spinner = (Spinner) view.findViewById(R.id.profile_hockey_level);
        hockey_team_spinner = (Spinner) view.findViewById(R.id.profile_hockey_team);

        football_amplua_spinner = (Spinner) view.findViewById(R.id.profile_football_amplua);
        football_level_spinner = (Spinner) view.findViewById(R.id.profile_football_level);
        football_team_spinner = (Spinner) view.findViewById(R.id.profile_football_team);

        RealmResults<Level> hockeyLevel;
        RealmResults<Amplua> hockeyAmplua;
        hockey = realmDB.where(Sport.class).equalTo("name", "Хоккей").findFirst();
        if (hockey != null) {
            hockeyLevel = realmDB.where(Level.class).equalTo("sport.uuid", hockey.getUuid()).findAll();
            levelHockeyAdapter = new LevelAdapter(getActivity().getBaseContext(), hockeyLevel, hockey);
            hockey_level_spinner.setAdapter(levelHockeyAdapter);
            hockeyAmplua = realmDB.where(Amplua.class).equalTo("sport.uuid", hockey.getUuid()).findAll();
            ampluaHockeyAdapter = new AmpluaAdapter(getActivity().getBaseContext(), hockeyAmplua, hockey);
            hockey_amplua_spinner.setAdapter(ampluaHockeyAdapter);
        } else {
            Toast toast = Toast.makeText(getActivity(), "Пожалуйста обновите справочники",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            return view;
        }
        RealmResults<Team> hockeyTeam;
        //hockeyTeam = realmDB.where(Team.class).equalTo("sport.uuid",hockey.getUuid()).findAll();
        hockeyTeam = realmDB.where(Team.class).findAll();
        teamHockeyAdapter = new TeamAdapter(getActivity().getApplicationContext(), hockeyTeam);
        hockey_team_spinner.setAdapter(teamHockeyAdapter);

        RealmResults<Level> footballLevel;
        RealmResults<Team> footballTeam;
        RealmResults<Amplua> footballAmplua;

        football = realmDB.where(Sport.class).equalTo("name", "Футбол").findFirst();

        if (football != null) {
            footballLevel = realmDB.where(Level.class).equalTo("sport.uuid", football.getUuid()).findAll();
            levelFootballAdapter = new LevelAdapter(getActivity().getApplicationContext(), footballLevel, football);
            football_level_spinner.setAdapter(levelFootballAdapter);

            footballTeam = realmDB.where(Team.class).equalTo("sport.uuid", football.getUuid()).findAll();
            teamFootballAdapter = new TeamAdapter(getActivity().getApplicationContext(), footballTeam);
            football_team_spinner.setAdapter(teamFootballAdapter);

            footballAmplua = realmDB.where(Amplua.class).equalTo("sport.uuid", football.getUuid()).findAll();
            ampluaFootballAdapter = new AmpluaAdapter(getActivity().getApplicationContext(), footballAmplua, football);
            football_amplua_spinner.setAdapter(ampluaFootballAdapter);
        } else {
            Toast toast = Toast.makeText(getActivity(), "Пожалуйста обновите справочники",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            return view;
        }
        User user = realmDB.where(User.class).equalTo("active", true).findFirst();
        if (user == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Пользователь не выбран, пожалуйста выберите или содайте профиль", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
        }

        if (user != null) {
            UserSport userSportHockey = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", hockey.getUuid()).findFirst();
            UserSport userSportFootball = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", football.getUuid()).findFirst();
            phone.setText(user.getPhone());
            name.setText(user.getName());
            vk.setText(user.getVk());
            if (user.getBirthDate() != null)
                age.setText(user.getBirthDate().getYear() + "-" + user.getBirthDate().getMonth() + "-" + user.getBirthDate().getDay());
            else
                age.setText(dateAndTime.get(Calendar.YEAR) + "-" + dateAndTime.get(Calendar.MONTH) + "-" + dateAndTime.get(Calendar.DAY_OF_MONTH));
            image_name = user.getImage();
            if (image_name==null)
                image_name = "profile_"+user.get_id()+".jpg";

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
                    if (userSportHockey.getTeam().getUuid().equals(hockeyTeam.get(r).getUuid()))
                        hockey_team_spinner.setSelection(r);
                }
            }
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
                    if (userSportFootball.getTeam().getUuid().equals(footballTeam.get(r).getUuid()))
                        football_team_spinner.setSelection(r);
                }
            }

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator;
            if (user.getChangedAt() != null) {
                Bitmap myBitmap = getResizedBitmap(path, user.getImage(), 0, 600, user.getChangedAt().getTime());
                if (myBitmap != null) {
                    iView.setImageBitmap(myBitmap);
                }
            }
        }

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
                fm.executePendingTransactions();
                newFragment.getDialog(). setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        age.setText(MainActivity.UserAge);
                    }
                });
            }
        });

        return view;
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(year, monthOfYear, dayOfMonth);
            age.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
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
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getActivity().getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                if (myBitmap != null) {
                    Bitmap myBitmap2 = MainFunctions.storeNewImage
                            (myBitmap, getActivity().getApplicationContext(), 1024);
                    if (myBitmap2!=null)
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
                if (name.getText().toString().length() < 2 || phone.getText().toString().length() < 2) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                    break;
                }
                try {
                    // название файла аватара не меняется (если оно было)
                    iView.buildDrawingCache();
                    Bitmap bmp = iView.getDrawingCache();
                    MainFunctions.storeImage(image_name, "User", getContext(), bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String vk_input = vk.getText().toString();
                String vk_output = "http://vk.com/undefined";
                if (vk_input.contains("vk") && !vk_input.contains("http"))
                    vk_output = "http://".concat(vk_input);
                if (!vk_input.contains("vk") && !vk_input.contains("http"))
                    vk_output = "http://vk.com/".concat(vk_input);

                User user = realmDB.where(User.class).equalTo("active", true).findFirst();
                realmDB.beginTransaction();
                user.setName(name.getText().toString());
                user.setVk(vk_output);
                user.setPhone(phone.getText().toString());
                user.setBirthDate(dateAndTime.getTime());
                //user.setAge(Integer.parseInt(age.getText().toString()));
                user.setChangedAt(new Date());
                realmDB.commitTransaction();

                UserSport userSport = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", hockey.getUuid()).findFirst();
                if (userSport != null) {
                    realmDB.beginTransaction();
                    userSport.setChangedAt(new Date());
                    userSport.setAmplua(ampluaHockeyAdapter.getItem(hockey_amplua_spinner.getSelectedItemPosition()));
                    userSport.setLevel(levelHockeyAdapter.getItem(hockey_level_spinner.getSelectedItemPosition()));
                    if (hockey_team_spinner.getSelectedItemPosition() > 0)
                        userSport.setTeam(teamHockeyAdapter.getItem(hockey_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                } else {
                    realmDB.beginTransaction();
                    userSport = realmDB.createObject(UserSport.class);
                    userSport.setSport(hockey);
                    userSport.setUuid(java.util.UUID.randomUUID().toString());
                    userSport.setUser(user);
                    userSport.setChangedAt(new Date());
                    userSport.setAmplua(ampluaHockeyAdapter.getItem(hockey_amplua_spinner.getSelectedItemPosition()));
                    userSport.setLevel(levelHockeyAdapter.getItem(hockey_level_spinner.getSelectedItemPosition()));
                    if (hockey_team_spinner.getSelectedItemPosition() > 0)
                        userSport.setTeam(teamHockeyAdapter.getItem(hockey_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                }

                UserSport userSport2 = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", football.getUuid()).findFirst();
                if (userSport2 != null) {
                    realmDB.beginTransaction();
                    userSport2.setAmplua(ampluaFootballAdapter.getItem(football_amplua_spinner.getSelectedItemPosition()));
                    userSport2.setLevel(levelFootballAdapter.getItem(football_level_spinner.getSelectedItemPosition()));
                    if (football_team_spinner.getSelectedItemPosition() > 0)
                        userSport2.setTeam(teamFootballAdapter.getItem(football_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                } else {
                    realmDB.beginTransaction();
                    userSport2 = realmDB.createObject(UserSport.class);
                    userSport2.setSport(football);
                    userSport2.setUuid(java.util.UUID.randomUUID().toString());
                    userSport2.setUser(user);
                    userSport2.setChangedAt(new Date());
                    userSport2.setAmplua(ampluaFootballAdapter.getItem(football_amplua_spinner.getSelectedItemPosition()));
                    userSport2.setLevel(levelFootballAdapter.getItem(football_level_spinner.getSelectedItemPosition()));
                    if (football_team_spinner.getSelectedItemPosition() > 0)
                        userSport2.setTeam(teamFootballAdapter.getItem(football_team_spinner.getSelectedItemPosition()));
                    realmDB.commitTransaction();
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
                break;

            case R.id.profile_button_delete:
                user = realmDB.where(User.class).equalTo("active", true).findFirst();
                ((MainActivity) getActivity()).deleteProfile(user.getid());
                realmDB.beginTransaction();
                user.deleteFromRealm();
                realmDB.commitTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
                break;

            default:
                break;
        }

    }

    public void storeImage(String name) throws IOException {
        Bitmap bmp;
        File sd_card = Environment.getExternalStorageDirectory();
        String target_filename = sd_card.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator + name;
        File target_file = new File(target_filename);
        if (!target_file.getParentFile().exists()) {
            target_file.getParentFile().mkdirs();
        }

        iView.buildDrawingCache();
        bmp = iView.getDrawingCache();
        FileOutputStream out = new FileOutputStream(target_file);
        if (bmp != null) {
            //Bitmap.createScaledBitmap(bmp, 100, 100, false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        }
        out.flush();
        out.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}