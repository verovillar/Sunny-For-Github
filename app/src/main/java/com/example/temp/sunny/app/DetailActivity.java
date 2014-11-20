package com.example.temp.sunny.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.temp.sunny.app.data.WeatherContract;
import com.example.temp.sunny.app.data.WeatherContract.WeatherEntry;


public class DetailActivity extends ActionBarActivity {

    public static final String DATE_KEY = "forecast_date";
    private static final String LOCATION_KEY = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";


        private String mForecast;
        private String mLocation;
        private static final int DETAIL_FORECAST_LOADER = 0;

        private ShareActionProvider mShareActionProvider;

        public DetailFragment() {
            this.setHasOptionsMenu(true);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString(LOCATION_KEY, mLocation);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mLocation != null &&
                    !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
                getLoaderManager().restartLoader(DETAIL_FORECAST_LOADER, null, this);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem menuItem;
            menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = /*new ShareActionProvider(getActivity());*/(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null) {

                // If onLoadFinished happens before this, we can go ahead and set the share intent now.
                if (mForecast != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                }
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        public Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_FORECAST_LOADER, null, this);
            if (savedInstanceState != null ) {
                mLocation = savedInstanceState.getString(LOCATION_KEY);
            }
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if( intent == null || !intent.hasExtra(DATE_KEY) ) {
                return null;
            }

            String forecastDate = intent.getStringExtra(DATE_KEY);

            String[] columns = {
                    WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                    WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_HUMIDITY,
                    WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_WIND_SPEED,
                    WeatherEntry.COLUMN_DEGREES,
                    WeatherEntry.COLUMN_WEATHER_ID,
                    // This works because the WeatherProvider returns location data joined with
                    // weather data, even though they're stored in two different tables.
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
            };

            // Sort order: Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

            mLocation = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, forecastDate);

            Log.d("DetailFragment", "Uri: " + weatherForLocationUri.toString());

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    columns,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

            String dateString = Utility.formatDate(
                    data.getString(data.getColumnIndex(WeatherEntry.COLUMN_DATETEXT)));
            ((TextView) getView().findViewById(R.id.textView_date)).setText(dateString);

            //TextView day = (TextView) rootView.findViewById(R.id.textView_day);



            boolean isMetric = Utility.isMetric(getActivity());
            String high = Utility.formatTemperature(
                    data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), isMetric) + "\u00B0";
            ((TextView) getView().findViewById(R.id.textview_highTemp)).setText(high);
            String low = Utility.formatTemperature(
                    data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), isMetric) + "\u00B0";
            ((TextView) getView().findViewById(R.id.textView_lowTemp)).setText(low);

            ImageView forecastImage = (ImageView) getView().findViewById(R.id.imageView_forecastImage);

            String weatherDescription = data.getString(data.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
            ((TextView) getView().findViewById(R.id.textView_forecastDescription)).setText(weatherDescription);

            TextView humidity = (TextView) getView().findViewById(R.id.textView_humidity);
            TextView pressure = (TextView) getView().findViewById(R.id.textView_pressure);
            TextView wind = (TextView) getView().findViewById(R.id.textView_wind);

            //needed for share intent
            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

            Log.v(LOG_TAG, "Forecast String: " + mForecast);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
