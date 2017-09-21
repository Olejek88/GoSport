package ru.shtrm.gosport.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TrainingAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Training;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_MULTIPLE;

public class MyTrainingsFragment extends Fragment {
    private Realm realmDB;
	private ListView trainingListView;
    //CalendarView simpleCalendarView;
    MaterialCalendarView simpleCalendarView;

    public static MyTrainingsFragment newInstance() {
		return new MyTrainingsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mytrainings, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        //simpleCalendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        simpleCalendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);
        simpleCalendarView.setSelectionMode(SELECTION_MODE_MULTIPLE);
        toolbar.setSubtitle("Мои тренировки");
        realmDB = Realm.getDefaultInstance();

        trainingListView = (ListView) rootView.findViewById(R.id.trainings_listView);
        trainingListView.setOnItemClickListener(new ListviewClickListener());

        initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView() {
		FillListViewTraining(null);
    }

	private void FillListViewTraining(Sport sport) {
        RealmResults<Training> trainings;
        if (sport != null) {
            trainings = realmDB.where(Training.class).equalTo("sport.uuid", sport.getUuid()).findAll();
        } else {
            trainings = realmDB.where(Training.class).findAll();
        }
        TrainingAdapter traningAdapter = new TrainingAdapter(getContext(), trainings, sport);
        trainingListView.setAdapter(traningAdapter);

        Date newdate1,newdate2;
        newdate1 = new Date();
        simpleCalendarView.setDateSelected(new Date(), true);
        for (Training training : trainings) {
            newdate2 = training.getDate();
            simpleCalendarView.setDateSelected(training.getDate(),true);
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

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            Training training = (Training) parentView.getItemAtPosition(position);
            if (training != null) {
                Bundle bundle = new Bundle();
                bundle.putString("training_uuid", training.getUuid());
                TrainingInfoFragment trainingInfoFragment = TrainingInfoFragment.newInstance();
                trainingInfoFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, trainingInfoFragment).commit();
            }
        }
    }

}
