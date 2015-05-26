package com.greenowlmobile.nlp.demonlp.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class InternetUtilities {

	public static boolean hasInternetConnectivity(Activity activity) {
		NetworkInfo info = ((ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();

		if (info != null && info.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}
	
	public static String getIncidents(String url, int tries) {

        InputStream inputStream = null;
        String result = null;
        try {
            for (int i = 0; i < tries ; i++) {
                HttpParams httpParams = new BasicHttpParams();

                if (i + 1 >= tries) {
                    HttpConnectionParams.setConnectionTimeout(httpParams, Constants.CONNECTION_TIME_OUT);
                    HttpConnectionParams.setSoTimeout(httpParams, Constants.CONNECTION_TIME_OUT);
                }

                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpClient.setParams(httpParams);

                // make GET request to the given URL
                HttpResponse response = httpClient.execute(new HttpGet(url));

                if (response != null) {
                    Log.i("Test",
                            "*url = " + url + "\n" +
                                    "*response.getStatusLine() = " + response.getStatusLine());
                }

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // receive response as inputStream
                    inputStream = response.getEntity().getContent();

                    // convert inputstream to string
                    if (inputStream != null) {
                        result = convertInputStreamToString(inputStream);

                        response.getEntity().consumeContent();

                        return result;
                    }
                } else {
                    result = "";
                }

            }

        } catch (Exception e) {
            Log.e("Test", e.toString());
            result = "";
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
