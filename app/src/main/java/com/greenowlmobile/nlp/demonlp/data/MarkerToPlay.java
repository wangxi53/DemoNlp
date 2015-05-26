package com.greenowlmobile.nlp.demonlp.data;

import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;

/**
 * Created by xleiw_000 on 2015-05-26.
 */
public class MarkerToPlay {
    private Marker marker;
    private float distance;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkerToPlay)) return false;

        MarkerToPlay that = (MarkerToPlay) o;

        if (Float.compare(that.distance, distance) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (distance != +0.0f ? Float.floatToIntBits(distance) : 0);
    }

}
