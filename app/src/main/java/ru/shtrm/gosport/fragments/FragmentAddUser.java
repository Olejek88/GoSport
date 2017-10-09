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
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.SortField;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentAddUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    Spinner typeSpinner;
    Spinner whoSpinner;
    private static final String TAG = "FragmentAdd";
    private ImageView iView;
    private EditText name, age, phone, vk;
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

        name = (EditText) view.findViewById(R.id.profile_add_name);
        age = (EditText) view.findViewById(R.id.profile_add_age);
        phone = (EditText) view.findViewById(R.id.profile_add_phone);
        vk = (EditText) view.findViewById(R.id.profile_add_vk);

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.profile_add_type);
        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.profile_type));
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeSpinnerAdapter);

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
                    //int height= (int) (200*(float)(myBitmap.getHeight()/myBitmap.getWidth()));
                    int width= (int) (200*(float)(myBitmap.getWidth()/myBitmap.getHeight()));
                    if (width>0) {
                        Bitmap myBitmap2 = Bitmap.createScaledBitmap(myBitmap, width, 200, false);
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
                User user = realmDB.where(User.class).equalTo("name", name.getText().toString()).findFirst();
                String image_name = "profile";
                Bitmap bmp;
                if (user!=null) {
                     Toast.makeText(getActivity().getApplicationContext(),
                            "Пользователь с логином " + user.getLogin() + " уже есть на этом устройстве", Toast.LENGTH_LONG).show();
                     break;
                    }
                if (name.getText().length()<2 || age.getText().length()<2 || phone.getText().length()<8)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }
                realmDB.beginTransaction();
                Number currentIdNum = realmDB.where(User.class).max("_id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                User profile = realmDB.createObject(User.class,nextId);
                profile.setName(name.getText().toString());
                profile.setImage(image_name);
                profile.setAge(Integer.parseInt(age.getText().toString()));
                profile.setPhone(phone.getText().toString());
                profile.setVK(vk.getText().toString());
                profile.setType(typeSpinner.getSelectedItemPosition());
                profile.setChangedAt(new Date());
                profile.setCreatedAt(new Date());
                profile.setActive(true);
                profile.setUuid(java.util.UUID.randomUUID().toString());
                try {
                    image_name = "profile" + profile.get_id() + ".jpg";
                    iView.buildDrawingCache();
                    bmp = iView.getDrawingCache();
                    MainFunctions.storeImage(image_name, "User", getContext(), bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e (TAG,"name=" + image_name);

                profile.setImage(image_name);
                realmDB.commitTransaction();

                user = realmDB.where(User.class).equalTo("name", name.getText().toString()).findFirst();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
