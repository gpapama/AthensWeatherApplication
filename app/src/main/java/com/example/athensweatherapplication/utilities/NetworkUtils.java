package com.example.athensweatherapplication.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    //we get the data from operweathermap
    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast?";

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";

    final static String QUERY_PARAM = "id";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    //we need an API id to fetch the data
    final static String APIID = "APPID";

   //Build the URL
    public static URL buildUrl(String locationQuery) {
        Uri builtUri = Uri.parse(WEATHER_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(APIID, "bfb65ff129d822c588d60200e5719473")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v("rasse", "Built URI " + url);
        return url;
    }

    //get the response
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

