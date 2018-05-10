package ru.shtrm.gosport.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import io.realm.Realm;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.UserInfoFragmentBinding;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserSport;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class UserInfoFragment extends Fragment {
    private Realm realmDB;

    public static UserInfoFragment newInstance() {
        return (new UserInfoFragment());
    }

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UserInfoFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.user_info_fragment,
                container,false);
        View view = binding.getRoot();

        realmDB = Realm.getDefaultInstance();

        final User user = realmDB.where(User.class).equalTo("uuid",
                AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!",
                    Toast.LENGTH_SHORT).show();
        } else {
            binding.setUser(user);
        }

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Игрок");

		initView(view);

		view.setFocusableInTouchMode(true);
		view.requestFocus();

		return view;
	}

	private void initView(View view) {
        ImageView user_image;
        FloatingActionButton edit_image;
        ImageView call_image;

        TextView tv_hockey_amplua, tv_hockey_level, tv_hockey_team;
        TextView tv_football_amplua, tv_football_level, tv_football_team;

        tv_hockey_amplua = (TextView) view.findViewById(R.id.profile_hockey_amplua);
        tv_hockey_level = (TextView) view.findViewById(R.id.profile_hockey_level);
        tv_hockey_team = (TextView) view.findViewById(R.id.profile_hockey_team);

        tv_football_amplua = (TextView) view.findViewById(R.id.profile_football_amplua);
        tv_football_level = (TextView) view.findViewById(R.id.profile_football_level);
        tv_football_team = (TextView) view.findViewById(R.id.profile_football_team);

        user_image = (ImageView) view.findViewById(R.id.user_image);
        call_image = (ImageView) view.findViewById(R.id.user_phone_icon);

        edit_image = (FloatingActionButton) view.findViewById(R.id.user_edit_image);
        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = realmDB.where(User.class).equalTo("uuid",
                        AuthorizedUser.getInstance().getUuid()).findFirst();
                if (user == null) {
                    getActivity().getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, FragmentAddUser.newInstance()).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, FragmentEditUser.newInstance("")).commit();
                }
            }
        });

        final User user = realmDB.where(User.class).equalTo("uuid",
                AuthorizedUser.getInstance().getUuid()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
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
            String path = MainFunctions.getPicturesDirectory(getContext());
            Bitmap user_bitmap = getResizedBitmap(path, user.getImage(),
                    0, 600, user.getChangedAt().getTime());
            if (user_bitmap != null) {
                user_image.setImageBitmap(user_bitmap);
            }

            Sport hockey,football;
            hockey = realmDB.where(Sport.class).
                    equalTo("name","Хоккей").findFirst();
            football = realmDB.where(Sport.class).
                    equalTo("name","Футбол").findFirst();

            if (hockey!=null) {
                UserSport userSportHockey = realmDB.where(UserSport.class).
                        equalTo("user.uuid", user.getUuid()).
                        equalTo("sport.uuid", hockey.getUuid()).
                        findFirst();
                if (userSportHockey != null) {
                    tv_hockey_amplua.setText(getResources().getString(R.string.amplua,
                            userSportHockey.getAmplua().getName()));
                    tv_hockey_level.setText(getResources().getString(R.string.level,
                            userSportHockey.getLevel().getTitle()));
                    if (userSportHockey.getTeam() != null)
                        tv_hockey_team.setText(getResources().getString(R.string.team,
                                userSportHockey.getTeam().getTitle()));
                }
                else {
                    tv_hockey_amplua.setText("не выбрано");
                }
            }

            if (football!=null) {
                UserSport userSportFootball = realmDB.where(UserSport.class).
                        equalTo("user.uuid", user.getUuid()).
                        equalTo("sport.uuid", football.getUuid()).
                        findFirst();
                if (userSportFootball != null) {
                    tv_football_amplua.setText(getResources().
                            getString(R.string.amplua, userSportFootball.getAmplua().getName()));
                    tv_football_level.setText(getResources().
                            getString(R.string.level, userSportFootball.getLevel().getTitle()));
                    if (userSportFootball.getTeam() != null)
                        tv_football_team.setText(getResources().
                                getString(R.string.team, userSportFootball.getTeam().getTitle()));
                    else
                        tv_football_team.setText(getResources().
                                getString(R.string.team, getString(R.string.not_selected)));
                }
                else {
                    //tv_football_amplua.setText("не выбрано");
                    tv_football_team.setText(getResources().
                            getString(R.string.team, getString(R.string.not_selected)));
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
