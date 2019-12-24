package ru.shtrm.gosport.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.SportAdapter;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.model.AuthorizedUser;
import ru.shtrm.gosport.ui.MainActivity;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class FragmentAddTeam extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_TEAM = 1;
    private String teamUuid = null;
    private Context mainActivityConnector = null;
    Spinner sportSpinner, levelSpinner;
    SportAdapter sportAdapter;
    LevelAdapter levelAdapter;
    private ImageView iView;
    private Bitmap teamBitmap = null;
    private EditText title, description;
    private Realm realmDB;

    public FragmentAddTeam() {
        // Required empty public constructor
    }

    public static FragmentAddTeam newInstance() {
        return (new FragmentAddTeam());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_team, container, false);
        realmDB = Realm.getDefaultInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            teamUuid = bundle.getString("uuid", "");
        }

        iView = view.findViewById(R.id.team_add_image);
        iView.setOnClickListener(this);
        Button one = view.findViewById(R.id.team_button_submit);
        one.setOnClickListener(this);

        sportSpinner = view.findViewById(R.id.simple_spinner);
        levelSpinner = view.findViewById(R.id.profile_hockey_level);
        title = view.findViewById(R.id.team_add_title);
        description = view.findViewById(R.id.team_add_description);

        RealmResults<Sport> sport = realmDB.where(Sport.class).findAll();
        sportAdapter = new SportAdapter(mainActivityConnector.getApplicationContext(), sport);
        sportSpinner.setAdapter(sportAdapter);

        Sport hockey = realmDB.where(Sport.class).equalTo("name","Хоккей").findFirst();
        RealmResults<Level> level = realmDB.where(Level.class).findAll();
        levelAdapter = new LevelAdapter(mainActivityConnector.getApplicationContext(), level, hockey);
        levelSpinner.setAdapter(levelAdapter);

        if (teamUuid!=null) {
            Team team = realmDB.where(Team.class).equalTo("uuid", teamUuid).findFirst();
            if (team != null) {
                title.setText(team.getTitle());
                description.setText(team.getDescription());
                // TODO разобраться с binding на imageview
                String path = MainFunctions.getPicturesDirectory(mainActivityConnector);
                if (team.getPhoto()!=null) {
                    Bitmap team_bitmap = getResizedBitmap(path, team.getPhoto(),
                            0, 600, team.getChangedAt().getTime());
                    if (team_bitmap != null) {
                        iView.setImageBitmap(team_bitmap);
                    }
                }
                for (int r = 0; r < sport.size(); r++) {
                    if (team.getSport()!=null &&
                            team.getSport().getUuid().equals(sport.get(r).getUuid()))
                        sportSpinner.setSelection(r);
                }
                for (int r = 0; r < level.size(); r++) {
                    if (team.getLevel()!=null &&
                            team.getLevel().getUuid().equals(level.get(r).getUuid()))
                        levelSpinner.setSelection(r);
                }
            }
        }

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
                InputStream inputStream = mainActivityConnector.getApplicationContext()
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
                pickImage();
                break;

            case R.id.team_button_submit:
                String image_name=null;
                final User user = realmDB.where(User.class).
                        equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
                if (teamUuid!=null) {
                    Team team = realmDB.where(Team.class).
                            equalTo("uuid", teamUuid).findFirst();
                    if (team != null) {
                        if (team.getUser()!=null && team.getUser()!=user) {
                            Toast.makeText(mainActivityConnector.getApplicationContext(),
                                    "Вы не имеете права изменить эту команду. " +
                                            "Если в описании присутствует неточность - " +
                                            "сообщите администратору через форму на сайте.",
                                    Toast.LENGTH_LONG).show();
                            break;
                        }
                        if (teamBitmap != null) {
                            image_name = team.getUuid() + ".jpg";
                            MainFunctions.storeNewImage(teamBitmap, getContext(), 1024,
                                    image_name);
                        }

                        realmDB.beginTransaction();
                        team.setTitle(title.getText().toString());
                        if (image_name!=null)
                            team.setPhoto(image_name);
                        team.setUser(user);
                        team.setDescription(description.getText().toString());
                        team.setSport(sportAdapter.getItem(sportSpinner.getSelectedItemPosition()));
                        team.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
                        team.setChangedAt(new Date());
                        realmDB.commitTransaction();
                    }
                }
                else {
                    Team team_c = realmDB.where(Team.class).equalTo("name",
                            title.getText().toString()).findFirst();
                    if (team_c != null) {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Такая команда уже есть", Toast.LENGTH_LONG).show();
                        break;
                    }
                    if (title.getText().length() < 3) {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    String uuid = java.util.UUID.randomUUID().toString();
                    if (teamBitmap != null)
                        image_name = uuid + ".jpg";
                    else
                        image_name = null;
                    if (teamBitmap != null)
                        MainFunctions.storeNewImage(teamBitmap, getContext(), 1024, image_name);

                    realmDB.beginTransaction();
                    Team team = realmDB.createObject(Team.class, uuid);
                    team.setTitle(title.getText().toString());
                    if (image_name!=null)
                        team.setPhoto(image_name);
                    team.setUser(user);
                    team.setSport(sportAdapter.getItem(sportSpinner.getSelectedItemPosition()));
                    team.setLevel(levelAdapter.getItem(levelSpinner.getSelectedItemPosition()));
                    team.setDescription(description.getText().toString());
                    team.setChangedAt(new Date());
                    team.setCreatedAt(new Date());
                    team.setUuid(uuid);
                    if (image_name != null)
                        team.setPhoto(image_name);
                    realmDB.commitTransaction();
                }
                ((MainActivity)mainActivityConnector).getSupportFragmentManager().
                        beginTransaction().replace(R.id.frame_container, TeamsFragment.newInstance()).commit();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityConnector = getActivity();
        // TODO решить что делать если контекст не приехал
        if (mainActivityConnector==null)
            onDestroyView();
    }

}
