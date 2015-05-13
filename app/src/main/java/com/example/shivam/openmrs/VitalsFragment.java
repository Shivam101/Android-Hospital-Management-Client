package com.example.shivam.openmrs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Shivam on 13/05/15 at 12:47 PM.
 */
public class VitalsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_vitals, null);
        return root;

    }

    public VitalsFragment newInstance(){
        VitalsFragment mFragment = new VitalsFragment();
        return mFragment;
    }

}
