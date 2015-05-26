package com.greenowlmobile.nlp.demonlp.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greenowlmobile.nlp.demonlp.R;
import com.greenowlmobile.nlp.demonlp.data.Incident;

import java.util.ArrayList;

public class MapUtilities {
	public static String MARKER_TYPE_INCIDENT = "INCIDENT";
	
	private static Paint paint;
	private static Canvas canvas;
	
	public static void updateMyLocationMarker(Location location, Marker marker) {
		if (marker != null)
			marker.remove();
		
		Log.d("location", "bearing = " + location.getBearing());
		marker.setFlat(true);
		marker.setAnchor(0.5f, 0.5f);
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_n));
		marker.setRotation(location.getBearing());
		
		marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
	}

	public static void drawIncidentMarkers(FragmentActivity activity, final GoogleMap mapView, final ArrayList<Incident> incidentMarkers) {
		if (incidentMarkers != null) {
			for (final Incident incident : incidentMarkers) {

				final MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(new LatLng(incident.getLatitude(), incident.getLongitude()));
				markerOptions.anchor(0.5f, 1);

//				Drawable drawable = activity.getResources().getDrawable(R.drawable.uk_camera_cross);
//				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

				markerOptions.title(MARKER_TYPE_INCIDENT);
                markerOptions.snippet(incident.getMp3());
				
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mapView.addMarker(markerOptions);

					}
				});
			}
		}
	}

	public static void clearMarkers(FragmentActivity activity, ArrayList<Marker> markers) {
//		Log.d("maptag", "clear the markers on the map");
		if (markers != null) {
			for (final Marker marker : markers) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						marker.remove();
					}
				});
				
				marker.remove();
			}
		}
	}
	
	public static void clearMarker(FragmentActivity activity, final Marker marker) {
		Log.d("maptag", "clear the marker");
		if (marker != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					marker.remove();
				}
			});
		}
	}
	
}