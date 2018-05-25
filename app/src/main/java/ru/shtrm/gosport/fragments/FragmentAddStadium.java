package ru.shtrm.gosport.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.utils.MainFunctions;

import static android.content.Context.LOCATION_SERVICE;
import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class FragmentAddStadium extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_STADIUM = 1;
    private static final int WRITE_REQUEST_CODE = 1;
    private Context mainActivityConnector = null;
    private static final String TAG = "FragmentAdd";
    private String stadiumUuid = null;
    private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();

    private ItemizedIconOverlay<OverlayItem> currentIconOverlay=null;

    double stadiumLatitude=0.0, stadiumLongitude=0.0;
    Spinner typeSpinner;
    SportAdapter sportAdapter;
    private ImageView iView;
    private EditText title, description, address;
    private Realm realmDB;
    private Bitmap stadiumBitmap = null;
    Location location;

    public FragmentAddStadium() {
        // Required empty public constructor
    }

    public static FragmentAddStadium newInstance() {
        return (new FragmentAddStadium());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_stadium, container, false);
        double curLatitude=55.175708, curLongitude=61.390594;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            stadiumUuid = bundle.getString("uuid", "");
        }

        realmDB = Realm.getDefaultInstance();

        LocationManager lm = (LocationManager) mainActivityConnector.getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Нет разрешений на определение местоположения",
                        Toast.LENGTH_SHORT).show();
            }

            if (location==null) {
                location=MainFunctions.getLastKnownLocation(mainActivityConnector.
                        getApplicationContext());
            }
            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
            }
        }

        final MapView mapView = view.findViewById(R.id.stadium_mapview);
        if (mapView!=null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            IMapController mapController = mapView.getController();
            mapController.setZoom(17);

            GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
            mapController.setCenter(point2);

            // Добавляем несколько слоев
            CompassOverlay compassOverlay = new CompassOverlay(mainActivityConnector
                    .getApplicationContext(), mapView);
            compassOverlay.enableCompass();
            mapView.getOverlays().add(compassOverlay);

            ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
            mScaleBarOverlay.setCentred(true);
            mScaleBarOverlay.setScaleBarOffset(200, 10);
            mapView.getOverlays().add(mScaleBarOverlay);
        }

        iView = view.findViewById(R.id.stadium_add_image);
        iView.setOnClickListener(this);

        Button one = view.findViewById(R.id.stadium_button_submit);
        one.setOnClickListener(this);
        typeSpinner = view.findViewById(R.id.simple_spinner);
        title = view.findViewById(R.id.stadium_add_title);
        description = view.findViewById(R.id.stadium_add_description);
        address = view.findViewById(R.id.stadium_add_address);

        RealmResults<Sport> sport = realmDB.where(Sport.class).findAll();
        Spinner typeSpinner = view.findViewById(R.id.simple_spinner);
        sportAdapter = new SportAdapter(mainActivityConnector.getApplicationContext(), sport);
        typeSpinner.setAdapter(sportAdapter);

        DummyOverlay dumOverlay = new DummyOverlay(getContext());
        List<Overlay> listOfOverlays = mapView != null ? mapView.getOverlays() : null;
        if (listOfOverlays != null) {
            listOfOverlays.clear();
            listOfOverlays.add(dumOverlay);
        }

        if (stadiumUuid!=null) {
            Stadium stadium = realmDB.where(Stadium.class).
                    equalTo("uuid", stadiumUuid).findFirst();
            if (stadium !=null) {
                title.setText(stadium.getTitle());
                description.setText(stadium.getDescription());
                address.setText(stadium.getAddress());

                // TODO разобраться с binding на imageview
                String path = MainFunctions.getPicturesDirectory(mainActivityConnector);
                Bitmap stadium_bitmap = getResizedBitmap(path, stadium.getImage(),
                        0, 600, stadium.getChangedAt().getTime());
                if (stadium_bitmap != null) {
                    iView.setImageBitmap(stadium_bitmap);
                }

                for (int r = 0; r < sport.size(); r++) {
                    if (stadium.getSport()!=null &&
                            stadium.getSport().getUuid().equals(sport.get(r).getUuid()))
                        typeSpinner.setSelection(r);
                }

                if (mapView!=null) {
                    GeoPoint point = new GeoPoint(stadium.getLatitude(), stadium.getLongitude());
                    IMapController mapController = mapView.getController();
                    mapController.setCenter(point);

                    stadiumLatitude = stadium.getLatitude();
                    stadiumLongitude = stadium.getLongitude();

                    StadiumOverlayItem olItem = new StadiumOverlayItem(stadium.getTitle(),
                            "Stadium", new GeoPoint(stadiumLatitude, stadiumLongitude));
                    Drawable newMarker;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        newMarker = this.getResources().
                                getDrawable(R.drawable.stadium_marker_32, mainActivityConnector.
                                        getApplicationContext().getTheme());
                    } else {
                        newMarker = this.getResources().getDrawable(R.drawable.stadium_marker_32);
                    }
                    olItem.setMarker(newMarker);
                    overlayItemArray.add(olItem);
                    ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
                            mainActivityConnector.getApplicationContext(),
                            overlayItemArray, null);
                    mapView.getOverlays().add(aItemizedIconOverlay);
                }
            }
        }
        /*
        new Thread(new Runnable() {
            public void run() {
                Road road;
                RoadManager roadManager = new OSRMRoadManager(getActivity().getApplicationContext());
                try {
                    road = roadManager.getRoad(waypoints);
                    roadManager.addRequestOption("routeType=pedestrian");
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.RED, 8);
                    mapView.getOverlays().add(roadOverlay);
                    mapView.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();        */

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

        FragmentTransaction fragmentTransaction = ((MainActivity)mainActivityConnector).
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, MapFragment.newInstance()).addToBackStack(null);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Нет разрешений на вывод карты!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class DummyOverlay extends org.osmdroid.views.overlay.Overlay {

        private DummyOverlay(Context ctx) {
            super(ctx); // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas c, MapView osmv, boolean shadow) {

        }

        @Override
        public boolean onDoubleTap(MotionEvent e, MapView mapView) {
            Projection proj = mapView.getProjection();
            IGeoPoint loc = proj.fromPixels((int)e.getX(), (int)e.getY());
            stadiumLongitude = loc.getLongitude();
            stadiumLatitude = loc.getLatitude();
            //stadiumLatitude=mapView.getLatitudeSpanDouble();
            //stadiumLongitude=mapView.getLongitudeSpanDouble();
            Toast.makeText(getActivity(),
                    "Площадка находится здесь",
                    Toast.LENGTH_SHORT).show();

            StadiumOverlayItem olItem = new StadiumOverlayItem("",
                    "Stadium", new GeoPoint(stadiumLatitude, stadiumLongitude));
            Drawable newMarker;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                newMarker = mainActivityConnector.getResources().
                        getDrawable(R.drawable.stadium_marker_32,
                                mainActivityConnector.getApplicationContext().getTheme());
            } else {
                newMarker = mainActivityConnector.getResources().
                        getDrawable(R.drawable.stadium_marker_32);
            }
            olItem.setMarker(newMarker);
            ArrayList<OverlayItem> currentItemArray = new ArrayList<>();
            currentItemArray.add(olItem);
            if (currentIconOverlay==null) {
                currentIconOverlay = new ItemizedIconOverlay<>(
                        mainActivityConnector.getApplicationContext(),
                        currentItemArray, null);
            }
            mapView.getOverlays().add(currentIconOverlay);
            return true;
        }

    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_STADIUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_STADIUM && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = mainActivityConnector.getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                stadiumBitmap = BitmapFactory.decodeStream(inputStream);
                if (stadiumBitmap!=null) {
                    int width= (int) (300*(float)(stadiumBitmap.getWidth()/stadiumBitmap.getHeight()));
                    if (width>0) {
                        Bitmap myBitmap2 = Bitmap.
                                createScaledBitmap(stadiumBitmap, width, 300, false);
                        iView.setImageBitmap(myBitmap2);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.stadium_add_image:
                // do your code
                pickImage();
                break;

            case R.id.stadium_button_submit:
                String image_name=null;
                final User user = realmDB.where(User.class).
                        equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
                if (stadiumUuid!=null) {
                    Stadium stadium = realmDB.where(Stadium.class).
                            equalTo("uuid", stadiumUuid).findFirst();
                    if (stadium != null) {
                        if (false) {
                            Toast.makeText(mainActivityConnector.getApplicationContext(),
                                    "Вы не имеете права изменить эту площадку. " +
                                            " " + stadium.getUser().getUuid() +
                                            " " + user.getUuid() +
                                            "Если в описании присутствует неточность - " +
                                            "сообщите администратору через форму на сайте.",
                                    Toast.LENGTH_LONG).show();
                            break;
                        }
                        if (stadiumBitmap != null) {
                            image_name = stadium.getUuid() + ".jpg";
                            MainFunctions.storeNewImage(stadiumBitmap, getContext(), 1024,
                                    image_name);
                        }
                        realmDB.beginTransaction();
                        stadium.setTitle(title.getText().toString());
                        stadium.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
                        stadium.setDescription(description.getText().toString());
                        stadium.setAddress(address.getText().toString());
                        stadium.setUser(user);
                        if (image_name!=null)
                            stadium.setImage(image_name);
                        stadium.setChangedAt(new Date());
                        stadium.setLatitude(stadiumLatitude);
                        stadium.setLongitude(stadiumLongitude);
                        realmDB.commitTransaction();
                    }
                }
                else {
                    Stadium stadium_c = realmDB.where(Stadium.class).
                            equalTo("name", title.getText().toString()).findFirst();
                    if (stadium_c!=null) {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Такой стадион уже есть", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (title.getText().length()<3)
                    {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (stadiumLatitude==0.0)
                    {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Выберите координаты площадки на карте", Toast.LENGTH_LONG).show();
                        break;
                    }

                    String uuid = java.util.UUID.randomUUID().toString();
                    if (stadiumBitmap != null)
                        image_name = uuid + ".jpg";
                    else
                        image_name = null;
                    Log.e(TAG, "name=" + image_name);
                    if (stadiumBitmap != null)
                        MainFunctions.storeNewImage(stadiumBitmap, getContext(), 1024, image_name);

                    realmDB.beginTransaction();
                    Stadium stadium = realmDB.createObject(Stadium.class, uuid);
                    stadium.setTitle(title.getText().toString());
                    stadium.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
                    stadium.setDescription(description.getText().toString());
                    stadium.setAddress(address.getText().toString());
                    stadium.setChangedAt(new Date());
                    stadium.setCreatedAt(new Date());
                    stadium.setUuid(uuid);
                    stadium.setUser(user);
                    stadium.setLatitude(stadiumLatitude);
                    stadium.setLongitude(stadiumLongitude);
                    if (image_name != null)
                        stadium.setImage(image_name);
                    realmDB.commitTransaction();
                }
                ((MainActivity)mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, MapFragment.newInstance()).commit();
                break;
            default:
                break;
        }

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
