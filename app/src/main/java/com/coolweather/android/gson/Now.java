package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/12/24 0024.
 */

public class Now
{
    @SerializedName("cond")
    public Condition condition;
    public class Condition
    {
        @SerializedName("txt")
        public String info;
    }
    @SerializedName("tmp")
    public String temperature;
}
