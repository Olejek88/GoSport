package ru.toir.mobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import ru.toir.mobile.MainActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.User;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

public class FragmentEditUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private ImageView iView;
    private EditText name,login,pass;
    private String image_name;
    private Realm realmDB;

    public FragmentEditUser() {
        // Required empty public constructor
    }

    public static FragmentEditUser newInstance(String title) {
        return (new FragmentEditUser());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        realmDB = Realm.getDefaultInstance();
        iView = (ImageView) view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this); // calling onClick() method
        Button one = (Button) view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this); // calling onClick() method
        Button delete = (Button) view.findViewById(R.id.profile_button_delete);
        delete.setOnClickListener(this); // calling onClick() method

        name = (EditText) view.findViewById(R.id.profile_add_name);
        login = (EditText) view.findViewById(R.id.profile_add_login);
        pass = (EditText) view.findViewById(R.id.profile_add_password);
        //login.setEnabled(false);

        User user = realmDB.where(User.class).equalTo("active", true).findFirst();
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getActivity().getApplicationContext()));
        //user = users.getActiveUser();
        if (user==null) {
            Toast.makeText(getActivity().getApplicationContext(), "Пользователь не выбран, пожалуйста выберите или содайте профиль", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
        }

        if (user!=null) {
            pass.setText(user.getPass());
            login.setText(user.getLogin());
            name.setText(user.getName());
            image_name = user.getImage();

            String path = getActivity().getExternalFilesDir("/users") + File.separator;
            if (user.getChangedAt()!=null) {
                Bitmap myBitmap = getResizedBitmap(path, user.getImage(), 0, 600, user.getChangedAt().getTime());
                if (myBitmap!=null) {
                    iView.setImageBitmap(myBitmap);
                }
            }
        }
        return view;
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                if (myBitmap!=null) {
                    int height = (int) (200 * (float) myBitmap.getHeight() / (float) myBitmap.getWidth());
                    if (height > 0) {
                        Bitmap myBitmap2 = Bitmap.createScaledBitmap(myBitmap, 200, height, false);
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
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getActivity().getApplicationContext()));

        switch (v.getId()) {

            case R.id.profile_add_image:
                // do your code
                pickImage();
                break;

            case R.id.profile_button_submit:
                if (name.getText().toString().length()<2 || login.getText().toString().length()<2 || pass.getText().toString().length()<2)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                try {
                    // название файла аватара не меняется
                    storeImage(image_name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                User user = realmDB.where(User.class).equalTo("active", true).findFirst();
                realmDB.beginTransaction();
                user.setName(name.getText().toString());
                user.setLogin(login.getText().toString());
                user.setPass(pass.getText().toString());
                realmDB.commitTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
                break;

            case R.id.profile_button_delete:
                user = realmDB.where(User.class).equalTo("active", true).findFirst();
                ((MainActivity)getActivity()).deleteProfile((int) user.get_id());
                realmDB.beginTransaction();
                user.deleteFromRealm();
                realmDB.commitTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentWelcome.newInstance()).commit();
                break;

            default:
                break;
        }

    }

    public void storeImage(String name) throws IOException {
        Bitmap bmp;
        File sd_card = Environment.getExternalStorageDirectory();
        String target_filename = sd_card.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getPackageName() + File.separator + "img" + File.separator + name;
        File target_file = new File (target_filename);
        if (!target_file.getParentFile().exists()) {
            target_file.getParentFile().mkdirs();
        }

        iView.buildDrawingCache();
        bmp = iView.getDrawingCache();
        FileOutputStream out = new FileOutputStream(target_file);
        if (bmp!=null) {
            //Bitmap.createScaledBitmap(bmp, 100, 100, false);
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
