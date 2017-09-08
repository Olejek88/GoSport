package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentAddTeam extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_TEAM = 1;
    Spinner typeSpinner, levelSpinner;
    SportAdapter sportAdapter;
    LevelAdapter levelAdapter;
    private static final String TAG = "FragmentAdd";
    private ImageView iView;
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
        View view = inflater.inflate(R.layout.fragment_addteam, container, false);
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

        Sport hockey = realmDB.where(Sport.class).equalTo("title","Хоккей").findFirst();
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
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                if (myBitmap!=null) {
                    int height = (int) (300*((float)myBitmap.getHeight()/(float)myBitmap.getWidth()));
                    if (height>0) {
                        Bitmap myBitmap2 = Bitmap.createScaledBitmap(myBitmap, 300, height, false);
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
                Team team_c = realmDB.where(Team.class).equalTo("title", title.getText().toString()).findFirst();
                String image_name = "profile";
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
                team.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
                team.setDescription(description.getText().toString());
                team.setChangedAt(new Date());
                team.setCreatedAt(new Date());
                team.setUuid(java.util.UUID.randomUUID().toString());
                try {
                    image_name ="team"+team.get_id()+".jpg";
                    storeImage(image_name);
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
            if (!target_file.getParentFile().mkdirs())
                Toast.makeText(getActivity().getApplicationContext(),
                        "Невозможно создать директорию!", Toast.LENGTH_LONG).show();
        }
        iView.buildDrawingCache();
        bmp = iView.getDrawingCache();
        FileOutputStream out = new FileOutputStream(target_file);
        if (bmp!=null) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        }
        out.flush();
        out.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
