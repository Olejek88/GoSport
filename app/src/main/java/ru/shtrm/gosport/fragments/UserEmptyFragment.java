package ru.shtrm.gosport.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.shtrm.gosport.R;

public class UserEmptyFragment extends Fragment {

    public static UserEmptyFragment newInstance() {
        return (new UserEmptyFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_empty_fragment, container, false);
        FloatingActionButton add_user = view.findViewById(R.id.user_edit_image);
        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().
                        replace(R.id.frame_container, FragmentAddUser.newInstance()).commit();
            }
        });
        return view;
    }
}

