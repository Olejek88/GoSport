package ru.shtrm.gosport.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import ru.shtrm.gosport.AuthorizedUser;
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
    private Context mainActivityConnector = null;
    int selectedYear,selectedDay,selectedMonth;

    public FragmentAddUser() {
        // Required empty public constructor
    }

    public static FragmentAddUser newInstance() {
        return (new FragmentAddUser());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        realmDB = Realm.getDefaultInstance();

        iView = view.findViewById(R.id.profile_add_image);
        iView.setOnClickListener(this);
        Button one = view.findViewById(R.id.profile_button_submit);
        one.setOnClickListener(this);

        typeSpinner = view.findViewById(R.id.profile_add_type);
        name = view.findViewById(R.id.profile_add_name);
        birthDate = view.findViewById(R.id.profile_add_birth);
        phone = view.findViewById(R.id.profile_add_phone);
        vk = view.findViewById(R.id.profile_add_vk);

        ArrayAdapter<String> typeSpinnerAdapter =
                new ArrayAdapter<>(mainActivityConnector, android.R.layout.simple_spinner_dropdown_item,
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
                new DatePickerDialog(mainActivityConnector, dateCallBack, year, month, day);
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
                InputStream inputStream = mainActivityConnector.getApplicationContext()
                        .getContentResolver().openInputStream(data.getData());
                userBitmap = BitmapFactory.decodeStream(inputStream);
                if (userBitmap!=null) {
                    int width= (int) (300*(float)(userBitmap.getWidth()/userBitmap.getHeight()));
                    if (width>0) {
                        Bitmap myBitmap2 = Bitmap.
                                createScaledBitmap(userBitmap, width, 300, false);
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
                     Toast.makeText(mainActivityConnector.getApplicationContext(),
                            "Пользователь с логином " + user.getLogin() +
                                    " уже есть на этом устройстве", Toast.LENGTH_LONG).show();
                     break;
                    }
                if (name.getText().length()<2 ||
                        phone.getText().length()<8 || selectedYear<100)
                    {
                        Toast.makeText(mainActivityConnector.getApplicationContext(),
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

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, selectedYear);
                cal.set(Calendar.MONTH, selectedMonth);
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);

                User profile = realmDB.createObject(User.class,nextId);
                profile.setName(name.getText().toString());
                profile.set_id(MainActivity.NO_SYNC);
                profile.setBirthDate(cal.getTime());
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
                       ((MainActivity)mainActivityConnector).addProfile(profile);
                        ((MainActivity)mainActivityConnector).refreshProfileList();
                        AuthorizedUser.getInstance().setUuid(user.getUuid());
                        Fragment f = UserInfoFragment.newInstance();
                        ((MainActivity)mainActivityConnector).getSupportFragmentManager().
                               beginTransaction().replace(R.id.frame_container, f).commit();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityConnector = getActivity();
        // TODO решить что делать если контекст не приехал
        if (mainActivityConnector==null)
            onDestroyView();
    }
}
