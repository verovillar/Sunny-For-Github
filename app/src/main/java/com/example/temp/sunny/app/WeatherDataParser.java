package com.example.temp.sunny.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davillar on 9/25/2014.
 */
public class WeatherDataParser {
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
        throws JSONException {

        JSONObject weather = new JSONObject(weatherJsonStr);
        JSONArray daysList = weather.getJSONArray("list");
        JSONObject requestedDay = daysList.getJSONObject(dayIndex);
        JSONObject temperatureInfo = requestedDay.getJSONObject("temp");
        return temperatureInfo.getDouble("max");
    }
}
