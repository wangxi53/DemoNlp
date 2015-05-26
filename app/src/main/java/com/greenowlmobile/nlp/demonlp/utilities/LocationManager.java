package com.greenowlmobile.nlp.demonlp.utilities;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LocationManager implements
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static LocationManager instance;
    private ArrayList<LocationListener> locationListeners;

    // Global constants
    // Milliseconds per second
    private final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;

    // Global variables
    GoogleApiClient mGoogleApiClient;
    boolean mUpdatesRequested;
    private FragmentActivity fragmentActivity;
    private Activity activity;

    private SimpleDateFormat _df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS");
    private String timeStamp;
    // this is a number calculated by (_totalPointCount - 1) mod 30, and it's used as a counter to trigger upload point event
    private int pointCount;
    // total number of GPS data collected
    private int totalPointCount;

    private int bufferSize;
    private Location previousLocation;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public LocationManager(FragmentActivity fragmentActivity, int bufferSize) {
        this.fragmentActivity = fragmentActivity;
        this.bufferSize = bufferSize;

        instance = this;

        pointCount = 0;
        totalPointCount = 0;
        locationListeners = new ArrayList<LocationListener>();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Start with updates turned off
        mUpdatesRequested = true;
    }

    public LocationManager(Activity activity, int bufferSize) {
        this.activity = activity;
        this.bufferSize = bufferSize;

        instance = this;

        pointCount = 0;
        totalPointCount = 0;
        locationListeners = new ArrayList<LocationListener>();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // Start with updates turned off
        mUpdatesRequested = true;
    }

    public static LocationManager getInstance(FragmentActivity fragmentActivity, int bufferSize) {
        if (null == instance) {
            instance = new LocationManager(fragmentActivity, bufferSize);
        }
        return instance;
    }

    public static LocationManager getInstance(Activity activity, int bufferSize) {
        if (null == instance) {
            instance = new LocationManager(activity, bufferSize);
        }
        return instance;
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void setLocationRequestPriority(int priority) {
        if (priority == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY) {
            Log.d("priority", "PRIORITY_BALANCED_POWER_ACCURACY");
        } else if (priority == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            Log.d("priority", "PRIORITY_HIGH_ACCURACY");
        }
        mLocationRequest.setPriority(priority);

        mUpdatesRequested = true;
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.reconnect();
        } else {
            mGoogleApiClient.connect();
        }

    }

    public void stop() {
        mGoogleApiClient.disconnect();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Display the connection status
        Toast.makeText(activity, "Connection Failed: " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();


    	/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        activity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
//            activity.showDialog(connectionResult.getErrorCode());
        }
    }


    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // If already requested, start periodic updates
        if (mUpdatesRequested) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //not moving
        if (location.getBearing() == 0) {
            if (previousLocation != null) {
                //use previous bearing if available
                location.setBearing(previousLocation.getBearing());
            }
//			else {
//				//create a bearing based on the last two points
//				previousLocation = location;
//				bearing = Utilities.getBearing(location, previousLocation);
//			}
        } else if (previousLocation != null && location.getBearing() == 90) {
            //used for gps simulator
            location.setBearing(previousLocation.bearingTo(location));
//			location.setBearing(Utilities.getBearing(location, previousLocation));
        }
//		Logger.d("gps", "location.getBearing() = " + location.getBearing());

        previousLocation = location;


        fireLocationChanged(location);

    }

    public Location getLastKnownLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /////// L O C A T I O N   L I S T E N E R ///////
    public interface LocationListener {
        public void locationChanged(Location location);
    }

    public void addListener(LocationListener listener) {
        if (!locationListeners.contains(listener)) {
            locationListeners.add(listener);
        }
    }

    public void removeListener(LocationListener listener) {
        locationListeners.remove(listener);
    }

    private void fireLocationChanged(Location location) {
        for (LocationListener listener : locationListeners) {
            listener.locationChanged(location);
        }
    }
}