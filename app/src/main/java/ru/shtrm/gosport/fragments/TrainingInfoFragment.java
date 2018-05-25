package ru.shtrm.gosport.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;

import io.realm.Realm;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.TrainingInfoFragmentBinding;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserTraining;

import static android.content.Context.LOCATION_SERVICE;

public class TrainingInfoFragment extends Fragment {
    private Realm realmDB;
    private Activity mainActivityConnector = null;
    private Training training;
    TrainingInfoFragmentBinding binding;
    String training_uuid;
    Location location;

    public static TrainingInfoFragment newInstance() {
        return (new TrainingInfoFragment());
    }

    ArrayList<OverlayItem> aOverlayItemArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.training_info_fragment,
                container,false);
        View view = binding.getRoot();
        FloatingActionButton training_accept = view.findViewById(R.id.training_submit);
        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Тренировка");

        Bundle bundle = this.getArguments();
        if (bundle!=null)
            training_uuid = bundle.getString("uuid");

        realmDB = Realm.getDefaultInstance();
        initView(view);
        if (training_accept!=null) {
            setFABColor(training_accept);
            training_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFABClick(v);
                }
            });
        }
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    private void initView(View view) {
        ImageView iv_sport, call_image;
        double curLatitude, curLongitude;
        final ArrayList<GeoPoint> waypoints = new ArrayList<>();

        iv_sport = view.findViewById(R.id.training_sport_icon);
        call_image = view.findViewById(R.id.training_phone_icon);

        LocationManager lm = (LocationManager) mainActivityConnector.getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(mainActivityConnector,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mainActivityConnector,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivityConnector,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(mainActivityConnector,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                } else {
                    ActivityCompat.requestPermissions(mainActivityConnector,
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

        training = realmDB.where(Training.class).equalTo("uuid", training_uuid).findFirst();
        if (training == null) {
            Toast.makeText(getActivity(),
                    mainActivityConnector.getResources().getString(R.string.message_no_such_training),
                    Toast.LENGTH_SHORT).show();
        } else {
            binding.setTraining(training);
            if (training.getSport().getTitle().equals("Хоккей")) {
                iv_sport.setImageResource(R.drawable.menu_hockey);
            } else {
                iv_sport.setImageResource(R.drawable.user_football);
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
        final MapView mapView = view.findViewById(R.id.gps_mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(17f);
        if (training!=null && training.getStadium()!=null) {
            GeoPoint point2 = new GeoPoint(training.getStadium().getLatitude(),
                    training.getStadium().getLongitude());
            mapController.setCenter(point2);
            OverlayItem overlayItem = new OverlayItem("Тренировка здесь", "Training",
                    new GeoPoint(training.getStadium().getLatitude(), training.getStadium().getLongitude()));
            aOverlayItemArray = new ArrayList<>();
            aOverlayItemArray.add(overlayItem);
            ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
                    mainActivityConnector.getApplicationContext(), aOverlayItemArray, null);
            mapView.getOverlays().add(aItemizedIconOverlay);
            //GeoPoint endPoint = new GeoPoint(training.getStadium().getLatitude(), training.getStadium().getLongitude());
            //waypoints.add(endPoint);
        }
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

    public void onFABClick(View view) {
        final User user = realmDB.where(User.class).
                equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
        UserTraining userTraining = realmDB.where(UserTraining.class).
                equalTo("user.uuid", user.getUuid()).
                equalTo("training.uuid", training_uuid).
                findFirst();
        FloatingActionButton training_accept = view.findViewById(R.id.training_submit);
        if (userTraining!=null) {
            realmDB.beginTransaction();
            userTraining.deleteFromRealm();
            realmDB.commitTransaction();
        }
        else {
            realmDB.beginTransaction();
            String uuid = java.util.UUID.randomUUID().toString();
            userTraining = realmDB.createObject(UserTraining.class, uuid);
            userTraining.setTraining(training);
            userTraining.setUser(user);
            userTraining.setUuid(uuid);
            userTraining.setChangedAt(new Date());
            userTraining.setCreatedAt(new Date());
            realmDB.commitTransaction();
        }
        setFABColor(training_accept);
    }

    public void setFABColor(FloatingActionButton training_accept) {
        final User user = realmDB.where(User.class).
                equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
        UserTraining userTraining = realmDB.where(UserTraining.class).
                equalTo("user.uuid", user.getUuid()).
                equalTo("training.uuid", training_uuid).
                findFirst();
        if (userTraining == null) {
            training_accept.setBackgroundTintList(
                    ColorStateList.valueOf(mainActivityConnector.getResources().getColor(R.color.larisaBlueColor)));
            training_accept.setImageResource(R.drawable.ic_plus_white_36dp);
        } else {
            training_accept.setBackgroundTintList(
                    ColorStateList.valueOf(mainActivityConnector.getResources().getColor(R.color.green)));
            training_accept.setImageResource(R.drawable.check_box);
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
        if (mainActivityConnector == null)
            onDestroyView();
    }
}
