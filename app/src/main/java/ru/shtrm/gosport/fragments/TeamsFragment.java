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
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;

import ru.shtrm.gosport.R;

public class TeamsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner typeSpinner;
	private ListView teamListView;

    FloatingActionButton fab_check;

    public static TeamsFragment newInstance() {
		return new TeamsFragment();
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

        teamListView = (ListView) rootView.findViewById(R.id.erl_team_listView);
        teamListView.setOnItemClickListener(new ListviewClickListener());
        typeSpinner.setOnItemSelectedListener(spinnerListener);

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
        if (teamTypeUuid != null) {
            teams = realmDB.where(Team.class).equalTo("sport.uuid", teamTypeUuid).findAll();
        } else {
            teams = realmDB.where(Team.class).findAll();
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
                Bundle bundle = new Bundle();
                bundle.putString("uuid", team_uuid);
                FragmentAddTeam teamFragment = FragmentAddTeam.newInstance();
                teamFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, teamFragment).commit();
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

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
            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.frame_container, FragmentAddTeam.newInstance()).
                    addToBackStack("TeamsFragment").
                    commit();
        }
    }

}
