package com.example.shivam.openmrs;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.shivam.openmrs.R;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.text.WordUtils;


/**
 * Created by Shivam on 13/05/15 at 3:00 PM.
 */
public class PatientAdapter extends BaseAdapter implements SectionIndexer {

    Context mContext;
    List<ParseObject> mPatients=null;
    static int layoutResourceId;
    HashMap<String, Integer> mapIndex;
    String[] sections;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

    public PatientAdapter(Context context,int layoutResourceId, List<ParseObject> mPatients) {
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.mPatients = mPatients;
        mapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mPatients.size(); x++) {
            ParseObject pat = mPatients.get(x);
            String ch = pat.getString("patientName");
            String ch2 = ch.substring(0, 1);
            ch2 = ch2.toUpperCase(Locale.US);

            // HashMap will prevent duplicates
            mapIndex.put(ch2, x);
        }
        Set<String> sectionLetters = mapIndex.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Log.d("sectionList", sectionList.toString());
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.patient_list_item, null);
            holder = new ViewHolder();
            holder.titleLabel = (TextView) convertView.findViewById(R.id.patientName);
            holder.contentLabel = (TextView) convertView.findViewById(R.id.patientDetails);
            holder.userImage = (ImageView)convertView.findViewById(R.id.userImage);
            convertView.setTag(holder); //VERY IMPORTANT LINE !!!
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject patient = mPatients.get(position);
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        holder.titleLabel.setText(WordUtils.capitalize(patient.getString("patientName")));
        holder.contentLabel.setText(String.valueOf(patient.getInt("patientAge")) + " " + patient.getString("patientGender") + " Height: "+String.valueOf(patient.getDouble("patientHeight"))+" Date: "+df.format(patient.get("patientDate")));
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig().width(100).height(100)
                .endConfig()
                .round();
        TextDrawable td = builder.build(WordUtils.capitalize(patient.getString("patientName")).substring(0, 1), color1);
        //TextDrawable td = TextDrawable.builder().beginConfig().width(50).height(50).endConfig().buildRect(patient.getString("patientName").substring(0,1),R.color.accentColor);
        //holder.userImage.setImageDrawable(td);

            //Log.e("IMAGE",String.valueOf(Uri.parse(patient.getParseFile("patientImage").getData().toString())));
            //System.out.println(Uri.parse(patient.getParseFile("patientImage").getData().toString()));
            ParseFile file = patient.getParseFile("patientImage");
            Uri imageUri = Uri.parse(file.getUrl());
            Picasso.with(this.mContext).load(imageUri).resize(100,100).placeholder(R.drawable.ic_person_grey_500_48dp).into(holder.userImage);
            /*patient.getParseFile("patientImage").getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Picasso.with(mContext).load(Uri.parse(bytes.toString())).into(holder.userImage);
                    } else {
                        //Toast.makeText(MainActivity.class)
                    }
                }
            });*/
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mapIndex.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        TextView titleLabel;
        TextView contentLabel;
        ImageView userImage;
    }

    public void refreshAdapter(List<ParseObject> patients)
    {
        mPatients.clear();
        mPatients.addAll(patients);
        notifyDataSetChanged();
    }





}