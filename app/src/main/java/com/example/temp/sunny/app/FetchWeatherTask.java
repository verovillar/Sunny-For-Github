package com.example.temp.sunny.app;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Veronica on 11/14/14.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private final Context mContext;

    public FetchWeatherTask(Context context) {
        mContext = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        // This will only happen if there was an error getting or parsing the forecast.
        return null;

    }

}
