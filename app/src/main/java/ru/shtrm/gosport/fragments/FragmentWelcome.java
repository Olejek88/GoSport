package ru.shtrm.gosport.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.shtrm.gosport.R;

public class FragmentWelcome extends Fragment {

    public FragmentWelcome() {
        // Required empty public constructor
    }

    public static FragmentWelcome newInstance() {
        //String gTitle = title;
        return (new FragmentWelcome());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }
}
