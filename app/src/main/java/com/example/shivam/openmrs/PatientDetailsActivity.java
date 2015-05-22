package com.example.shivam.openmrs;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.util.List;


public class PatientDetailsActivity extends ActionBarActivity {

    TextView patientName,ageText,heightText,phoneText,genderText;
    String patientAge,patientHeight,patientNumber,patientGender,patientDetail;
    ParseFile file;
    Uri imageUri;
    RelativeLayout userImage;
    ImageView userImage2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        String patientNameText = getIntent().getStringExtra("PATIENT_NAME");
        patientName = (TextView)findViewById(R.id.patientName);
        userImage = (RelativeLayout)findViewById(R.id.userImage);
        userImage2 = (ImageView)findViewById(R.id.userImage2);
        ageText = (TextView)findViewById(R.id.userAge);
        heightText = (TextView)findViewById(R.id.userHeight);
        genderText = (TextView)findViewById(R.id.userGender);
        phoneText = (TextView)findViewById(R.id.userPhone);
        patientName.setText(patientNameText);
        ParseQuery<ParseObject> patientQuery = new ParseQuery<ParseObject>("Patient");
        patientQuery.whereEqualTo("patientName", patientNameText);
        patientQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null)
                {
                    patientGender = list.get(0).getString("patientGender");
                    patientAge = String.valueOf(list.get(0).getInt("patientAge"));
                    patientNumber = list.get(0).getString("patientPhone");
                    patientHeight = String.valueOf(list.get(0).getDouble("patientHeight"));
                    System.out.println(patientGender + " " + patientHeight + " " + patientAge);
                    ageText.setText(patientAge+" years");
                    phoneText.setText(patientNumber);
                    heightText.setText(patientHeight+" feet");
                    genderText.setText(patientGender);
                    file = list.get(0).getParseFile("patientImage");
                    imageUri = Uri.parse(file.getUrl());
                    Picasso.with(PatientDetailsActivity.this).load(imageUri).fit().centerCrop().placeholder(R.drawable.ic_person_grey_500_48dp).into(userImage2);
                }
                else
                {
                    Toast.makeText(PatientDetailsActivity.this, "Could not load details !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
