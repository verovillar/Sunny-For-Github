package com.example.temp.sunny.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ( findViewById(R.id.weather_detail_container) != null ) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if ( savedInstanceState == null ) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_preferred_location_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String preferredLocation = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Intent mapViewIntent= new Intent(Intent.ACTION_VIEW);
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", preferredLocation).build();
        mapViewIntent.setData(geoLocation);
        if (mapViewIntent.resolveActivity((getPackageManager())) != null) {
            startActivity(mapViewIntent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + preferredLocation + ", no activity available to handle this request.");
        }
    }

    @Override
    public void onItemSelected(String date) {


        if (mTwoPane) {
            DetailFragment forecastDetails = DetailFragment.newInstance(date);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, forecastDetails)
                    .commit();
        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, date, duration);
            toast.show();
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
