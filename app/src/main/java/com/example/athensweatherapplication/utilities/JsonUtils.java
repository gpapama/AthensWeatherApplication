package com.example.athensweatherapplication.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Date;

public class JsonUtils {
    //extract the data from json
    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

            /* Weather information. Each day's forecast info is an element of the "list" array */
            final String OWM_LIST = "list";

            /* All temperatures are children of the "main" object */
            final String OWM_TEMPERATURE = "main";

            /* Max temperature for the day */
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";

            final String OWM_WEATHER = "weather";
            final String OWM_DESCRIPTION = "main";
            final String OWM_DATE = "dt_txt";
            final String OWM_MESSAGE_CODE = "cod";

            /* String array to hold each day's weather String */
            String[] parsedWeatherData = null;

            JSONObject forecastJson = new JSONObject(forecastJsonStr);

            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        /* Location invalid */
                        return null;
                    default:
                        /* Server probably down */
                        return null;
                }
            }

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            parsedWeatherData = new String[weatherArray.length()];

            long localDate = System.currentTimeMillis();
            long utcDate = DateUtils.getUTCDateFromLocal(localDate);
            long startDay = DateUtils.normalizeDate(utcDate);
            Date d = new Date();
            int now_time = (int) ((d.getTime() / 1000 / 60 / 60) % 24+3);
            now_time =  now_time - now_time % 3;
            long dateTimeMillis = startDay+now_time*3600000 ;
            long timeOfDay = 0;
            String myTime;
            for (int i = 0; i < weatherArray.length(); i++) {
                String date;
                String highAndLow;

                /* These are the values that will be collected */
                double high;
                double low;
                String description;

                /* Get the JSON object representing the day */
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                timeOfDay = (dateTimeMillis / (1000 * 60 * 60)) % 24;
                if (timeOfDay == 0) myTime="00:00";
                else myTime=timeOfDay + ":00";
                date = DateUtils.getFriendlyDateString(context, dateTimeMillis, false);


                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);


                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);
                highAndLow = WeatherUtils.formatHighLows(context, high, low);

                parsedWeatherData[i] = date + " - " + myTime + " - " + description + " - " + highAndLow;
                dateTimeMillis = dateTimeMillis + 10800000;
            }

            return parsedWeatherData;
    }

}
