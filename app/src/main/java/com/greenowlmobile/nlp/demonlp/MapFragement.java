package com.greenowlmobile.nlp.demonlp;

import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greenowlmobile.nlp.demonlp.data.Incident;
import com.greenowlmobile.nlp.demonlp.data.MarkerToPlay;
import com.greenowlmobile.nlp.demonlp.utilities.Constants;
import com.greenowlmobile.nlp.demonlp.utilities.InternetUtilities;
import com.greenowlmobile.nlp.demonlp.utilities.LocationManager;
import com.greenowlmobile.nlp.demonlp.utilities.MapUtilities;
import com.greenowlmobile.nlp.demonlp.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragement extends Fragment {

	private Location location_update;
	private Location location_previous;

	private LatLng current;
	private GoogleMap map;
	private GoogleMap mapViewMagnified;
	private ImageView centerLocationButton;
    private Button nearbyButton;
//	private ArrayList<Marker> markers = new ArrayList<Marker>();// used for clear all markers

    private ArrayList<Marker> mp3Markers = new ArrayList<>();// used for check un-played mp3
	private ImageView btn_drive_mode;
    private double neLat;
    private double neLng;
    private double swLat;
    private double swLng;
    private String url;
    private ArrayList<Incident> incidents = new ArrayList<>();
    private TextToSpeech tts;
    private static volatile boolean enableButtonFlag = false;
    private int pointsToCheck = 0;
    private LatLngBounds currentLatLngBounds;
    private List<MarkerToPlay> markerToPlays;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_map, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

        markerToPlays = new ArrayList<MarkerToPlay>();

		try {
			// register the GPS services
			if (Utilities.checkIfLocationServiceIsOn(getActivity())) {
				// register the GPS services
				LocationManager.getInstance(getActivity(), 5)
						.setLocationRequestPriority(
								LocationRequest.PRIORITY_HIGH_ACCURACY);

				location_update = Constants.LastKnowLocation;

				Constants.locationListener = new LocationManager.LocationListener() {

					@Override
					public void locationChanged(final Location location) {

						if (location != null) {

							if (Constants.MY_LOCATION_MARKER == null) {
								MarkerOptions myLocationMarkerOptions = new MarkerOptions();
								myLocationMarkerOptions.flat(true);
								myLocationMarkerOptions.anchor((float) 0.5,
										(float) 0.5);
								myLocationMarkerOptions
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.red_n));
								myLocationMarkerOptions.position(new LatLng(
										location.getLatitude(), location
												.getLongitude()));
								myLocationMarkerOptions.rotation(location
										.getBearing());

								Constants.MY_LOCATION_MARKER = map
										.addMarker(myLocationMarkerOptions);

								LatLng latLng = new LatLng(
										location.getLatitude(),
										location.getLongitude());
								CameraUpdate cameraUpdate = CameraUpdateFactory
										.newLatLng(latLng);

								map.animateCamera(cameraUpdate);
							}

							if (centerLocationButton.isSelected()) {

								LatLng latLng = new LatLng(
										location.getLatitude(),
										location.getLongitude());
								CameraUpdate cameraUpdate = CameraUpdateFactory
										.newLatLng(latLng);

								Constants.MY_LOCATION_MARKER
										.setPosition(latLng);
								Constants.MY_LOCATION_MARKER
										.setRotation(location.getBearing());

								map.animateCamera(cameraUpdate);

							} else {
								if (Constants.MY_LOCATION_MARKER != null)
								    Constants.MY_LOCATION_MARKER.remove();
							}

                            if(InternetUtilities.hasInternetConnectivity(getActivity())){
                                if(Constants.callToServerFlag){
                                    Constants.callToServerFlag = false;

                                    Thread callToServerThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            neLat = location.getLatitude() + Constants.neLatAdj;
                                            neLng = location.getLongitude() + Constants.neLngAdj;

                                            swLat = location.getLatitude() + Constants.swLatAdj;
                                            swLng = location.getLongitude() + Constants.swLngAdj;

                                            url = Constants.baseUrl + "ne=" + neLat + "," + neLng
                                                    + "&" + "sw=" + swLat + "," + swLng;

                                            updateIncidents(url);

                                        }
                                    });

                                    callToServerThread.start();

                                }

                            } else {
                                if (Constants.noInternetFlag){
                                    Constants.noInternetFlag = false;
                                    Toast.makeText(getActivity(), "No Internet access", Toast.LENGTH_LONG).show();
                                }
                            }

                            //check whether current location is near from any markers
                            pointsToCheck ++;
                            if(pointsToCheck >= Constants.pointsToCheck){
                                pointsToCheck = 0;

                                LatLng neLatLng = new LatLng(location.getLatitude() + neLat, location.getLongitude() + neLng);
                                LatLng swLatLng = new LatLng(location.getLatitude() + swLat, location.getLongitude() + swLng);

//                                LatLng neLatLng = new LatLng( 43.77027 + Constants.neLat, -79.546585 + Constants.neLng);
//                                LatLng swLatLng = new LatLng(43.77027 + Constants.swLat, -79.546585 + Constants.swLng);

                                currentLatLngBounds = new LatLngBounds(swLatLng, neLatLng);

                                Marker marker;

                                float distanceTo;
                                float bearingTo;
                                Location markerLocation;
                                Location newLocation;

                                for (int i = 0; i < mp3Markers.size(); i++){
                                    marker = mp3Markers.get(i);

                                    if (currentLatLngBounds.contains(marker.getPosition())){

                                        markerLocation = new Location("Marker");
                                        markerLocation.setLatitude(marker.getPosition().latitude);
                                        markerLocation.setLongitude(marker.getPosition().longitude);

                                        newLocation = new Location("New Location");

                                        newLocation.setLatitude(location.getLatitude());
                                        newLocation.setLongitude(location.getLongitude());

                                        distanceTo = newLocation.distanceTo(markerLocation);
                                        bearingTo = newLocation.bearingTo(markerLocation);

                                        Log.e("Test Distance", "Distance is: " + distanceTo + "; Bearing to: " + bearingTo);

                                        //should meet distance and bearing limit
                                        if (isInDistance(distanceTo) && !Constants.mps3PlayedMarker.contains(marker.getId())) {
                                                MarkerToPlay markerToPlay = new MarkerToPlay();
                                                markerToPlay.setMarker(marker);
                                                markerToPlay.setDistance(distanceTo);

                                                markerToPlays.add(markerToPlay);
                                        }

                                    }

                                }

                                //play the mp3 and delete the list
                                for (MarkerToPlay markerToPlay : markerToPlays){
                                    speakTextOneByOne(markerToPlay.getMarker().getSnippet());

                                    Constants.mps3PlayedMarker.add(markerToPlay.getMarker().getId());
                                    Log.e("Test", "mp3: " + markerToPlay.getMarker().getSnippet());
                                }

                                markerToPlays.clear();

                            }

						}

						location_update = location;
					}

				};

			}

			// Get a handle to the Map Fragment
			if (Constants.map == null) {
				map = ((MapFragment) getActivity().getFragmentManager()
						.findFragmentById(R.id.map_drive_mode)).getMap();
			} else {
				map = Constants.map;
			}

			// Enable the my location button on the map views
			map.setMyLocationEnabled(false);

			if (location_update == null) {
				current = new LatLng(Constants.Latitude_default,
						Constants.Longitude_default);

				// draw my own current location marker by using default location
				if (Constants.MY_LOCATION_MARKER != null) {
					Constants.MY_LOCATION_MARKER.remove();
				}
				MarkerOptions myLocationMarkerOptions = new MarkerOptions();
				myLocationMarkerOptions.flat(false);
				myLocationMarkerOptions.anchor((float) 0.5, (float) 0.5);
				myLocationMarkerOptions.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.red_n));
				myLocationMarkerOptions.position(current);
				myLocationMarkerOptions.rotation(0);

				Constants.MY_LOCATION_MARKER = map
						.addMarker(myLocationMarkerOptions);

			} else {
				// draw my own current location marker
				if (Constants.MY_LOCATION_MARKER != null) {
					Constants.MY_LOCATION_MARKER.remove();
				}
				MarkerOptions myLocationMarkerOptions = new MarkerOptions();
				myLocationMarkerOptions.flat(false);
				myLocationMarkerOptions.anchor((float) 0.5, (float) 0.5);
				myLocationMarkerOptions.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.red_n));
				myLocationMarkerOptions.position(new LatLng(location_update
						.getLatitude(), location_update.getLongitude()));
				myLocationMarkerOptions.rotation(location_update.getBearing());

				Constants.MY_LOCATION_MARKER = map
						.addMarker(myLocationMarkerOptions);

				current = new LatLng(location_update.getLatitude(),
						location_update.getLongitude());
			}

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,
                    Constants.ZOOM_LEVEL_START_TO_SHOW_DRIVE_MODE));

			// Set the current camera focus update to the latest location (let
			// screen follow the user)
			location_previous = location_update;
			if (location_update != location_previous) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (location_update != null) {
					LatLng latLng = new LatLng(location_update.getLatitude(),
							location_update.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory
							.newLatLng(latLng);
					map.animateCamera(cameraUpdate);
				} else {
					Toast.makeText(getActivity(), "Waiting for GPS signal...",
							Toast.LENGTH_SHORT).show();
				}

			}

			// "follow me" button
			centerLocationButton = (ImageView) getView().findViewById(
					R.id.iv_center_location);

			centerLocationButton.setSelected(Constants.FOLLOW_ME_ENABLE);
			if (centerLocationButton.isSelected()) {
				centerLocationButton.setColorFilter(getResources().getColor(
						R.color.icon_bg));
			} else {
				centerLocationButton.setColorFilter(null);
			}

			centerLocationButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (centerLocationButton.isSelected()) {
						Toast.makeText(getActivity(), "Follow Me: Disabled",
								Toast.LENGTH_SHORT).show();
						centerLocationButton.setColorFilter(null);

					} else {
						if (location_update != null) {
							Toast.makeText(getActivity(), "Follow Me: Enabled",
									Toast.LENGTH_SHORT).show();
							centerLocationButton.setColorFilter(getResources()
									.getColor(R.color.icon_bg));

							LatLng latLng = new LatLng(location_update
									.getLatitude(), location_update
									.getLongitude());
							CameraUpdate cameraUpdate = CameraUpdateFactory
									.newLatLng(latLng);
							map.animateCamera(cameraUpdate);
						} else {
							Toast.makeText(getActivity(),
									"Waiting for GPS signal...",
									Toast.LENGTH_SHORT).show();
						}
					}
					centerLocationButton.setSelected(!centerLocationButton
							.isSelected());

					Constants.FOLLOW_ME_ENABLE = centerLocationButton
							.isSelected();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		btn_drive_mode = (ImageView) getActivity().findViewById(
				R.id.btn_drive_mode);
		btn_drive_mode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ViewPager) getActivity().findViewById(R.id.pager))
						.setCurrentItem(1);

			}
		});

        nearbyButton = (Button) getActivity().findViewById(R.id.button_map_view);
        nearbyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyButton.setClickable(false);

                Constants.mps3PlayedMarker.clear();

                MapUtilities.clearMarkers(getActivity(), mp3Markers);

                mp3Markers.clear();

                final double cameraLat = map.getCameraPosition().target.latitude;
                final double cameraLon = map.getCameraPosition().target.longitude;

                Thread updateIncidentsThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        neLat = cameraLat + Constants.neLatAdj;
                        neLng = cameraLon + Constants.neLngAdj;

                        swLat = cameraLat + Constants.swLatAdj;
                        swLng = cameraLon + Constants.swLngAdj;

                        url = Constants.baseUrl + "ne=" + neLat + "," + neLng
                                + "&" + "sw=" + swLat + "," + swLng;

                        updateIncidents(url);

                    }
                });
                updateIncidentsThread.start();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.isInfoWindowShown()){
                    marker.showInfoWindow();

                    speakText(marker.getSnippet());

                } else {
                    marker.hideInfoWindow();
                    tts.stop();
                }

                return false;
            }
        });

	}

	@Override
	public void onPause() {
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }

		super.onPause();
		// unregister the location listener
		Constants.LastKnowLocation = location_update;
		LocationManager.getInstance(getActivity(), 5).removeListener(
				Constants.locationListener);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Utilities.checkIfLocationServiceIsOn(getActivity());
		LocationManager.getInstance(getActivity(), 5).addListener(
				Constants.locationListener);

        //initialize tts
        tts = new TextToSpeech(getActivity().getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            tts.setLanguage(Locale.CANADA);
                        }
                    }
                });

	}

    private void speakText(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void speakTextOneByOne(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    private boolean isInDistance(float distanceTo){
        if(distanceTo < Constants.distanceLimit){
            return true;
        }

        return false;
    }

    private boolean isInBearing(float bearingTo, Location location){
        if(Math.abs(bearingTo - location.getBearing()) <= Constants.bearingAdj){
            return true;
        }

        return false;
    }

    private void updateIncidents(String url){
        try {
            String result = InternetUtilities.getIncidents(url, Constants.tries_post_to_server);
//                            String result = "[{\"sourceText\":\"Toronto: In YORK on WESTON ROAD SOUTHBOUND between STEELES AVENUE and FINCH AVENUE due to Road construction during the day time as reported by GTN\",\"eventCode\":815,\"mp3s\":[\"weston road\",\"southbound\",\"between\",\"STEELES AVENUE\",\"and\",\"finch avenue\",\"possible slow traffic (default)\",\"due to incident (default)\"],\"lat\":43.77027,\"lon\":-79.546585},{\"sourceText\":\"Toronto: In YORK on STEELES AVENUE WESTBOUND between HIGHWAY 400 / NORFINCH DR and KIPLING AVENUE due to Road construction during the day time as reported by GTN\",\"eventCode\":815,\"mp3s\":[\"steeles avenue\",\"westbound\",\"between\",\"HIGHWAY 400 / NORFINCH DR\",\"and\",\"kipling avenue\",\"possible slow traffic (default)\",\"due to incident (default)\"],\"lat\":43.772076,\"lon\":-79.53805}]";
            Log.e("Test", result);

            if(result != null && !result.trim().equalsIgnoreCase("")
                    && !result.trim().equalsIgnoreCase(Constants.errorResponse)
                    && !result.trim().equalsIgnoreCase(Constants.exceptionResponse)){
                JSONArray incidentsJsonArray = new JSONArray(result);
                JSONObject incidentJsonObject;
                ArrayList<String> mp3s;
                Incident incident;
                StringBuilder mp3;
                String mp3String;
                String handledMp3String;

                if(incidentsJsonArray.length() == 0){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "There is no incident around you.", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                for (int i = 0; i < incidentsJsonArray.length(); i++){
                    mp3s = new ArrayList<>();
                    incident = new Incident();
                    mp3 = new StringBuilder();

                    incidentJsonObject = incidentsJsonArray.getJSONObject(i);
                    incident.setSourceText(incidentJsonObject.getString("sourceText"));
                    incident.setEventCode(incidentJsonObject.getInt("eventCode"));
                    incident.setLatitude(incidentJsonObject.getDouble("lat"));
                    incident.setLongitude(incidentJsonObject.getDouble("lon"));

                    JSONArray mp3sJsonArray = incidentJsonObject.getJSONArray("mp3s");

                    for (int j = 0; j < mp3sJsonArray.length(); j++){
                        mp3String = mp3sJsonArray.getString(j);
                        mp3s.add(mp3String);

                        if(mp3String.contains("default")){
                            handledMp3String = mp3String.replaceAll("default", "")
                                    .replace("(","").replaceAll("\\)", "");
                            mp3.append(handledMp3String);
                        } else {
                            mp3.append(mp3String);
                        }

                        mp3.append(' ');
                    }
                    incident.setMp3s(mp3s);
                    incident.setMp3(mp3.toString());

                    incidents.add(incident);

                    final Incident finalIncident = incident;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //draw markers on map
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(finalIncident.getLatitude(), finalIncident.getLongitude()))
                                    .title("Incident")
                                    .snippet(finalIncident.getMp3())
                                    .anchor(0.5f, 1));

//                                            markers.add(marker);

                            mp3Markers.add(marker);
                        }
                    });

                }

            } else if (result.trim().equalsIgnoreCase("")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "There is no incident around you.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else if (result.trim().equalsIgnoreCase(Constants.errorResponse)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Server Error",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else if (result.trim().equalsIgnoreCase(Constants.exceptionResponse)){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Caught Exception",
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nearbyButton.setClickable(true);
                }
            });

        } catch (Exception e){
            e.printStackTrace();

        }

    }

}
