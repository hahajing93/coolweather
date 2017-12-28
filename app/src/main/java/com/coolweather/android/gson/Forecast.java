package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class Forecast
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
        public String max;
        public String min;
    }
}
