package ru.shtrm.gosport.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.UserTrainingListAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserTraining;
import ru.shtrm.gosport.ui.MainActivity;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;

public class MyTrainingsFragment extends Fragment {
    private Realm realmDB;
    private Context mainActivityConnector = null;

    private ListView trainingListView;
    MaterialCalendarView simpleCalendarView;

    public static MyTrainingsFragment newInstance() {
        return new MyTrainingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mytrainings, container, false);
        Toolbar toolbar = ((AppCompatActivity) mainActivityConnector).findViewById(R.id.toolbar);
        simpleCalendarView = rootView.findViewById(R.id.calendarView);
        simpleCalendarView.setSelectionMode(SELECTION_MODE_NONE);
        simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {

            }
        });
        toolbar.setSubtitle("Мои тренировки");
        realmDB = Realm.getDefaultInstance();

        trainingListView = rootView.findViewById(R.id.trainings_listView);
        trainingListView.setOnItemClickListener(new ListviewClickListener());

        initView();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void initView() {
        FillListViewTraining(null, null);
    }

    private void FillListViewTraining(Sport sport, Date date) {
        RealmResults<UserTraining> userTrainings;
        User user = new User().getActiveUser(realmDB);
        if (user == null) return;
        if (sport != null) {
            userTrainings = realmDB.where(UserTraining.class).
                    equalTo("training.sport.uuid", sport.getUuid()).
                    equalTo("user.uuid", user.getUuid()).
                    findAll();
        } else {
            userTrainings = realmDB.where(UserTraining.class).
                    equalTo("user.uuid", user.getUuid()).
                    findAll();
        }
        UserTrainingListAdapter trainingAdapter = new UserTrainingListAdapter(mainActivityConnector,
                userTrainings);
        trainingListView.setAdapter(trainingAdapter);

        Date newDate;
        simpleCalendarView.setDateSelected(new Date(), true);
        for (UserTraining userTraining : userTrainings) {
            newDate = userTraining.getTraining().getDate();
            simpleCalendarView.setDateSelected(newDate, true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initView();
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

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            UserTraining userTraining = (UserTraining) parentView.getItemAtPosition(position);
            if (userTraining != null) {
                Bundle bundle = new Bundle();
                bundle.putString("uuid", userTraining.getTraining().getUuid());
                TrainingInfoFragment trainingInfoFragment = TrainingInfoFragment.newInstance();
                trainingInfoFragment.setArguments(bundle);
                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, trainingInfoFragment).commit();
            }
        }
    }

}
