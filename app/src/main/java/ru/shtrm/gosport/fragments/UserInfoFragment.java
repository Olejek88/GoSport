package ru.shtrm.gosport.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.adapters.AmpluaAdapter;
import ru.shtrm.gosport.db.adapters.LevelAdapter;
import ru.shtrm.gosport.db.adapters.TeamAdapter;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserSport;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class UserInfoFragment extends Fragment {
    private Realm realmDB;

    public static UserInfoFragment newInstance() {
        return (new UserInfoFragment());
    }

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.user_layout, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Игрок");

        realmDB = Realm.getDefaultInstance();
		initView(rootView);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView(View view) {
        TextView tv_user_name;
        TextView tv_user_phone;
        TextView tv_user_birth;
        TextView tv_user_vk;
        TextView tv_user_type;

        ImageView user_image;
        ImageView edit_image;
        ImageView call_image;

        TextView tv_hockey_amplua, tv_hockey_level, tv_hockey_team;
        TextView tv_football_amplua, tv_football_level, tv_football_team;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        tv_user_name = (TextView) view.findViewById(R.id.user_text_name);
        tv_user_phone = (TextView) view.findViewById(R.id.user_text_phone);
        tv_user_birth = (TextView) view.findViewById(R.id.user_text_age);
        tv_user_vk = (TextView) view.findViewById(R.id.user_text_vk);
        tv_user_type = (TextView) view.findViewById(R.id.user_text_type);

        tv_hockey_amplua = (TextView) view.findViewById(R.id.profile_hockey_amplua);
        tv_hockey_level = (TextView) view.findViewById(R.id.profile_hockey_level);
        tv_hockey_team = (TextView) view.findViewById(R.id.profile_hockey_team);

        tv_football_amplua = (TextView) view.findViewById(R.id.profile_football_amplua);
        tv_football_level = (TextView) view.findViewById(R.id.profile_football_level);
        tv_football_team = (TextView) view.findViewById(R.id.profile_football_team);

        user_image = (ImageView) view.findViewById(R.id.user_image);
        edit_image = (ImageView) view.findViewById(R.id.user_edit_image);
        call_image = (ImageView) view.findViewById(R.id.user_phone_icon);

        edit_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, FragmentEditUser.newInstance("EditProfile")).commit();
            }
        });

        String uuid = AuthorizedUser.getInstance().getUuid();
        final User user = realmDB.where(User.class).equalTo("uuid",AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
			tv_user_name.setText(user.getName());
            if (user.getBirthDate()!=null)
                tv_user_birth.setText(fmt.format(user.getBirthDate().getTime()));
            else
                tv_user_birth.setText("не указана");
            tv_user_phone.setText(user.getPhone());
            if (user.getType()==1)
                tv_user_type.setText("Игрок");
            else
                tv_user_type.setText("Организатор");
            if (user.getVK().contains("http"))
                tv_user_vk.setText(user.getVK());
            else
                tv_user_vk.setText("https://vk.com/"+user.getVK());

            if (user.getPhone() != null) {
                call_image.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent = new Intent(Intent.ACTION_CALL);
                      intent.setData(Uri.parse("tel:" + user.getPhone()));
                      startActivity(intent);
                  }
              });
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator;
            Bitmap user_bitmap = getResizedBitmap(path, user.getImage(), 0, 600, user.getChangedAt().getTime());
            if (user_bitmap != null) {
                user_image.setImageBitmap(user_bitmap);
            }

            Sport hockey,football;
            hockey = realmDB.where(Sport.class).equalTo("name","Хоккей").findFirst();
            football = realmDB.where(Sport.class).equalTo("name","Футбол").findFirst();

            if (hockey!=null) {
                UserSport userSportHockey = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", hockey.getUuid()).findFirst();
                if (userSportHockey != null) {
                    tv_hockey_amplua.setText(userSportHockey.getAmplua().getName());
                    tv_hockey_level.setText(userSportHockey.getLevel().getTitle());
                    if (userSportHockey.getTeam() != null)
                        tv_hockey_team.setText(userSportHockey.getTeam().getTitle());

                }
                else {
                    tv_hockey_amplua.setText("не выбрано");
                }
            }

            if (football!=null) {
                UserSport userSportFootball = realmDB.where(UserSport.class).equalTo("user.uuid", user.getUuid()).equalTo("sport.uuid", football.getUuid()).findFirst();
                if (userSportFootball != null) {
                    tv_football_amplua.setText(userSportFootball.getAmplua().getName());
                    tv_football_level.setText(userSportFootball.getLevel().getTitle());
                    if (userSportFootball.getTeam() != null)
                        tv_football_team.setText(userSportFootball.getTeam().getTitle());
                }
                else {
                    tv_football_amplua.setText("не выбрано");
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
