package com.example.threadscontrolapplication.event_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//an event model to represent each event in the symposium
public class Events implements Serializable {
    public String name;
    public String desc;
    public String venue;
    public String startTime;
    public String endTime;
    public int levels;
    public int progress;
    public String contact;
    public String date;
    public int currentLevel;
    public Events()
    {
        currentLevel=1;
        progress=0;
    }
}
