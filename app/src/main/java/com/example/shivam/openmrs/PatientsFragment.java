package com.example.shivam.openmrs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Shivam on 13/05/15 at 12:44 PM.
 */
public class PatientsFragment extends Fragment {

    FloatingActionButton addPatient;
    ListView patientList;
    SwipeRefreshLayout mRefresh;
    List<ParseObject> patients;
    PatientAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_patients, null);
        addPatient = (FloatingActionButton)root.findViewById(R.id.fab1);
        mRefresh = (SwipeRefreshLayout)root.findViewById(R.id.listRefresh);
        mRefresh.setColorSchemeResources(R.color.refresh_blue,R.color.refresh_red,R.color.refresh_green,R.color.refresh_yellow);
        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),AddPatientActivity.class);
                startActivity(i);
            }
        });
        patientList = (ListView)root.findViewById(R.id.patientList);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        refreshList();

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList()
    {
        ParseQuery<ParseObject> noteQuery = new ParseQuery<ParseObject>("Patient");
        noteQuery.addDescendingOrder("createdAt");
        noteQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
//                getActivity().setProgressBarIndeterminateVisibility(false);
                if (mRefresh.isRefreshing()) {
                    mRefresh.setRefreshing(false);
                }
                if (e == null) {
                    patients = parseObjects;
                    //String[] usernames = new String[notes.size()];
                    //int i = 0;
                    if (patientList.getAdapter() == null) {
                        mAdapter = new PatientAdapter(getActivity(), R.layout.patient_list_item, patients);
                        patientList.setAdapter(mAdapter);
                    } else {
                        mAdapter.refreshAdapter(patients);
                    }

                }
            }
        });
    }


    public PatientsFragment newInstance(){
        PatientsFragment mFragment = new PatientsFragment();
        return mFragment;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_patients, menu);
        return true;
    }


}
