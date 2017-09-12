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
import android.widget.ListView;
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;

public class TrainingsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner typeSpinner;
	private ListView teamListView;

    FloatingActionButton fab_check;

    private String object_uuid;

    public static TrainingsFragment newInstance() {
		return new TrainingsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.team_reference_layout, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Команды");
        realmDB = Realm.getDefaultInstance();

        fab_check = (FloatingActionButton) rootView.findViewById(R.id.fab_check);
        fab_check.setOnClickListener(new submitOnClickListener());

		// обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();
        teamListView = (ListView) rootView.findViewById(R.id.erl_team_listView);

        RealmResults<Sport> sports = realmDB.where(Sport.class).findAll();
        typeSpinner = (Spinner) rootView.findViewById(R.id.simple_spinner);

        SportAdapter sportAdapter = new SportAdapter(getContext(), sports);
        sportAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        teamListView = (ListView) rootView.findViewById(R.id.erl_team_listView);
        teamListView.setOnItemClickListener(new ListviewClickListener());

		initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		isInit = true;

		return rootView;
	}

	private void initView() {
		FillListViewTeam(null);
    }

	private void FillListViewTeam(String teamTypeUuid) {
        RealmResults<Team> teams;
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            object_uuid = bundle.getString("object_uuid");
        }
        if (teamTypeUuid != null) {
            teams = realmDB.where(Team.class).greaterThan("_id",2).equalTo("sport.uuid", teamTypeUuid).findAll();
        } else {
            teams = realmDB.where(Team.class).greaterThan("_id",2).findAll();
        }
        TeamAdapter teamAdapter = new TeamAdapter(getContext(), teams);
        teamListView.setAdapter(teamAdapter);
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
            Team team = (Team)parentView.getItemAtPosition(position);
            if (team != null) {
                String team_uuid = team.getUuid();
                //Intent teamInfo = new Intent(getActivity(),
                //        TeamInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("team_uuid", team_uuid);
                //equipmentInfo.putExtras(bundle);
                //getActivity().startActivity(equipmentInfo);
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

            FillListViewTeam(type);
        }
    }

    private class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentAddTeam.newInstance()).commit();
        }
    }

}
