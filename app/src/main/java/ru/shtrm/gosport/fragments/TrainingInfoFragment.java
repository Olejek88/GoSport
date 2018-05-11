package ru.shtrm.gosport.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import io.realm.Realm;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.TrainingInfoFragmentBinding;
import ru.shtrm.gosport.db.realm.Training;

import static android.content.Context.LOCATION_SERVICE;

public class TrainingInfoFragment extends Fragment {
    private Realm realmDB;
    TrainingInfoFragmentBinding binding;
    String training_uuid;
    Location location;
    private double curLatitude, curLongitude;

    public static TrainingInfoFragment newInstance() {
        return (new TrainingInfoFragment());
    }

    ArrayList<OverlayItem> aOverlayItemArray;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.training_info_fragment,
                container,false);
        View view = binding.getRoot();

        Toolbar toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Тренировка");

        Bundle b = this.getArguments();
        if (b!=null)
            training_uuid = b.getString("training_uuid");

        realmDB = Realm.getDefaultInstance();
        initView(view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    private void initView(View view) {
        ImageView iv_sport, call_image;
        final ArrayList<GeoPoint> waypoints = new ArrayList<>();

        iv_sport = (ImageView) view.findViewById(R.id.training_sport_icon);
        call_image = (ImageView) view.findViewById(R.id.training_phone_icon);

        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                }
                return;
            }
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
                GeoPoint currentPoint = new GeoPoint(curLatitude, curLongitude);
                waypoints.add(currentPoint);
            }
        }

        final Training training = realmDB.where(Training.class).equalTo("uuid", training_uuid).findFirst();
        if (training == null) {
            Toast.makeText(getActivity(), "Нет такой тренировки!", Toast.LENGTH_SHORT).show();
        } else {
            binding.setTraining(training);
            if (training.getSport().getTitle().equals("Хоккей")) {
                iv_sport.setImageResource(R.drawable.user_hockey);
            } else {
                iv_sport.setImageResource(R.drawable.menu_football);
            }

            if (training.getUser().getPhone() != null) {
                call_image.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent = new Intent(Intent.ACTION_CALL);
                      intent.setData(Uri.parse("tel:" + training.getUser().getPhone()));
                      startActivity(intent);
                  }
              });
            }
        }
        final MapView mapView = (MapView) view.findViewById(R.id.gps_mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        if (training!=null && training.getStadium()!=null) {
            GeoPoint point2 = new GeoPoint(training.getStadium().getLatitude(), training.getStadium().getLongitude());
            mapController.setCenter(point2);
            OverlayItem overlayItem = new OverlayItem("Тренировка здесь", "Training",
                    new GeoPoint(training.getStadium().getLatitude(), training.getStadium().getLongitude()));
            aOverlayItemArray = new ArrayList<>();
            aOverlayItemArray.add(overlayItem);
            ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
                    getActivity().getApplicationContext(), aOverlayItemArray, null);
            mapView.getOverlays().add(aItemizedIconOverlay);
            //GeoPoint endPoint = new GeoPoint(training.getStadium().getLatitude(), training.getStadium().getLongitude());
            //waypoints.add(endPoint);
        }
        CompassOverlay compassOverlay = new CompassOverlay(getActivity()
                .getApplicationContext(), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(200, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
