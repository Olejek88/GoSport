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
import ru.shtrm.gosport.EventInfoActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.StadiumInfoActivity;
import ru.shtrm.gosport.db.adapters.EventAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TrainingAdapter;
import ru.shtrm.gosport.db.realm.Event;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Training;

public class EventsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner typeSpinner;
	private ListView eventsListView;

    FloatingActionButton fab_check;

    private String object_uuid;

    public static EventsFragment newInstance() {
		return new EventsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.events_list_layout, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Соревнования");
        realmDB = Realm.getDefaultInstance();

        SpinnerListener spinnerListener = new SpinnerListener();
        RealmResults<Sport> sports = realmDB.where(Sport.class).findAll();
        typeSpinner = (Spinner) rootView.findViewById(R.id.simple_spinner);

        SportAdapter sportAdapter = new SportAdapter(getContext(), sports);
        sportAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        eventsListView = (ListView) rootView.findViewById(R.id.events_listView);
        eventsListView.setOnItemClickListener(new ListviewClickListener());

        initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView() {
		FillListViewEvents(null);
    }

	private void FillListViewEvents(Sport sport) {
        RealmResults<Event> events;
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            object_uuid = bundle.getString("object_uuid");
        }
        if (sport != null) {
            events = realmDB.where(Event.class).equalTo("sport.uuid", sport.getUuid()).findAll();
        } else {
            events = realmDB.where(Event.class).findAll();
        }
        EventAdapter eventAdapter = new EventAdapter(getContext(), events, sport);
        eventsListView.setAdapter(eventAdapter);
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
            Event event = (Event) parentView.getItemAtPosition(position);
            if (event != null) {
                String event_uuid = event.getUuid();
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("event_uuid", event_uuid);
                eventInfo.putExtras(bundle);
                getActivity().startActivity(eventInfo);
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

            FillListViewEvents(typeSelected);
        }
    }
}
