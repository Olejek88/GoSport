package ru.shtrm.gosport.fragments;

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
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.User;

public class FragmentAddUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    Spinner typeSpinner;
    Spinner whoSpinner;
    private ImageView iView;
    private EditText name, login, pass, tag_id;
    private Realm realmDB;

    public FragmentAddUser() {
        // Required empty public constructor
    }

    public static FragmentAddUser newInstance() {
        return (new FragmentAddUser());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addprofile, container, false);
        realmDB = Realm.getDefaultInstance();
        iView = (ImageView) view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this); // calling onClick() method
        Button one = (Button) view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this); // calling onClick() method

        typeSpinner = (Spinner) view.findViewById(R.id.profile_add_type);
        whoSpinner = (Spinner) view.findViewById(R.id.profile_add_whois);

        //ArrayAdapter<String> langSpinnerAdapter = new ArrayAdapter<>(getContext(),
        //        android.R.layout.simple_spinner_dropdown_item,
        //        new ArrayList<String>());
        //typeSpinner.setAdapter(langSpinnerAdapter);
        //whoSpinner.setAdapter(langSpinnerAdapter);

        name = (EditText) view.findViewById(R.id.profile_add_name);
        login = (EditText) view.findViewById(R.id.profile_add_login);
        pass = (EditText) view.findViewById(R.id.profile_add_password);
        tag_id = (EditText) view.findViewById(R.id.profile_add_tagid);
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
                    //int height= (int) ((int)200*(float)((float)myBitmap.getHeight()/(float)myBitmap.getWidth()));
                    int height= (int) (200*(float)(myBitmap.getHeight()/myBitmap.getWidth()));
                    if (height>0) {
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
        switch (v.getId()) {

            case R.id.profile_add_image:
                // do your code
                pickImage();
                break;

            case R.id.profile_button_submit:
                //UsersDBAdapter users = new UsersDBAdapter(
                //        new ToirDatabaseContext(getActivity().getApplicationContext()));
                //Users profile;
                User user = realmDB.where(User.class).equalTo("tagId", tag_id.getText().toString()).findFirst();
                String image_name = "profile";
                if (user!=null) {
                     Toast.makeText(getActivity().getApplicationContext(),
                            "Пользователь с логином " + user.getLogin() + " уже есть на этом устройстве", Toast.LENGTH_LONG).show();
                     break;
                    }
                if (name.getText().toString().length()<2 || login.getText().toString().length()<2 || tag_id.getText().toString().length()<2)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                realmDB.beginTransaction();
                User profile = realmDB.createObject(User.class);
                profile.setName(name.getText().toString());
                profile.setImage(image_name);
                profile.setLogin(login.getText().toString());
                profile.setPass(pass.getText().toString());
                profile.setType(typeSpinner.getSelectedItemPosition());
                profile.setUuid(java.util.UUID.randomUUID().toString());
                try {
                    image_name ="profile"+profile.get_id()+".jpg";
                    storeImage(image_name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile.setImage(image_name);
                realmDB.commitTransaction();

                //users.replaceItem(profile.getUuid(),profile.getName(),profile.getLogin(),profile.getPass(),profile.getType(),profile.getTag_id(),true,profile.getWhois(),profile.getImage(),false);
                //TODO проверка, нужна ли
                user = realmDB.where(User.class).equalTo("tagId", tag_id.getText().toString()).findFirst();
                if (user.get_id()>0)
                    {
                       ((MainActivity)getActivity()).addProfile(profile);
                        ((MainActivity)getActivity()).refreshProfileList();
                       Fragment f = FragmentWelcome.newInstance();
                       getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, f).commit();
                    }
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
