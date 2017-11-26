package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.realm.LocalFiles;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentAddStadium extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_STADIUM = 1;
    private static final String TAG = "FragmentAdd";
    Spinner typeSpinner;
    SportAdapter sportAdapter;
    private EditText iView;
    private EditText title, description, address;
    private Sport sport;
    private Realm realmDB;

    public FragmentAddStadium() {
        // Required empty public constructor
    }

    public static FragmentAddStadium newInstance() {
        return (new FragmentAddStadium());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addstadium, container, false);
        realmDB = Realm.getDefaultInstance();

        final MapView mapView = (MapView) view.findViewById(R.id.stadium_mapview);
        if (mapView!=null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setBuiltInZoomControls(true);
            IMapController mapController = mapView.getController();
            mapController.setZoom(17);
        }

        iView = (EditText) view.findViewById(R.id.stadium_add_photo);
        iView.setOnClickListener(this);
        Button one = (Button) view.findViewById(R.id.stadium_button_submit);
        one.setOnClickListener(this);
        typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        title = (EditText) view.findViewById(R.id.stadium_add_title);
        description = (EditText) view.findViewById(R.id.stadium_add_description);
        address = (EditText) view.findViewById(R.id.stadium_add_address);

        RealmResults<Sport> sport;
        sport = realmDB.where(Sport.class).findAll();

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        sportAdapter = new SportAdapter(getActivity().getApplicationContext(), sport);
        typeSpinner.setAdapter(sportAdapter);

        DummyOverlay dumOverlay = new DummyOverlay(getContext());
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(dumOverlay);
        return view;
    }

    private class DummyOverlay extends org.osmdroid.views.overlay.Overlay {

        public DummyOverlay(Context ctx) {
            super(ctx); // TODO Auto-generated constructor stub
        }

        @Override
        protected void draw(Canvas c, MapView osmv, boolean shadow) {

        }

        @Override
        public boolean onDoubleTap(MotionEvent e, MapView mapView) {
            mapView.getLatitudeSpanDouble();
            mapView.getLongitudeSpanDouble();
            Toast.makeText(getActivity(), "!="+mapView.getLatitudeSpanDouble()+" "+mapView.getLongitudeSpanDouble(), Toast.LENGTH_SHORT).show();
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
            if (data == null) {
                return;
            }
            iView.setText(data.getData().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.team_add_image:
                // do your code
                pickImage();
                break;

            case R.id.team_button_submit:
                Team team_c = realmDB.where(Team.class).equalTo("title", title.getText().toString()).findFirst();
                String image_name = "profile";
                Bitmap bmp;
                if (team_c!=null) {
                     Toast.makeText(getActivity().getApplicationContext(),
                            "Такая команда уже есть", Toast.LENGTH_LONG).show();
                     break;
                    }
                if (title.getText().length()<3)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                realmDB.beginTransaction();
                Number currentIdNum = realmDB.where(Team.class).max("_id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                Team team = realmDB.createObject(Team.class,nextId);
                team.setTitle(title.getText().toString());
                team.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
                team.setDescription(description.getText().toString());
                team.setChangedAt(new Date());
                team.setCreatedAt(new Date());
                team.setUuid(java.util.UUID.randomUUID().toString());
                try {
                    image_name ="team_"+team.get_id()+".jpg";
                    iView.buildDrawingCache();
                    bmp = iView.getDrawingCache();
                    MainFunctions.storeImage(image_name,"Team",getContext(), bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e (TAG,"name=" + image_name);
                team.setPhoto(image_name);

                realmDB.commitTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TeamsFragment.newInstance()).commit();
                break;
            default:
                break;
        }

    }

    public void storeImage(String name) throws IOException {
        Bitmap bmp;
//        String target_filename = sd_card.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator + name;
        String target_filename = MainFunctions.getUserImagePath(getActivity().getApplicationContext()) + name;
        Log.d(TAG,target_filename);
        File target_file = new File (target_filename);
        if (!target_file.getParentFile().exists()) {
            if (!target_file.getParentFile().mkdirs()) {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Невозможно создать директорию!", Toast.LENGTH_LONG).show();
                return;
            }
        }
        iView.buildDrawingCache();
        bmp = iView.getDrawingCache();
        FileOutputStream out = new FileOutputStream(target_file);
        if (bmp!=null) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        }
        out.flush();
        out.close();

        realmDB.beginTransaction();
        Number currentIdNum = realmDB.where(LocalFiles.class).max("_id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
        User user = realmDB.where(User.class).equalTo("uuid", authorizedUser.getUuid()).findFirst();
        LocalFiles localFile = realmDB.createObject(LocalFiles.class, nextId);
        localFile.setUser(user);
        localFile.setSent(false);
        localFile.setObject("Stadium");
        localFile.setUuid(java.util.UUID.randomUUID().toString());
        localFile.setFileName(name);
        localFile.setChangedAt(new Date());
        localFile.setCreatedAt(new Date());
        realmDB.commitTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

}
