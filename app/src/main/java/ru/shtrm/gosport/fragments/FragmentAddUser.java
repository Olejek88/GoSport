package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import ru.shtrm.gosport.MainActivity;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.utils.MainFunctions;

public class FragmentAddUser extends Fragment implements View.OnClickListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    Spinner typeSpinner;
    private static final String TAG = "FragmentAdd";
    private ImageView iView;
    private EditText name, phone, vk;
    private TextView birthDate;
    private Realm realmDB;
    private Bitmap userBitmap = null;
    int selectedYear,selectedDay,selectedMonth;

    public FragmentAddUser() {
        // Required empty public constructor
    }

    public static FragmentAddUser newInstance() {
        return (new FragmentAddUser());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        realmDB = Realm.getDefaultInstance();

        realmDB.beginTransaction();
        User user = realmDB.where(User.class).findFirst();
        if (user!=null)
            user.deleteFromRealm();
        realmDB.commitTransaction();

        iView = (ImageView) view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this);
        Button one = (Button) view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this);

        typeSpinner = (Spinner) view.findViewById(R.id.profile_add_type);

        name = (EditText) view.findViewById(R.id.profile_add_name);
        birthDate = (TextView) view.findViewById(R.id.profile_add_birth);
        phone = (EditText) view.findViewById(R.id.profile_add_phone);
        vk = (EditText) view.findViewById(R.id.profile_add_vk);

        Spinner typeSpinner = (Spinner) view.findViewById(R.id.profile_add_type);
        ArrayAdapter<String> typeSpinnerAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.profile_type));
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeSpinnerAdapter);

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog();
            }
        });

        return view;
    }

    public void startDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(), dateCallBack, year, month, day);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener dateCallBack = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            selectedYear=year;
            selectedMonth=monthOfYear;
            selectedDay=dayOfMonth;
            birthDate.setText(getResources().getString(R.string.formatted_date,
                    selectedDay, selectedMonth+1,selectedYear));
        }
    };

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getActivity().getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                userBitmap = BitmapFactory.decodeStream(inputStream);
                if (userBitmap!=null) {
                    int width= (int) (300*(float)(userBitmap.getWidth()/userBitmap.getHeight()));
                    if (width>0) {
                        Bitmap myBitmap2 = Bitmap.createScaledBitmap(userBitmap, width, 300, false);
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
                User user = realmDB.where(User.class).equalTo("name",
                        name.getText().toString()).findFirst();
                String image_name;
                if (user!=null) {
                     Toast.makeText(getActivity().getApplicationContext(),
                            "Пользователь с логином " + user.getLogin() +
                                    " уже есть на этом устройстве", Toast.LENGTH_LONG).show();
                     break;
                    }
                if (name.getText().length()<2 ||
                        phone.getText().length()<8 || selectedYear<100)
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Вы должны заполнить все поля!", Toast.LENGTH_LONG).show();
                        break;
                    }

                realmDB.beginTransaction();
                Number currentIdNum = realmDB.where(User.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                String uuid = java.util.UUID.randomUUID().toString();

                User profile = realmDB.createObject(User.class,nextId);
                profile.setName(name.getText().toString());
                profile.set_id(MainActivity.NO_SYNC);
                profile.setBirthDate(new Date(selectedYear-1900,selectedMonth,selectedDay));
                profile.setPhone(phone.getText().toString());
                profile.setVk(MainFunctions.getVkProfile(vk.getText().toString()));
                profile.setType(typeSpinner.getSelectedItemPosition());
                profile.setChangedAt(new Date());
                profile.setCreatedAt(new Date());
                profile.setActive(true);
                profile.setUuid(uuid);
                image_name = uuid + ".jpg";
                Log.e(TAG, "name=" + image_name);
                profile.setImage(image_name);
                realmDB.commitTransaction();

                if (userBitmap!=null)
                    MainFunctions.storeNewImage(userBitmap, getContext(), 1024, image_name);

                user = realmDB.where(User.class).equalTo("name", name.getText().toString()).findFirst();
                if (user!=null && user.get_id()!=null && user.get_id().length()>0)
                    {
                       ((MainActivity)getActivity()).addProfile(profile);
                        ((MainActivity)getActivity()).refreshProfileList();
                       Fragment f = UserInfoFragment.newInstance();
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
