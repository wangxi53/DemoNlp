package com.greenowlmobile.nlp.demonlp.data;

import java.util.ArrayList;

/**
 * Created by xleiw_000 on 2015-05-21.
 */
public class Incident {
    String sourceText;
    int eventCode;
    double latitude;
    double longitude;
    ArrayList<String> mp3s;
    String mp3;

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getMp3s() {
        return mp3s;
    }

    public void setMp3s(ArrayList<String> mp3s) {
        this.mp3s = mp3s;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }
}
