package com.example.temp.sunny.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


public class DetailActivity extends ActionBarActivity {

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
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String forecastData;

        public DetailFragment() {
            this.setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate menu resource file
            inflater.inflate(R.menu.detailfragment, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem menuItem;
            menuItem = menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider = /*new ShareActionProvider(getActivity());*/(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
                mShareActionProvider.setShareHistoryFileName("custom_share_history.xml");
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        public Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecastData + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            TextView day = (TextView) rootView.findViewById(R.id.textView_day);
            TextView date = (TextView) rootView.findViewById(R.id.textView_date);

            TextView highTemp = (TextView) rootView.findViewById(R.id.textview_highTemp);
            TextView lowTemp = (TextView) rootView.findViewById(R.id.textView_lowTemp);

            ImageView forecastImage = (ImageView) rootView.findViewById(R.id.imageView_forecastImage);
            TextView forecastDescription = (TextView) rootView.findViewById(R.id.textView_forecastDescription);

            TextView humidity = (TextView) rootView.findViewById(R.id.textView_humidity);
            TextView pressure = (TextView) rootView.findViewById(R.id.textView_pressure);
            TextView wind = (TextView) rootView.findViewById(R.id.textView_wind);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                forecastData = intent.getStringExtra(Intent.EXTRA_TEXT);
                day.setText(forecastData);
            }


            return rootView;
        }
    }
}
