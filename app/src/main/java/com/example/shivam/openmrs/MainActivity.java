package com.example.shivam.openmrs;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import br.liveo.interfaces.NavigationLiveoListener;
import br.liveo.navigationliveo.NavigationLiveo;


public class MainActivity extends NavigationLiveo implements NavigationLiveoListener {

    ParseUser currentUser;
    public ArrayList<String> mListNameItem;

    @Override
    public void onUserInformation() {

        currentUser = ParseUser.getCurrentUser();
        if(currentUser!=null) {
            this.mUserName.setText(currentUser.getUsername());
            this.mUserEmail.setText(currentUser.getEmail());
            this.mUserBackground.setImageResource(R.drawable.background);
        }
        else
        {
            Intent i = new Intent(MainActivity.this, SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    }

    @Override
    public void onInt(Bundle bundle) {
        currentUser = ParseUser.getCurrentUser();
        if(currentUser==null)
        {
            Intent i = new Intent(MainActivity.this, SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        this.setNavigationListener(this);
        this.setDefaultStartPositionNavigation(0);
        this.removeSelectorNavigation();
        this.setColorSelectedItemNavigation(R.color.nliveo_red_colorPrimary);
        mListNameItem = new ArrayList<>();
        mListNameItem.add(0, "Patients");
        mListNameItem.add(1, "Capture Vitals");
        mListNameItem.add(2, "Appointment Scheduling");
        ArrayList<Integer> mListIconItem = new ArrayList<>();

        mListIconItem.add(0, R.drawable.ic_person_grey_500_24dp);
        mListIconItem.add(1, R.drawable.ic_favorite_grey_500_24dp);
        mListIconItem.add(2, R.drawable.ic_schedule_grey_500_24dp);
        ArrayList<Integer> mListHeaderItem = new ArrayList<>();
        mListHeaderItem.add(4);

        SparseIntArray mSparseCounterItem = new SparseIntArray();
        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.logOut();
            Intent i = new Intent(MainActivity.this, SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickNavigation(int position, int containerLayout) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment;
        switch (position) {
            case 0:
                this.getToolbar().setTitle("Patients");
                mFragment = new PatientsFragment().newInstance();
                if (mFragment != null) {
                    mFragmentManager.beginTransaction().replace(containerLayout, mFragment).commit();
                }
                break;
            case 1:
                this.getToolbar().setTitle("Capture Vitals");
                mFragment = new VitalsFragment().newInstance();
                if (mFragment != null) {
                    mFragmentManager.beginTransaction().replace(containerLayout, mFragment).commit();
                }
                break;
            case 2:
                this.getToolbar().setTitle("Appointment Scheduling");
                mFragment = new AppointmentsFragment().newInstance();
                if (mFragment != null) {
                    mFragmentManager.beginTransaction().replace(containerLayout, mFragment).commit();
                }
                break;
        }

    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int i, boolean b) {

    }

    @Override
    public void onClickFooterItemNavigation(View view) {

    }

    @Override
    public void onClickUserPhotoNavigation(View view) {

    }
}
