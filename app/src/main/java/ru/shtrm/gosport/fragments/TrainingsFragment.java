package ru.shtrm.gosport.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.adapters.TrainingAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;

public class TrainingsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner typeSpinner;
	private ListView trainingListView;

    FloatingActionButton fab_check;

    private String object_uuid;

    public static TrainingsFragment newInstance() {
		return new TrainingsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.training_list_layout, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Команды");
        realmDB = Realm.getDefaultInstance();

        fab_check = (FloatingActionButton) rootView.findViewById(R.id.fab_check);
        fab_check.setOnClickListener(new submitOnClickListener());

        SpinnerListener spinnerListener = new SpinnerListener();
        RealmResults<Sport> sports = realmDB.where(Sport.class).findAll();
        typeSpinner = (Spinner) rootView.findViewById(R.id.simple_spinner);

        SportAdapter sportAdapter = new SportAdapter(getContext(), sports);
        sportAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

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
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            object_uuid = bundle.getString("object_uuid");
        }
        if (sport != null) {
            trainings = realmDB.where(Training.class).equalTo("sport.uuid", sport.getUuid()).findAll();
        } else {
            trainings = realmDB.where(Training.class).findAll();
        }
        TrainingAdapter traningAdapter = new TrainingAdapter(getContext(), trainings, sport);
        trainingListView.setAdapter(traningAdapter);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {
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

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        //boolean userSelect = false;

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            String type = null;
            Sport typeSelected;
            typeSelected = (Sport) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                type = typeSelected.getUuid();
            }

            FillListViewTraining(typeSelected);
        }
    }

    private class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentAddTraining.newInstance()).commit();
        }
    }

}
