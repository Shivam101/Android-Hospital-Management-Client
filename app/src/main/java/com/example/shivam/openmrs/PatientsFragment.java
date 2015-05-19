package com.example.shivam.openmrs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Shivam on 13/05/15 at 12:44 PM.
 */
public class PatientsFragment extends Fragment {

    FloatingActionButton addPatient;
    ListView patientList;
    SwipeRefreshLayout mRefresh;
    List<ParseObject> patients;
    private ParseQueryAdapter<Patient> patientsAdapter;
    PatientAdapter mAdapter;


    LayoutInflater inflater = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_patients, null);
        addPatient = (FloatingActionButton)root.findViewById(R.id.fab1);
        mRefresh = (SwipeRefreshLayout)root.findViewById(R.id.listRefresh);
        mRefresh.setColorSchemeResources(R.color.refresh_blue, R.color.refresh_red, R.color.refresh_green, R.color.refresh_yellow);
        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),AddPatientActivity.class);
                startActivity(i);
            }
        });
        patientList = (ListView)root.findViewById(R.id.patientList);

        ParseQueryAdapter.QueryFactory<Patient> factory = new ParseQueryAdapter.QueryFactory<Patient>() {
            public ParseQuery<Patient> create() {
                ParseQuery<Patient> query = Patient.getQuery();
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };
        refreshList();

        //patientsAdapter = new PatientsAdapter(getActivity(), factory);
        patientList.setFastScrollEnabled(true);
        //patientList.setAdapter(patientsAdapter);
        //patientList.setAdapter(mAdapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                patientList.setAdapter(mAdapter);
                refreshList();
            }
        });
        //syncToParse();

        return root;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        //syncToParse();
        refreshList();
    }

    public void refreshList()
    {
        ParseQuery<ParseObject> noteQuery = new ParseQuery<ParseObject>("Patient");
        noteQuery.addAscendingOrder("patientName");
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

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings)
        {
            ParseUser.logOut();
            Intent i = new Intent(getActivity(), SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else if(id==R.id.action_sync)
        {

            syncToParse();
        }
        return super.onOptionsItemSelected(item);

    }

    public void syncToParse() {
        ParseQuery<Patient> query = Patient.getQuery();
        query.fromPin("PatientGroup");
        query.whereEqualTo("isDraft", true);
        query.findInBackground(new FindCallback<Patient>() {
            @Override
            public void done(List<Patient> list, ParseException e) {
                if(e==null)
                {
                    for (final Patient p : list) {
                        // Set is draft flag to false before
                        // syncing to Parse
                        p.setDraft(false);
                        Dialog progressDialog;
                        //getActivity().progressDialog = ProgressDialog.show(getActivity(), "", "Saving Patient Details...", true);
                        p.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null)
                                {
                                    patientsAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    p.setDraft(true);
                                }
                            }
                        });

                    }

                    }
            }
        });
    }

    private class PatientsAdapter extends ParseQueryAdapter<Patient> {

        public PatientsAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<Patient> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(Patient p, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.patient_list_item, parent, false);
                holder = new ViewHolder();
                holder.patientName = (TextView) view.findViewById(R.id.patientName);
                holder.patientDetails = (TextView)view.findViewById(R.id.patientDetails);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView listTitle = holder.patientName;
            TextView listContent = holder.patientDetails;
            listTitle.setText(p.getName());
            listContent.setText(p.getDetails());
            if (p.isDraft()) {
                //listTitle.setTypeface(null, Typeface.ITALIC);
                listTitle.setTextColor(getResources().getColor(R.color.accentColor));
            } else {
                listTitle.setTypeface(null, Typeface.NORMAL);
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView patientName,patientDetails;
    }



}
