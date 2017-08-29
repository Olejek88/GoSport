package ru.shtrm.gosport.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;
import ru.shtrm.gosport.AuthorizedUser;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.User;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class UserInfoFragment extends Fragment {
    private Realm realmDB;

    public static UserInfoFragment newInstance() {
        return (new UserInfoFragment());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
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
        ImageView user_image;
        ImageView edit_image;
        ImageView call_image;

		tv_user_name = (TextView) view.findViewById(R.id.user_text_name);
        user_image = (ImageView) view.findViewById(R.id.user_image);
        edit_image = (ImageView) view.findViewById(R.id.user_edit_image);
        call_image = (ImageView) view.findViewById(R.id.user_boss_contact);

        edit_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentEditUser.newInstance("EditProfile")).commit();
            }
        });

		// !!!!!
        final User user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
		//final User user = realmDB.where(User.class).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
            //if (user.getTagId().length() > 20) tv_user_id.setText("ID: " + user.getTagId().substring(4, 24));
            tv_user_id.setText(getString(R.string.id, user.getTagId()));
			tv_user_name.setText(user.getName());
			tv_user_date.setText(DateFormat.getDateTimeInstance().format(new Date()));
			tv_user_boss.setText(user.getContact());
			tv_user_type.setText(user.getWhoIs());

            LocationManager manager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (statusOfGPS && manager.getLastKnownLocation(manager.getBestProvider(new Criteria(), false)) != null) {
                    user_status_gps.setChecked(true);
                    tv_user_gps.setText(String.valueOf(manager.getLastKnownLocation(manager.getBestProvider(new Criteria(), false)).getLongitude()) + ", " + String.valueOf(manager.getLastKnownLocation(manager.getBestProvider(new Criteria(), false)).getLatitude()));
                } else {
                    user_status_gps.setChecked(false);
                    tv_user_gps.setText("не определено");
                }
            }
            ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    user_status_gprs.setChecked(true);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    user_status_gprs.setChecked(false);
                }
            } else {
                user_status_gprs.setChecked(false);
            }

            if (user.getContact() != null) {
                call_image.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent = new Intent(Intent.ACTION_CALL);
                      intent.setData(Uri.parse("tel:" + user.getContact()));
                      startActivity(intent);
                  }
              });
            }

            String path = getActivity().getExternalFilesDir("/users") + File.separator;
            Bitmap user_bitmap = getResizedBitmap(path, user.getImage(), 0, 600, user.getChangedAt().getTime());
            if (user_bitmap != null) {
                user_image.setImageBitmap(user_bitmap);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
