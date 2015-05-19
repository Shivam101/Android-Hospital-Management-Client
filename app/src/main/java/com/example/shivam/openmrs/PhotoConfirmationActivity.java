package com.example.shivam.openmrs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class PhotoConfirmationActivity extends ActionBarActivity {

    ImageView mPatientImage;
    Uri imageUri;
    String patientName,patientAge,patientHeight,patientGender,patientPhone;
    Button mSave;
    Bitmap bitmap;
    Dialog progressDialog;
    Patient patient = new Patient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_confirmation);
        imageUri = getIntent().getData();
        mPatientImage = (ImageView)findViewById(R.id.image);
        mSave = (Button)findViewById(R.id.saveImage);
        Picasso.with(this).load(imageUri.toString()).resize(500,500).into(mPatientImage);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient.setUuidString();
                patient.setDraft(true);
                patientName = getIntent().getStringExtra("name");
                patientAge = getIntent().getStringExtra("age");
                patientGender = getIntent().getStringExtra("gender");
                patientHeight = getIntent().getStringExtra("height");
                patientPhone = getIntent().getStringExtra("phone");
                patientName = patientName.trim();
                patient.setName(patientName);
                patient.setAge(patientAge);
                patient.setGender(patientGender);
                patient.setHeight(patientHeight);
                patient.setPhone(patientPhone);
                byte[] fileData = FileHelper.getByteArrayFromFile(PhotoConfirmationActivity.this, imageUri);
                fileData = FileHelper.reduceImageForUpload(fileData);
                String fileName = FileHelper.getFileName(PhotoConfirmationActivity.this, imageUri);
                ParseFile mFile = new ParseFile(fileName, fileData);
                patient.setImage(mFile);
                mFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null)
                        {
                            Toast.makeText(PhotoConfirmationActivity.this,"Could not save image",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            PhotoConfirmationActivity.this.progressDialog = ProgressDialog.show(PhotoConfirmationActivity.this, "", "Saving Patient Details...", true);

                            patient.pinInBackground("PatientGroup", new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("STA", "here");
                                        PhotoConfirmationActivity.this.progressDialog.dismiss();
                                        Toast.makeText(PhotoConfirmationActivity.this, "Saved Patient Details !", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(PhotoConfirmationActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    } else {
                                        Log.e("ERROR", String.valueOf(e));
                                        PhotoConfirmationActivity.this.progressDialog.dismiss();
                                        MaterialDialog.Builder builder = new MaterialDialog.Builder(PhotoConfirmationActivity.this);
                                        builder.content("Couldn't save patient details :( Try again later.");
                                        builder.title("Oops !");
                                        builder.positiveText(android.R.string.ok);
                                        builder.show();
                                    }
                                }
                            });

                        }
                    }
                });
//                PhotoConfirmationActivity.this.progressDialog = ProgressDialog.show(PhotoConfirmationActivity.this, "", "Saving Patient Details...", true);
//
//                patient.pinInBackground("PatientGroup", new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Log.e("STA", "here");
//                            PhotoConfirmationActivity.this.progressDialog.dismiss();
//                            Toast.makeText(PhotoConfirmationActivity.this, "Saved Patient Details !", Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(PhotoConfirmationActivity.this, MainActivity.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(i);
//                        } else {
//                            Log.e("ERROR", String.valueOf(e));
//                            PhotoConfirmationActivity.this.progressDialog.dismiss();
//                            MaterialDialog.Builder builder = new MaterialDialog.Builder(PhotoConfirmationActivity.this);
//                            builder.content("Couldn't save patient details :( Try again later.");
//                            builder.title("Oops !");
//                            builder.positiveText(android.R.string.ok);
//                            builder.show();
//                        }
//                    }
//                });


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_confirmation, menu);
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
