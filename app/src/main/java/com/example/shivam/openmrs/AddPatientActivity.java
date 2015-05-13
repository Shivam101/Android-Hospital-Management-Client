package com.example.shivam.openmrs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;


public class AddPatientActivity extends ActionBarActivity {

    EditText mName,mAge,mHeight,mPhone;
    Button mSavePatient;
    Spinner mGender;
    Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        mName = (EditText)findViewById(R.id.nameET);
        mAge = (EditText)findViewById(R.id.ageET);
        mHeight = (EditText)findViewById(R.id.heightET);
        mPhone = (EditText)findViewById(R.id.phoneET);
        mSavePatient = (Button)findViewById(R.id.savePatient);
        mGender = (Spinner)findViewById(R.id.genderSpinner);
        mSavePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject patientObject = new ParseObject("Patient");
                String name = mName.getText().toString();
                name = name.trim();
                String age = mAge.getText().toString();
                age = age.trim();
                String height = mHeight.getText().toString();
                height = height.trim();
                String phone = mPhone.getText().toString();
                phone = phone.trim();
                String gender = String.valueOf(mGender.getSelectedItem());
                if(name.isEmpty()||age.isEmpty()||height.isEmpty()||phone.isEmpty()||gender.isEmpty())
                {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(AddPatientActivity.this);
                    builder.content("Details cannot be blank.");
                    builder.title("Oops !");
                    builder.positiveText(android.R.string.ok);
                    builder.show();
                }
                else{
                patientObject.put("patientName",name);
                patientObject.put("patientAge",age);
                patientObject.put("patientHeight",height);
                patientObject.put("patientGender",gender);
                patientObject.put("patientPhone",phone);
                AddPatientActivity.this.progressDialog = ProgressDialog.show(AddPatientActivity.this, "", "Saving Patient Details...", true);
                patientObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null)
                        {
                            AddPatientActivity.this.progressDialog.dismiss();
                            Toast.makeText(AddPatientActivity.this,"Saved Patient Details !",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AddPatientActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                        else
                        {
                            AddPatientActivity.this.progressDialog.dismiss();
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(AddPatientActivity.this);
                            builder.content("Couldn't save patient details :( Try again later.");
                            builder.title("Oops !");
                            builder.positiveText(android.R.string.ok);
                            builder.show();
                        }
                    }
                });
            }}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_patient, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
