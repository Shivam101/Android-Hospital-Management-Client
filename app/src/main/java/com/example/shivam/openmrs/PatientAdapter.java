package com.example.shivam.openmrs;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shivam.openmrs.R;
import com.parse.ParseObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Shivam on 13/05/15 at 3:00 PM.
 */
public class PatientAdapter extends BaseAdapter {

    Context mContext;
    List<ParseObject> mPatients=null;
    static int layoutResourceId;

    public PatientAdapter(Context context,int layoutResourceId, List<ParseObject> mPatients) {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.mPatients = mPatients;
    }

    @Override
    public int getCount() {
        return mPatients.size();
    }

    @Override
    public Object getItem(int position) {
        return mPatients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.patient_list_item, null);
            holder = new ViewHolder();
            holder.titleLabel = (TextView) convertView.findViewById(R.id.patientName);
            holder.contentLabel = (TextView) convertView.findViewById(R.id.patientDetails);
            convertView.setTag(holder); //VERY IMPORTANT LINE !!!
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject patient = mPatients.get(position);
        holder.titleLabel.setText(patient.getString("patientName"));
        holder.contentLabel.setText(patient.getString("patientAge")+" "+patient.getString("patientGender")+" Height:"+patient.getString("patientHeight"));
        return convertView;
    }
    public static class ViewHolder
    {
        TextView titleLabel;
        TextView contentLabel;
    }

    public void refreshAdapter(List<ParseObject> patients)
    {
        mPatients.clear();
        mPatients.addAll(patients);
        notifyDataSetChanged();
    }





}