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
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.EventAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.realm.Event;
import ru.shtrm.gosport.db.realm.Sport;

public class FragmentEvents extends Fragment {
    private Realm realmDB;
    private Context mainActivityConnector = null;

	private Spinner typeSpinner;
	private ListView eventsListView;

    public static FragmentEvents newInstance() {
		return new FragmentEvents();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmDB = Realm.getDefaultInstance();

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Соревнования");

        SpinnerListener spinnerListener = new SpinnerListener();
        RealmResults<Sport> sports = realmDB.where(Sport.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);

        SportAdapter sportAdapter = new SportAdapter(mainActivityConnector, sports);
        sportAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(sportAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        eventsListView = rootView.findViewById(R.id.events_listView);
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
        if (sport != null) {
            events = realmDB.where(Event.class).equalTo("sport.uuid", sport.getUuid()).findAll();
        } else {
            events = realmDB.where(Event.class).findAll();
        }
        EventAdapter eventAdapter = new EventAdapter(mainActivityConnector, events, sport);
        eventsListView.setAdapter(eventAdapter);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			initView();
		}
	}

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            //Event event = (Event) parentView.getItemAtPosition(position);
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

            Sport typeSelected;
            typeSelected = (Sport) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                FillListViewEvents(typeSelected);
            }
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
        if (mainActivityConnector==null)
            onDestroyView();
    }

}
