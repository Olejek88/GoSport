package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentAddTeam extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_TEAM = 1;
    Spinner typeSpinner, levelSpinner;
    SportAdapter sportAdapter;
    LevelAdapter levelAdapter;
    private static final String TAG = "FragmentAdd";
    private ImageView iView;
    private Bitmap teamBitmap = null;
    private EditText title, description;
    private Sport sport;
    private Realm realmDB;

    public FragmentAddTeam() {
        // Required empty public constructor
    }

    public static FragmentAddTeam newInstance() {
        return (new FragmentAddTeam());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_team, container, false);
        realmDB = Realm.getDefaultInstance();
        iView = (ImageView) view.findViewById(R.id.team_add_image);
        iView.setOnClickListener(this); // calling onClick() method
        Button one = (Button) view.findViewById(R.id.team_button_submit);
        one.setOnClickListener(this); // calling onClick() method
        typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        levelSpinner = (Spinner) view.findViewById(R.id.profile_hockey_level);
        title = (EditText) view.findViewById(R.id.team_add_title);
        description = (EditText) view.findViewById(R.id.team_add_description);

        RealmResults<Sport> sport;
        sport = realmDB.where(Sport.class).findAll();

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.simple_spinner);
        sportAdapter = new SportAdapter(getActivity().getApplicationContext(), sport);
        typeSpinner.setAdapter(sportAdapter);

        Sport hockey = realmDB.where(Sport.class).equalTo("name","Хоккей").findFirst();
        RealmResults<Level> level;
        level = realmDB.where(Level.class).findAll();
        Spinner levelSpinner = (Spinner) view.findViewById(R.id.profile_hockey_level);
        levelAdapter = new LevelAdapter(getActivity().getApplicationContext(), level, hockey);
        levelSpinner.setAdapter(levelAdapter);

        return view;
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_TEAM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_TEAM && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData()==null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getActivity().getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                teamBitmap = BitmapFactory.decodeStream(inputStream);
                if (teamBitmap!=null) {
                    int width= (int) (300*(float)(teamBitmap.getWidth()/teamBitmap.getHeight()));
                    if (width>0) {
                        Bitmap myBitmap2 = Bitmap.createScaledBitmap(teamBitmap, width, 300, false);
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

            case R.id.team_add_image:
                // do your code
                pickImage();
                break;

            case R.id.team_button_submit:
                Team team_c = realmDB.where(Team.class).equalTo("name", title.getText().
                        toString()).findFirst();
                String image_name;
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
                String uuid = java.util.UUID.randomUUID().toString();
                if (teamBitmap!=null)
                    image_name = uuid + ".jpg";
                else
                    image_name = null;
                Log.e(TAG, "name=" + image_name);

                Team team = realmDB.createObject(Team.class, MainActivity.NO_SYNC);
                team.setTitle(title.getText().toString());
                team.setSport(sportAdapter.getItem(typeSpinner.getSelectedItemPosition()));
                team.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
                team.setDescription(description.getText().toString());
                team.setChangedAt(new Date());
                team.setCreatedAt(new Date());
                team.setUuid(uuid);
                if (image_name!=null)
                    team.setPhoto(image_name);
                realmDB.commitTransaction();

                if (teamBitmap!=null)
                    MainFunctions.storeNewImage(teamBitmap, getContext(), 1024, image_name);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, TeamsFragment.newInstance()).commit();
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
}
