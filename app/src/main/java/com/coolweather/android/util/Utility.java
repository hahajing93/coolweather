package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.util.Log.v;

/**
 * Created by Administrator on 2017/12/23 0023.
 */

public class Utility
{

    /**
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response)
    {
       if (!TextUtils.isEmpty(response))
       {
           try {
               JSONArray allProvinces = new JSONArray(response);
                for(int i = 0;i < allProvinces.length();i++)
                {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getString("id"));
                    province.save();
                }
                return true;
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
       return false;
    }


    /**
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId)
    {
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allCities = new JSONArray(response);
                for(int i = 0;i < allCities.length();i++)
                {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param response
     * @param cityId
     * @return
     */
        public static boolean handleCountyResponse(String response, int cityId)
        {
            if(!TextUtils.isEmpty(response))
            {
                try {
                    JSONArray allCounties = new JSONArray(response);
                    for(int i = 0;i < allCounties.length(); i++)
                    {
                        JSONObject countyObject = allCounties.getJSONObject(i);
                        County county = new County();
                        county.setCountyName(countyObject.getString("name"));
                        county.setWeatherId(countyObject.getString("weather_id"));
                        county.setCityId(cityId);
                        county.save();
                    }
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        //json数据转换为Weather对象
        public static Weather handleWeatherResponse(String response)
        {

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                Log.v("123","123");
                Log.v("123",weatherContent);
                //return new Gson().fromJson(weatherContent,Weather.class);
                return new Gson().fromJson(weatherContent,Weather.class);
//                Gson gson = new Gson();
//                Weather weather = gson.fromJson(weatherContent, Weather.class);
                //return weather;
               // return JSON.parseObject(weatherContent,Weather.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    public static List<Forecast> handleWeatherResponse2(String response)
    {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.v("123","123");
            Log.v("123",weatherContent);
            //return new Gson().fromJson(weatherContent,Weather.class);
            Gson gson = new Gson();
            return gson.fromJson(weatherContent,new TypeToken<List<Forecast>>(){}.getType());
           // return new Gson().fromJson(weatherContent,new Ty);
//                Gson gson = new Gson();
//                Weather weather = gson.fromJson(weatherContent, Weather.class);
            //return weather;
            // return JSON.parseObject(weatherContent,Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
