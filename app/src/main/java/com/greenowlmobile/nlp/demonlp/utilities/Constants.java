package com.greenowlmobile.nlp.demonlp.utilities;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


public class Constants {
	//Constants for splash page
	public final static int Splash_Timer_Interval = 5 * 1000;//declare the total time to count down for the timer
	public final static int Splash_Countdown_Intervel = 1000;

    public static final int tries_post_to_server = 3;
    public static final int CONNECTION_TIME_OUT = 60000;
    public static final String baseUrl = "http://54.224.65.246:8080/incidents_nlp_poc/getIncidentsJSON?";

    //for Map
    public static Marker MY_LOCATION_MARKER;
    public final static int ZOOM_LEVEL_START_TO_SHOW_DRIVE_MODE = 10;
    public static boolean FOLLOW_ME_ENABLE = true;
    public static GoogleMap map;
    public static LatLng Latlng_report_from_map_view = null;
    public static Location LastKnowLocation;
    public final static double Latitude_default = 43.3065991;
    public final static double Longitude_default = -79.9163396;
    public final static double neLatAdj = 0.5;
    public final static double neLngAdj = 0.5;
    public final static double swLatAdj = -0.5;
    public final static double swLngAdj = -0.5;

    public final static double neLat = 0.05;
    public final static double neLng = 0.05;
    public final static double swLat = -0.05;
    public final static double swLng = -0.05;

    public final static float bearingAdj = 25f;
    public final static int distanceLimit = 300;

    public static boolean callToServerFlag = true;
    public static boolean noInternetFlag = true;
    public static final int pointsToCheck = 10;

    public static List<String> mps3PlayedMarker = new ArrayList<>();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

}
