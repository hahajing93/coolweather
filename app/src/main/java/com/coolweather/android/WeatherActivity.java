package com.coolweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Daily_Forecast;
import com.coolweather.android.gson.Daily_Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/12/26 0026.
 */

public class WeatherActivity extends AppCompatActivity
{
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if(Build.VERSION.SDK_INT > 21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        //初始化各种控件
        weatherLayout =  (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_city);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
       String weatherString = defaultSharedPreferences.getString("weather", null);
        String bingPic = defaultSharedPreferences.getString("bing_pic",null);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        if( weatherString != null)
        {
            Log.v("weatherString",weatherString);
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //List<Forecast> forecastList = Utility.handleWeatherResponse2(weatherString);
            //showWeatherInfo(weather,forecastList);
            mWeatherId = weather.basic.weatherId;
            Log.v("information",mWeatherId);
            showWeatherInfo(weather);
        }
        else
        {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather( mWeatherId );
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String defaultSharedPreferencesString = defaultSharedPreferences.getString("weather", null);
                Weather weatherNow = Utility.handleWeatherResponse(defaultSharedPreferencesString);
                String weatherIdNow = weatherNow.basic.weatherId;
                requestWeather(weatherIdNow);
            }
        });
        if(bingPic != null)
        {
            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
        }
        else
        {
            loadBingPic();
        }
    }

    private void loadBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestBingPic,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bingPic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }


    public void requestWeather(final String weatherId)
    {
        //https://free-api.heweather.com/s6/weather/now?location=beijing&key=702297cc638b4d0ea6cfaf5cb9b7cb5e
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=702297cc638b4d0ea6cfaf5cb9b7cb5e";

            HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败1", Toast.LENGTH_LONG ).show();
                            swipeRefresh.setRefreshing(false);
                        }

                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("weather", responseText);
                    // intent.putExtra("weather_id", weatherId);
                    editor.apply();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Log.v("weatherInf",sharedPreferences.getString("weather",null));

                    Log.v("responseText",responseText);

                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    //final List<Forecast> forecastList = Utility.handleWeatherResponse2(responseText);
                    Log.v("weather",weather.basic.update.updateTime);
                    Log.v("weather",weather.basic.cityName);
                    Log.v("weather",weather.now.temperature);
                    Log.v("weather",weather.now.condition.info);
                    Log.v("weather",weather.aqi.city.aqi);
                    Log.v("weather",weather.aqi.city.pm25);
                    //Log.v("weatherforecast",forecastList.get(0).condition.info);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weather != null && "ok".equals(weather.status))
                            {
                                Log.v("weather","返回成功并且有数据");
                                //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                //editor.putString("weather", responseText);
                               // intent.putExtra("weather_id", weatherId);
                                //editor.apply();
                                Log.v("123","准备显示");
                                showWeatherInfo(weather);
                            }
                            else
                            {
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败2", Toast.LENGTH_LONG ).show();
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            });
    }

    private void showWeatherInfo(Weather weather)
    {
        Log.v("showWeatherInfo","1123333333");
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime;
        //String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.condition.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        //forecastLayout.removeAllViews();
        if(weather == null)
        {
            Log.v("weather","weather空");
        }
//        if(weather.forecastList == null)
//        {
//            Log.v("forecastList","forecastList空");
//        }
//        for ( Daily_Forecast forecast : weather.forecastList )
//        {
//           View view =  LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
//            TextView dateText = (TextView)view.findViewById(R.id.date_text);
//            TextView infoText = (TextView) view.findViewById(R.id.info_text);
//             TextView maxText = (TextView) view.findViewById(R.id.max_text);
//            TextView minText = (TextView)view.findViewById(R.id.min_text);
//            dateText.setText(forecast.date);
//            infoText.setText(forecast.condition.info);
//            maxText.setText(forecast.temperature.max);
//            minText.setText(forecast.temperature.min);
//            forecastLayout.addView(view);
//        }
        if(weather.aqi != null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
