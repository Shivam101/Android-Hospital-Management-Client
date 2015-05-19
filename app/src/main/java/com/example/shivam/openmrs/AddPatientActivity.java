package com.example.shivam.openmrs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddPatientActivity extends ActionBarActivity {

    EditText mName,mAge,mHeight,mPhone;
    Button mSavePatient;
    Spinner mGender;
    int MEDIA_TYPE_IMAGE = 1;
    int PICTURE_INTENT_CODE = 2;
    Uri imageUri;
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
        /*mSavePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Patient patient = new Patient();
                patient.setUuidString();
                patient.setDraft(true);
                String name = mName.getText().toString();
                name = name.trim();
                String age = mAge.getText().toString();
                age = age.trim();
                String height = mHeight.getText().toString();
                height = height.trim();
                String phone = mPhone.getText().toString();
                phone = phone.trim();
                String gender = String.valueOf(mGender.getSelectedItem());
                patient.setName(name);
                patient.setAge(age);
                patient.setGender(gender);
                patient.setHeight(height);
                patient.setPhone(phone);
                patient.pinInBackground("PatientGroup", new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            Toast.makeText(AddPatientActivity.this, "Saved Patient Details !", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AddPatientActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                        else
                        {
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(AddPatientActivity.this);
                            builder.content("Couldn't save patient details :( Try again later.");
                            builder.title("Oops !");
                            builder.positiveText(android.R.string.ok);
                            builder.show();
                        }
                    }
                });
            }
        });*/
        mSavePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = getOutputUri(MEDIA_TYPE_IMAGE);
                if (imageUri == null) {
                    Toast.makeText(AddPatientActivity.this, "Could not access storage", Toast.LENGTH_SHORT).show();
                } else {
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(pictureIntent, PICTURE_INTENT_CODE);
                }
            }
        });
    }

    private Uri getOutputUri(int mediaType) {
        if (hasExternalStorage()) {
            // get external storage directory
            String appName = AddPatientActivity.this.getString(R.string.app_name);
            File extStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
            if(!extStorageDir.exists())
            {
                if(!extStorageDir.mkdirs())
                {
                    Toast.makeText(AddPatientActivity.this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                }
            }
            File mFile;
            Date mCurrentDate = new Date();
            String mTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(mCurrentDate);
            String path = extStorageDir.getPath() + File.separator;
            if(mediaType == MEDIA_TYPE_IMAGE) {
                mFile = new File(path + "PATIENTIMG_" + mTimestamp + ".jpg");
            }
            else
            {
                return null;
            }
            return Uri.fromFile(mFile);
        } else {
            return null;
        }
    }
    private boolean hasExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICTURE_INTENT_CODE) {
            String name = mName.getText().toString();
            name = name.trim();
            String age = mAge.getText().toString();
            age = age.trim();
            String height = mHeight.getText().toString();
            height = height.trim();
            String phone = mPhone.getText().toString();
            phone = phone.trim();
            String gender = String.valueOf(mGender.getSelectedItem());
            Intent galleryAddIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            galleryAddIntent.setData(imageUri);
            AddPatientActivity.this.sendBroadcast(galleryAddIntent);
            Intent sendIntent = new Intent(AddPatientActivity.this, PhotoConfirmationActivity.class);
            sendIntent.setData(imageUri);
            sendIntent.putExtra("name", name);
            sendIntent.putExtra("age", age);
            sendIntent.putExtra("height", height);
            sendIntent.putExtra("phone", phone);
            sendIntent.putExtra("gender", gender);
            startActivity(sendIntent);
        }
        else if(resultCode != RESULT_CANCELED)
        {
            Toast.makeText(AddPatientActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
        }
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
