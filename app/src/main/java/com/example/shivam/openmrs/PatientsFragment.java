package com.example.shivam.openmrs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.commons.lang3.text.WordUtils;

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
    SearchView mSearchView;
    ProgressDialog dialog;

    LayoutInflater inflater = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_patients, null);
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
        patientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                RelativeLayout linearLayoutParent = (RelativeLayout) container;
//                RelativeLayout linearLayoutChild = (RelativeLayout ) linearLayoutParent.getChildAt(1);
//                TextView tvCountry = (TextView) linearLayoutChild.getChildAt(0);
                TextView tv = (TextView)patientList.getChildAt(position).findViewById(R.id.patientName);
                String name = tv.getText().toString();
                //Toast.makeText(getActivity(),tv.getText().toString(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(),PatientDetailsActivity.class);
                i.putExtra("PATIENT_NAME",name);
                startActivity(i);
                //ParseQuery<>
            }
        });

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
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search for patients");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchView.setQuery("", false);
                mSearchView.clearFocus();
                mSearchView.setIconified(true);
                s = s.trim();

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Patient");
                query.whereContains("patientName", WordUtils.capitalize(s));
                dialog = ProgressDialog.show(getActivity(), "Looking for Patients...", "Please wait...", true);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e==null)
                        {

                            dialog.dismiss();
                            mAdapter = new PatientAdapter(getActivity(), R.layout.patient_list_item, list);
                                patientList.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            //show new list
                        }
                        else
                        {
                            dialog.dismiss();
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                            builder.content("Couldn't find any patients :( Try again later.");
                            builder.title("Oops !");
                            builder.positiveText(android.R.string.ok);
                            builder.show();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
        else if(id==R.id.action_sort_age)
        {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Patient");
            query.addAscendingOrder("patientAge");
            dialog = ProgressDialog.show(getActivity(), "Sorting Patients...", "Please wait...", true);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        dialog.dismiss();
                        mAdapter = new PatientAdapter(getActivity(), R.layout.patient_list_item, list);
                        patientList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        //show new list
                    } else {
                        dialog.dismiss();
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                        builder.content("Couldn't find any patients :( Try again later.");
                        builder.title("Oops !");
                        builder.positiveText(android.R.string.ok);
                        builder.show();
                    }
                }
            });
        }
        else if(id==R.id.action_sort_height)
        {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Patient");
            query.addAscendingOrder("patientHeight");
            dialog = ProgressDialog.show(getActivity(), "Sorting Patients...", "Please wait...", true);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        dialog.dismiss();
                        mAdapter = new PatientAdapter(getActivity(), R.layout.patient_list_item, list);
                        patientList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        //show new list
                    } else {
                        dialog.dismiss();
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                        builder.content("Couldn't find any patients :( Try again later.");
                        builder.title("Oops !");
                        builder.positiveText(android.R.string.ok);
                        builder.show();
                    }
                }
            });
        }
        else if(id==R.id.action_sort_date)
        {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Patient");
            query.addDescendingOrder("patientDate");
            dialog = ProgressDialog.show(getActivity(), "Sorting Patients...", "Please wait...", true);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        dialog.dismiss();
                        mAdapter = new PatientAdapter(getActivity(), R.layout.patient_list_item, list);
                        patientList.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        //show new list
                    } else {
                        dialog.dismiss();
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                        builder.content("Couldn't find any patients :( Try again later.");
                        builder.title("Oops !");
                        builder.positiveText(android.R.string.ok);
                        builder.show();
                    }
                }
            });
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
