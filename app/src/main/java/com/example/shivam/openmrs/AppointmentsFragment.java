package com.example.shivam.openmrs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Shivam on 13/05/15 at 12:50 PM.
 */
public class AppointmentsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_appointments, null);
        return root;

    }

    public AppointmentsFragment newInstance(){
        AppointmentsFragment mFragment = new AppointmentsFragment();
        return mFragment;
    }

}
