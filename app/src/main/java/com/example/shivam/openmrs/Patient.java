package com.example.shivam.openmrs;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
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

    public String getDetails() {
        return String.valueOf(getInt("patientAge"))+" "+getString("patientGender")+" Height:"+String.valueOf(getDouble("patientHeight"))+getString("patientPhone");
    }

    public void setName(String name)
    {
        put("patientName",name);
    }

    public void setAge(Integer age)
    {
        put("patientAge",age);
    }

    public void setGender(String gender)
    {
        put("patientGender",gender);
    }

    public void setHeight(Double height)
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

    public void setImage(ParseFile image) { put("patientImage",image); }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }


    public static ParseQuery<Patient> getQuery()
    {
        return ParseQuery.getQuery(Patient.class);
    }

}
