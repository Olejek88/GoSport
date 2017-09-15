package ru.shtrm.gosport.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.StadiumAdapter;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

import static android.content.Context.LOCATION_SERVICE;
import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class TrainingInfoFragment extends Fragment {
    private Realm realmDB;
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
        View rootView = inflater
                .inflate(R.layout.user_layout, container, false);
        Toolbar toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Тренировка");

        Bundle b = getActivity().getIntent().getExtras();
        training_uuid = b.getString("stadium_uuid");

        realmDB = Realm.getDefaultInstance();
        initView(rootView);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void initView(View view) {
        TextView tv_title;
        TextView tv_team;
        TextView tv_comment;
        TextView tv_cost;
        TextView tv_sport;
        TextView tv_level;
        TextView tv_stadium;
        TextView tv_contact;
        TextView tv_date;
        ImageView iv_sport, call_image;
        final ArrayList<GeoPoint> waypoints = new ArrayList<>();

        tv_title = (TextView) view.findViewById(R.id.training_text_name);
        tv_team = (TextView) view.findViewById(R.id.training_team);
        tv_stadium = (TextView) view.findViewById(R.id.training_location);
        tv_comment = (TextView) view.findViewById(R.id.training_comment);
        tv_cost = (TextView) view.findViewById(R.id.training_cost);
        tv_sport = (TextView) view.findViewById(R.id.training_sport);
        //tv_level = (TextView) view.findViewById(R.id.training_sport_level);
        tv_contact = (TextView) view.findViewById(R.id.training_phone);
        tv_date = (TextView) view.findViewById(R.id.training_date);

        iv_sport = (ImageView) view.findViewById(R.id.training_sport_icon);
        call_image = (ImageView) view.findViewById(R.id.training_phone_icon);

        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
                } else {
                    // No explanation needed, we can request the permission.
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
			tv_title.setText(training.getTitle());
            tv_team.setText(training.getTeam().getTitle());
            tv_stadium.setText(training.getStadium().getTitle());
            tv_comment.setText(training.getComment());
            tv_cost.setText(training.getCost());
            tv_sport.setText(training.getSport().getTitle());
            tv_contact.setText(training.getUser().getName()+" ("+training.getUser().getPhone()+")");
            tv_date.setText(training.getDate().toString());
            if (training.getSport().getTitle().equals("Хоккей")) {
                iv_sport.setImageResource(R.drawable.hockey_32);
            } else {
                iv_sport.setImageResource(R.drawable.football_32);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
