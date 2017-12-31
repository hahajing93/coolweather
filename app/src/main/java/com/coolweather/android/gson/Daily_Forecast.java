package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/12/30 0030.
 */


public class Daily_Forecast
{
    @SerializedName("cond")
    public Condition condition;
    public class Condition
    {
        @SerializedName("txt_d")
        public String info;
    }
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature
    {
        @SerializedName("max")
        public String max;
        @SerializedName("min")
        public String min;
    }
}
