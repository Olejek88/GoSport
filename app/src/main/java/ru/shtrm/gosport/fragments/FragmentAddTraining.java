package ru.shtrm.gosport.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.StadiumAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

public class FragmentAddTraining extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "FragmentAdd";
    Spinner typeSpinner,stadiumSpinner,teamSpinner,levelSpinner;
    SportAdapter sportAdapter;
    StadiumAdapter stadiumAdapter;
    TeamAdapter teamAdapter;
    LevelAdapter levelAdapter;
    private ImageView image_date;
    private EditText title, cost, comment;
    private TextView date;
    private Sport sport;
    private Stadium stadium;
    private Team team;
    private Level level;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Calendar c;
    int selectedMinute,selectedHour,selectedYear,selectedDay,selectedMonth;
    private Realm realmDB;


    public FragmentAddTraining() {
        // Required empty public constructor
    }

    public static FragmentAddTraining newInstance() {
        return (new FragmentAddTraining());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_training, container, false);
        realmDB = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Добавить тренировку");

        SpinnerListener spinnerListener = new SpinnerListener();

        Button one = (Button) view.findViewById(R.id.training_button_submit);
        one.setOnClickListener(this);

        title = (EditText) view.findViewById(R.id.training_add_title);
        typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        stadiumSpinner = (Spinner) view.findViewById(R.id.training_add_stadium);
        teamSpinner = (Spinner) view.findViewById(R.id.training_add_team);
        levelSpinner = (Spinner) view.findViewById(R.id.training_add_level);
        cost = (EditText) view.findViewById(R.id.training_add_cost);
        comment = (EditText) view.findViewById(R.id.training_add_comment);
        stadiumSpinner = (Spinner) view.findViewById(R.id.training_add_stadium);
        teamSpinner = (Spinner) view.findViewById(R.id.training_add_team);
        levelSpinner = (Spinner) view.findViewById(R.id.training_add_level);
        date = (TextView) view.findViewById(R.id.training_selected_time);
        image_date = (ImageView) view.findViewById(R.id.training_add_date_icon);

        RealmResults<Sport> sport;
        sport = realmDB.where(Sport.class).findAll();

        FillSpinners(null, view);

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        sportAdapter = new SportAdapter(getActivity().getApplicationContext(), sport);

        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog();
            }
        });
        image_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog();
            }
        });
        return view;
    }

    public void startDatePickerDialog() {
        c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker dialog, int year, int monthOfYear, int dayOfMonth) {
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        selectedYear=year;
        selectedMonth=monthOfYear;
        selectedDay=dayOfMonth;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedHour=hourOfDay;
        selectedMinute=minute;
        date.setText(selectedDay+"/"+(selectedMonth+1)+"/"+selectedYear+" "+selectedHour+":"+selectedMinute);
    }

    void FillSpinners(Sport sport, View view) {
        RealmResults<Stadium> stadiums;
        RealmResults<Team> teams;
        RealmResults<Level> levels;

        if (sport==null) {
            stadiums = realmDB.where(Stadium.class).findAll();
            teams = realmDB.where(Team.class).findAll();
            levels = realmDB.where(Level.class).findAll();
        }
        else {
            stadiums = realmDB.where(Stadium.class).equalTo("sport.uuid",sport.getUuid()).findAll();
            teams = realmDB.where(Team.class).equalTo("sport.uuid",sport.getUuid()).findAll();
            levels = realmDB.where(Level.class).equalTo("sport.uuid",sport.getUuid()).findAll();
        }
        stadiumAdapter = new StadiumAdapter(getActivity().getApplicationContext(), stadiums, sport);
        stadiumSpinner.setAdapter(stadiumAdapter);
        teamAdapter = new TeamAdapter(getActivity().getApplicationContext(), teams);
        teamSpinner.setAdapter(teamAdapter);
        levelAdapter = new LevelAdapter(getActivity().getApplicationContext(), levels, sport);
        levelSpinner.setAdapter(levelAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.training_button_submit:
                final User user = realmDB.where(User.class).equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
                if (title.getText().length()<3)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                if (user == null) {
                    Toast.makeText(getActivity(), "Пожалуйста зарегистрируйтесь (в меню слева)", Toast.LENGTH_SHORT).show();
                    break;
                }

                String uuid = java.util.UUID.randomUUID().toString();
                realmDB.beginTransaction();
                Training training = realmDB.createObject(Training.class,"uuid");
                training.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
                training.setComment(comment.getText().toString());
                training.setCost(Integer.parseInt(cost.getText().toString()));
                training.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
                training.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
                training.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
                training.setTeam(teamAdapter.getItem(teamSpinner.getSelectedItemPosition()));
                training.setTitle(title.getText().toString());
                if (date.getText().length()>0) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute,0);
                    training.setDate(cal.getTime());
                }
                else {
                    Toast.makeText(getActivity(), "Пожалуйста выберите дату тренировки", Toast.LENGTH_SHORT).show();
                }
                training.setChangedAt(new Date());
                training.setCreatedAt(new Date());
                training.setUuid(uuid);
                training.setUser(user);
                realmDB.commitTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TrainingsFragment.newInstance()).commit();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {
            Sport typeSelected;
            typeSelected = (Sport) typeSpinner.getSelectedItem();
            FillSpinners(typeSelected, parentView);
        }
    }

}
