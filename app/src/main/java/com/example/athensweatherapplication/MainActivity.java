package com.example.athensweatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.athensweatherapplication.utilities.JsonUtils;
import com.example.athensweatherapplication.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // athens weather text
    private TextView weatherTextView;

    //error text
    private TextView errorMessage;

    //progress bar
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the references from xml
        weatherTextView = (TextView) findViewById(R.id.athens_weather_data);

        errorMessage = (TextView) findViewById(R.id.error_message_display);

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        loadWeatherData();

    }



    //starts the new activity if we click on the weather text
    public void myOnClick(View myWeatherTextView) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, ((TextView) myWeatherTextView).getText().toString());
        startActivity(intentToStartDetailActivity);
    }

    private void loadWeatherData() {
        // Call showWeatherDataView before executing the AsyncTask
        showWeatherDataView();
        //start the async operation to fetch the weather data
        //the id=264371 is for Athens
        new FetchWeatherTask().execute("264371");
    }

    // method to show the results and hide the error message
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        errorMessage.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        weatherTextView.setVisibility(View.VISIBLE);
    }

    //method to show the error message and hide the results
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        weatherTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        errorMessage.setVisibility(View.VISIBLE);
    }

    //the asyncronous operation to fetch the data from the api
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        //show the loading bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            //get the location
            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            //the request and the json response
            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = JsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        //make the progress bar invisible and show results
        @Override
        protected void onPostExecute(String[] weatherData) {
            // As soon as the data is finished loading, hide the loading indicator
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                // If the weather data was not null, make sure the data view is visible
                showWeatherDataView();
                // Iterate through the array and append the Strings to the TextView.
                for (String weatherString : weatherData) {
                    weatherTextView.append((weatherString) + "\n\n\n");
                }
            }
            //if no data show error message
            else {
                showErrorMessage();
            }
        }
    }

    private void openLocationInMap() {
        String addressString = "31 Pandrosou, Plaka, Athens";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //if we click the refresh button
        if (id == R.id.action_refresh) {
            weatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
