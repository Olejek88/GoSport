package ru.shtrm.gosport.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.FragmentAddTrainingBinding;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.StadiumSpinnerAdapter;
import ru.shtrm.gosport.db.adapters.TeamSpinnerAdapter;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

public class FragmentAddTraining extends Fragment implements View.OnClickListener {
    private Context mainActivityConnector = null;
    private Training training;

    Spinner typeSpinner,stadiumSpinner,teamSpinner,levelSpinner;
    SportAdapter sportAdapter;
    StadiumSpinnerAdapter stadiumAdapter;
    TeamSpinnerAdapter teamAdapter;
    LevelAdapter levelAdapter;
    private EditText title, cost, comment;
    private TextView date;
    private Calendar calendar;
    int selectedMinute,selectedHour,selectedYear,selectedDay,selectedMonth;
    private Realm realmDB;

    public FragmentAddTraining() {
        // Required empty public constructor
    }

    public static FragmentAddTraining newInstance() {
        return (new FragmentAddTraining());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddTrainingBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_add_training, container, false);
        View view = binding.getRoot();
        realmDB = Realm.getDefaultInstance();

        typeSpinner = view.findViewById(R.id.training_sport_spinner);
        stadiumSpinner = view.findViewById(R.id.training_add_stadium);
        teamSpinner = view.findViewById(R.id.training_add_team);
        levelSpinner = view.findViewById(R.id.training_add_level);
        title = view.findViewById(R.id.training_add_title);
        cost = view.findViewById(R.id.training_add_cost);
        comment = view.findViewById(R.id.training_add_comment);
        date = view.findViewById(R.id.training_selected_time);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String trainingUuid = bundle.getString("uuid", "");
            training = realmDB.where(Training.class).equalTo("uuid",
                    trainingUuid).findFirst();
            if (training != null) {
                binding.setTraining(training);
            }
        }
        realmDB = Realm.getDefaultInstance();
        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Добавить тренировку");

        Button one = view.findViewById(R.id.training_button_submit);
        one.setOnClickListener(this);

        RealmResults<Sport> sport = realmDB.where(Sport.class).findAll();

        FillSpinners(null);

        sportAdapter = new SportAdapter(mainActivityConnector.getApplicationContext(), sport);
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(new SpinnerListener());

        // TODO переделать в биндинг
        date = view.findViewById(R.id.training_selected_time);
        if (training == null || (training!=null && training.getDate()==null)) {
            //String dateS = mainActivityConnector.getResources().getString(R.string.training_time);
            //date.setText(dateS);
            //date.setTextColor(mainActivityConnector.getResources().getColor(R.color.larisaTextColor));
        }
        date.setOnClickListener(new View.OnClickListener() {
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
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            selectedYear = year;
            selectedMonth = monthOfYear;
            selectedDay = dayOfMonth;
            calendar.set(selectedYear, selectedMonth, selectedDay);
            date.setText(getResources().getString(R.string.formatted_date,
                    selectedDay, selectedMonth + 1, selectedYear));
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeCallBack,
                    hour, minute, DateFormat.is24HourFormat(getActivity()));
            timePickerDialog.show();
        }
    };

    TimePickerDialog.OnTimeSetListener timeCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            selectedHour = hour;
            selectedMinute = minute;
            date.setText(getResources().getString(R.string.formatted_date_time,
                    selectedDay, selectedMonth + 1, selectedYear,
                    selectedHour,selectedMinute));
        }
    };

    void FillSpinners(Sport sport) {
        RealmResults<Stadium> stadiums;
        RealmResults<Team> teams;
        RealmResults<Level> levels;

        ArrayList<Team> teamList = new ArrayList<>();
        Team team = new Team().getEmptyTeam();

        if (sport==null) {
            stadiums = realmDB.where(Stadium.class).findAll();
            teams = realmDB.where(Team.class).findAll();
            levels = realmDB.where(Level.class).findAll();
        }
        else {
            stadiums = realmDB.where(Stadium.class).
                    equalTo("sport.uuid",sport.getUuid()).findAll();
            teams = realmDB.where(Team.class).
                    equalTo("sport.uuid",sport.getUuid()).findAll();
            levels = realmDB.where(Level.class).
                    equalTo("sport.uuid",sport.getUuid()).findAll();
        }
        teamList.add(team);
        teamList.addAll(teams.subList(0,teams.size()));

        stadiumAdapter = new StadiumSpinnerAdapter(mainActivityConnector.getApplicationContext(), stadiums, sport);
        stadiumSpinner.setAdapter(stadiumAdapter);

        teamAdapter = new TeamSpinnerAdapter(
                mainActivityConnector.getApplicationContext(), R.id.training_add_team, teamList);
        teamSpinner.setAdapter(teamAdapter);

        levelAdapter = new LevelAdapter(mainActivityConnector.getApplicationContext(), levels, sport);
        levelSpinner.setAdapter(levelAdapter);

        if (training != null) {
            for (int r = 0; r < stadiums.size(); r++) {
                if (training.getStadium().getUuid().equals(stadiums.get(r).getUuid()))
                    stadiumSpinner.setSelection(r);
            }
            for (int r = 0; r < teams.size(); r++) {
                if (training.getTeam().getUuid().equals(teams.get(r).getUuid()))
                    teamSpinner.setSelection(r);
            }
            for (int r = 0; r < levels.size(); r++) {
                if (training.getLevel() != null &&
                        training.getLevel().getUuid().equals(levels.get(r).getUuid()))
                    levelSpinner.setSelection(r);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.training_button_submit:
                final User user = realmDB.where(User.class).
                        equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();

                if (title.getText().length()<3)
                    {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                if (user == null) {
                    Toast.makeText(getActivity(),
                            mainActivityConnector.getResources().getString(R.string.message_register),
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                if (training==null) {
                    createTraining(user);
                }
                else {
                    updateTraining(user);
                }

                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, TrainingsFragment.newInstance()).commit();
                break;
            default:
                break;
        }

    }

    private void createTraining (User user) {
        String uuid = java.util.UUID.randomUUID().toString();
        realmDB.beginTransaction();
        Training newTraining = realmDB.createObject(Training.class, uuid);
        newTraining.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
        newTraining.setComment(comment.getText().toString());
        newTraining.setCost(Integer.parseInt(cost.getText().toString()));
        newTraining.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
        newTraining.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
        newTraining.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
        Team team = teamAdapter.getItem(teamSpinner.getSelectedItemPosition());
        if (team!=null && team.get_id()!=null)
            newTraining.setTeam(team);
        newTraining.setTitle(title.getText().toString());
        if (date.getText().length() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);
            newTraining.setDate(cal.getTime());
        } else {
            Toast.makeText(getActivity(), "Пожалуйста выберите дату тренировки", Toast.LENGTH_SHORT).show();
        }
        newTraining.setChangedAt(new Date());
        newTraining.setCreatedAt(new Date());
        newTraining.setUuid(uuid);
        newTraining.setUser(user);
        realmDB.commitTransaction();
    }

    private void updateTraining (User user) {
        realmDB.beginTransaction();
        training.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
        training.setComment(comment.getText().toString());
        training.setCost(Integer.parseInt(cost.getText().toString()));
        training.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
        training.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
        training.setStadium(stadiumAdapter.getItem(stadiumSpinner.getSelectedItemPosition()));
        training.setTeam(teamAdapter.getItem(teamSpinner.getSelectedItemPosition()));
        training.setTitle(title.getText().toString());
        if (date.getText().length() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);
            training.setDate(cal.getTime());
        } else {
            Toast.makeText(getActivity(), "Пожалуйста выберите дату тренировки",
                    Toast.LENGTH_SHORT).show();
        }
        training.setChangedAt(new Date());
        training.setUser(user);
        realmDB.commitTransaction();
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
            Sport typeSelected = (Sport) typeSpinner.getSelectedItem();
            FillSpinners(typeSelected);
        }
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
