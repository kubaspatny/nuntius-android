package com.kubaspatny.startupanimation;

import android.util.Log;

import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Kuba on 26/8/2014.
 */
public class NetworkUtils {

    public static final String DEBUG_TAG = "NetworkUtils";

    public static int postHTTP(URL url, List<NameValuePair> params){

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQueryParameters(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            int response = conn.getResponseCode();

            Log.d(DEBUG_TAG, "The response is: " + response);

            return response;

        } catch(Exception e){

            Log.e(DEBUG_TAG, e.getLocalizedMessage());
            return 500;

        }

    }

    public static String getHTTP(URL url){

        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());

            result = inputStreamToString(in);

        } catch(Exception e) {

        } finally {
            if(urlConnection != null) urlConnection.disconnect();
        }

        return result;

    }

    private static String getQueryParameters(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static String inputStreamToString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}

class PostMessageHolder {

    private URL url;
    private List<NameValuePair> parms;

    public PostMessageHolder(URL url, List<NameValuePair> parms) {
        this.url = url;
        this.parms = parms;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<NameValuePair> getParms() {
        return parms;
    }

    public void setParms(List<NameValuePair> parms) {
        this.parms = parms;
    }

}