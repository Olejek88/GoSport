package ru.shtrm.gosport.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
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
    private Context mainActivityConnector = null;
    private static final int GPS_REQUEST_CODE = 1;
	private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
    StadiumOverlayItem olItem;

	Location location;
    StadiumAdapter stadiumAdapter;
    Realm realmDB;
    private double curLatitude, curLongitude;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return (new MapFragment());
    }

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);

        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Карта площадок");
        final ListView stadiumListView;

        realmDB = Realm.getDefaultInstance();
        RealmResults<Stadium> stadiums;

		LocationManager lm = (LocationManager) mainActivityConnector.getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(),
                        mainActivityConnector.getResources().getString(R.string.message_no_gps_permission),
                        Toast.LENGTH_SHORT).show();
            }

            if (location == null) location = getLastKnownLocation();
			if (location != null) {
				curLatitude = location.getLatitude();
				curLongitude = location.getLongitude();
			}
		}

        final MapView mapView = rootView.findViewById(R.id.gps_mapview);
        if (mapView!=null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            IMapController mapController = mapView.getController();
            mapController.setZoom(17f);

            GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
            mapController.setCenter(point2);
        }

        stadiumListView = rootView.findViewById(R.id.gps_listView);

        stadiums = realmDB.where(Stadium.class).findAll();
        if (stadiums!=null) {
            stadiumAdapter = new StadiumAdapter(mainActivityConnector, stadiums, null);
            stadiumListView.setAdapter(stadiumAdapter);

            for (Stadium item : stadiums) {
                curLatitude = item.getLatitude();
                curLongitude = item.getLongitude();

                olItem = new StadiumOverlayItem(
                        item.getTitle() + ", " + item.getSport().getTitle(), "Stadium",
                        new GeoPoint(curLatitude, curLongitude));
                Drawable newMarker;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    newMarker = this.getResources().
                            getDrawable(R.drawable.stadium_marker_32,
                                    mainActivityConnector.getApplicationContext().getTheme());
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
				final OverlayItem item = overlayItemArray.get(position);

				/*
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
				LastItemPosition = position;*/

				item.getPoint().getLongitude();
				if (mapView!=null) {
                    mapView.getController().setZoom(17f);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mapView.getController().animateTo(
                                    new GeoPoint(item.getPoint().getLatitude(),
                                            item.getPoint().getLongitude()));
                        }
                    }, 500);
                }
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
                    ((AppCompatActivity)mainActivityConnector).getSupportFragmentManager().
                            beginTransaction().replace(R.id.frame_container, f).commit();

                }
                return true;
            }
        });

        TaskItemizedOverlay overlay = new TaskItemizedOverlay(mainActivityConnector
				.getApplicationContext(), overlayItemArray) {
			@Override
			protected boolean onLongPressHelper(int index, OverlayItem item) {
                return super.onLongPressHelper(index, item);
			}
        };

        if (mapView!=null) {
            mapView.getOverlays().add(overlay);
            CompassOverlay compassOverlay = new CompassOverlay(mainActivityConnector
                    .getApplicationContext(), mapView);
            compassOverlay.enableCompass();
            mapView.getOverlays().add(compassOverlay);
            ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
            mScaleBarOverlay.setCentred(true);
            //play around with these values to get the location on screen in the right place for your applicatio
            mScaleBarOverlay.setScaleBarOffset(200, 10);
            mapView.getOverlays().add(mScaleBarOverlay);
        }

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissions, GPS_REQUEST_CODE);

        return rootView;
	}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), mainActivityConnector.getResources().
                                    getString(R.string.message_no_gps_permission),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)mainActivityConnector.
                getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert mLocationManager!=null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                location = mLocationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(),
                        mainActivityConnector.getResources().getString(R.string.message_no_gps_permission),
                        Toast.LENGTH_SHORT).show();
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
        private StadiumOverlayItem(String a, String b, GeoPoint p) {
            super(a, b, p);
        }
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
