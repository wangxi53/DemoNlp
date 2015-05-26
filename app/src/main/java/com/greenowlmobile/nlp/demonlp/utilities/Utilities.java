package com.greenowlmobile.nlp.demonlp.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

public class Utilities {

	public static void keepScreenOn(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
	
	public static String getCurrentTime(){
		String currentTimeString;
		Date dt=new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));   
		currentTimeString = df.format(dt);
		return currentTimeString;
	}
	
	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	public static boolean checkIfLocationServiceIsOn(final FragmentActivity activity) {
		// Get Location Manager and check for GPS & Network location services
        android.location.LocationManager lm = (android.location.LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
              !lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
          // Build the alert dialog
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          builder.setTitle("GPS/Location Services Not Active");
          builder.setMessage("Please turn On GPS/Location Services to use the full functionality of this application.");
          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            // Show location settings when the user acknowledges the alert dialog
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
            }
          });
          Dialog alertDialog = builder.create();
          alertDialog.setCanceledOnTouchOutside(false);
          alertDialog.show();
          return false;
        }
        return true;
	}
	
	public static boolean checkIfLocationServiceIsOn(final Fragment fragment) {
		// Get Location Manager and check for GPS & Network location services
        android.location.LocationManager lm = (android.location.LocationManager) fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
              !lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
          // Build the alert dialog
          AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
          
          builder.setTitle("GPS/Location Services Not Active");
          builder.setMessage("Please turn On GPS/Location Services to use the full functionality of this application.");
          
          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialogInterface, int i) {
	            // Show location settings when the user acknowledges the alert dialog
	            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	            fragment.getActivity().startActivity(intent);
	            }
          });
          
          Dialog alertDialog = builder.create();
          alertDialog.setCanceledOnTouchOutside(false);
          alertDialog.show();
          return false;
        }
        return true;
	}
	
	public static boolean checkIfLocationServiceIsOn(final Activity activity) {
		// Get Location Manager and check for GPS & Network location services
        android.location.LocationManager lm = (android.location.LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
              !lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
          // Build the alert dialog
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          builder.setTitle("GPS/Location Services Not Active");
          builder.setMessage("Please turn On GPS/Location Services to use the full functionality of this application.");
          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            // Show location settings when the user acknowledges the alert dialog
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
            }
          });
          Dialog alertDialog = builder.create();
          alertDialog.setCanceledOnTouchOutside(false);
          alertDialog.show();
          return false;
        }
        return true;
	}
	
	
	
	public static String userNamePasswordBase64(String username, String password)
	{
	 return "Basic " + base64Encode(username + ":" + password);
	}
	
	private static String base64Encode (String string){
	    String encodedString = "";
	    byte bytes [] = string.getBytes ();
	    int i = 0;
	    int pad = 0;
	    while (i < bytes.length) {
	      byte b1 = bytes [i++];
	      byte b2;
	      byte b3;
	      if (i >= bytes.length) {
	         b2 = 0;
	         b3 = 0;
	         pad = 2;
	         }
	      else {
	         b2 = bytes [i++];
	         if (i >= bytes.length) {
	            b3 = 0;
	            pad = 1;
	            }
	         else
	            b3 = bytes [i++];
	         }
	      byte c1 = (byte)(b1 >> 2);
	      byte c2 = (byte)(((b1 & 0x3) << 4) | (b2 >> 4));
	      byte c3 = (byte)(((b2 & 0xf) << 2) | (b3 >> 6));
	      byte c4 = (byte)(b3 & 0x3f);
	      encodedString += base64Array [c1];
	      encodedString += base64Array [c2];
	      switch (pad) {
	       case 0:
	         encodedString += base64Array [c3];
	         encodedString += base64Array [c4];
	         break;
	       case 1:
	         encodedString += base64Array [c3];
	         encodedString += "=";
	         break;
	       case 2:
	         encodedString += "==";
	         break;
	       }
	      }
	      return encodedString;
	  }
	
	private final static char base64Array [] = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
	      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
	      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
	      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
	      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
	      'w', 'x', 'y', 'z', '0', '1', '2', '3',
	      '4', '5', '6', '7', '8', '9', '+', '/'
	  };
	
}
