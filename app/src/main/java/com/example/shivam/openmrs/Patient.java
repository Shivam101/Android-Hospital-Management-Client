package com.example.shivam.openmrs;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

/**
 * Created by Shivam on 14/05/15 at 12:25 PM.
 */
@ParseClassName("Patient")
public class Patient extends ParseObject {

    public String getName()
    {
        return getString("patientName");
    }

    public String getDetails()
    {
        return getString("patientAge")+" "+getString("patientGender")+" Height:"+getString("patientHeight")+getString("patientPhone");
    }

    public void setName(String name)
    {
        put("patientName",name);
    }

    public void setAge(String age)
    {
        put("patientAge",age);
    }

    public void setGender(String gender)
    {
        put("patientGender",gender);
    }

    public void setHeight(String height)
    {
        put("patientHeight",height);
    }

    public void setPhone(String phone) { put ("patientPhone",phone); }

    public boolean isDraft()
    {
        return getBoolean("isDraft");
    }

    public void setDraft(boolean isDraft)
    {
        put("isDraft", isDraft);
    }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }


    public static ParseQuery<Patient> getQuery()
    {
        return ParseQuery.getQuery(Patient.class);
    }

}