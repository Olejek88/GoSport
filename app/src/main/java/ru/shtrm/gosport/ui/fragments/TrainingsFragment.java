package ru.shtrm.gosport.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import ru.shtrm.gosport.db.adapters.TrainingAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.ui.MainActivity;

public class TrainingsFragment extends Fragment {
    private Realm realmDB;
    private Context mainActivityConnector = null;

	private Spinner typeSpinner;
	private ListView trainingListView;
    TrainingAdapter trainingAdapter = null;

    FloatingActionButton fab_check;

    public static TrainingsFragment newInstance() {
		return new TrainingsFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.training_list_layout, container, false);
        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Тренировки");
        realmDB = Realm.getDefaultInstance();

        fab_check = rootView.findViewById(R.id.fab_check);
        fab_check.setOnClickListener(new submitOnClickListener());

        //realmDB.beginTransaction();
        //realmDB.where(Training.class).findAll().deleteAllFromRealm();
        //realmDB.commitTransaction();

        SpinnerListener spinnerListener = new SpinnerListener();
        RealmResults<Sport> sports = realmDB.where(Sport.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);

        SportAdapter sportAdapter = new SportAdapter(mainActivityConnector, sports);
        sportAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        trainingListView = rootView.findViewById(R.id.trainings_listView);

        initView();

        trainingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {
                Training training = trainingAdapter.getItem(pos);
                if (training != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", training.getUuid());
                    TrainingInfoFragment trainingInfoFragment = TrainingInfoFragment.newInstance();
                    trainingInfoFragment.setArguments(bundle);
                    ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, trainingInfoFragment).commit();
                }
            }
        });

        trainingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
                Training training = trainingAdapter.getItem(pos);
                if (training != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", training.getUuid());
                    Fragment f = FragmentAddTraining.newInstance();
                    f.setArguments(bundle);
                    ((AppCompatActivity)mainActivityConnector).getSupportFragmentManager().
                            beginTransaction().replace(R.id.frame_container, f).commit();

                }
                return true;
            }
        });

        rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView() {
		FillListViewTraining(null);
    }

	private void FillListViewTraining(Sport sport) {
        RealmResults<Training> trainings;
        /*Bundle bundle = this.getArguments();
        if(bundle != null) {
            String object_uuid = bundle.getString("object_uuid");
        }*/
        if (sport != null) {
            trainings = realmDB.where(Training.class).
                    equalTo("sport.uuid", sport.getUuid()).
                    findAllSorted("date");
        } else {
            trainings = realmDB.where(Training.class).findAllSorted("date");
        }
        trainingAdapter = new TrainingAdapter(mainActivityConnector, trainings, sport);
        trainingListView.setAdapter(trainingAdapter);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			initView();
		}
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
            if (typeSelected != null) {
                FillListViewTraining(typeSelected);
            }
        }
    }

    private class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                    replace(R.id.frame_container, FragmentAddTraining.newInstance()).commit();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
