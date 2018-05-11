package ru.shtrm.gosport.fragments;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.StadiumAdapter;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.gps.TaskItemizedOverlay;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {

	private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
	Location location;
	ArrayList<OverlayItem> aOverlayItemArray;
    StadiumAdapter stadiumAdapter;
    Realm realmDB;
    private double curLatitude, curLongitude;
	private int LastItemPosition = -1;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return (new MapFragment());
    }

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        final ListView stadiumListView;

        toolbar.setSubtitle("Карта площадок");
        realmDB = Realm.getDefaultInstance();
        RealmResults<Stadium> stadiums;

		LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Нет разрешений на определение местоположения",
                        Toast.LENGTH_SHORT).show();
            }

            if (location == null) location = getLastKnownLocation();
			if (location != null) {
				curLatitude = location.getLatitude();
				curLongitude = location.getLongitude();
			}
		}

        final MapView mapView = (MapView) rootView.findViewById(R.id.gps_mapview);
		mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setBuiltInZoomControls(true);
        IMapController mapController = mapView.getController();
		mapController.setZoom(17);
		GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
		mapController.setCenter(point2);

		/*
		OverlayItem overlayItem = new OverlayItem("We are here", "WAH",
				new GeoPoint(curLatitude, curLongitude));
		aOverlayItemArray = new ArrayList<>();
		aOverlayItemArray.add(overlayItem);
		ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
				getActivity().getApplicationContext(), aOverlayItemArray, null);
		mapView.getOverlays().add(aItemizedIconOverlay);*/

        stadiumListView = (ListView) rootView
                .findViewById(R.id.gps_listView);

        //final ArrayList<GeoPoint> waypoints = new ArrayList<>();
        //GeoPoint currentPoint = new GeoPoint(curLatitude, curLongitude);
        //waypoints.add(currentPoint);

        stadiums = realmDB.where(Stadium.class).findAll();
        if (stadiums!=null) {
            stadiumAdapter = new StadiumAdapter(getContext(), stadiums, null);
            stadiumListView.setAdapter(stadiumAdapter);

            for (Stadium item : stadiums) {
                curLatitude = item.getLatitude();
                curLongitude = item.getLongitude();

                GeoPoint endPoint = new GeoPoint(curLatitude, curLongitude);
                //waypoints.add(endPoint);

                StadiumOverlayItem olItem = new StadiumOverlayItem(item.getTitle(), "Stadium", new GeoPoint(curLatitude, curLongitude));
                Drawable newMarker;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    newMarker = this.getResources().
                            getDrawable(R.drawable.stadium_marker_32,
                                    getActivity().getApplicationContext().getTheme());
                } else {
                    newMarker = this.getResources().getDrawable(R.drawable.stadium_marker_32);
                }
                olItem.setMarker(newMarker);
                overlayItemArray.add(olItem);
            }
        }

        stadiumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OverlayItem item = overlayItemArray.get(position);
				// Get the new Drawable
				Drawable marker = view.getResources().getDrawable(
						R.drawable.stadium_marker_32);
				// Set the new marker
				item.setMarker(marker);
				if (LastItemPosition >= 0) {
					OverlayItem item2 = overlayItemArray.get(LastItemPosition);
					marker = view.getResources().getDrawable(
							R.drawable.stadium_marker_32);
					item2.setMarker(marker);
				}
				LastItemPosition = position;
			}
		});

        stadiumListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
                Stadium stadium = stadiumAdapter.getItem(pos);
                if (stadium != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", stadium.getUuid());
                    Fragment f = StadiumInfoFragment.newInstance();
                    f.setArguments(bundle);
                    getActivity().getSupportFragmentManager().
                            beginTransaction().replace(R.id.frame_container, f).commit();

                }
                return true;
            }
        });

        TaskItemizedOverlay overlay = new TaskItemizedOverlay(getActivity()
				.getApplicationContext(), overlayItemArray) {
			@Override
			protected boolean onLongPressHelper(int index, OverlayItem item) {
                return super.onLongPressHelper(index, item);
			}
        };
		mapView.getOverlays().add(overlay);

        CompassOverlay compassOverlay = new CompassOverlay(getActivity()
                .getApplicationContext(), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(200, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

        return rootView;
	}

    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                location = mLocationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Нет разрешений на определение местоположения", Toast.LENGTH_SHORT).show();
            }

            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    class StadiumOverlayItem extends OverlayItem {
        public Stadium stadium;
        public StadiumOverlayItem(String a, String b, GeoPoint p) {
            super(a, b, p);
        }
    }

    private class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.frame_container, FragmentAddStadium.newInstance()).commit();
        }
    }
}
