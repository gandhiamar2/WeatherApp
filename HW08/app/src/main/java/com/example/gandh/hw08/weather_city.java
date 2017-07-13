package com.example.gandh.hw08;

import com.ocpsoft.pretty.time.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gandh on 4/5/2017.
 */

public class weather_city {

    String time, text, icon, temp_c, temp_f;
    String w_date, w_temp_min_c,w_temp_max_c,w_temp_min_f,w_temp_max_f,w_day_cond,w_night_cond,more_data,day_image,night_image,fbase_key;
    boolean fav = false,favourite;
    String citykey,cityname, country, temperature, preety_time,date;
    PrettyTime pt = new PrettyTime();

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public String getFbase_key() {
        return fbase_key;
    }

    public void setFbase_key(String fbase_key) {
        this.fbase_key = fbase_key;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    @Override

    public String toString() {
        return "weather_city{" +
                "w_date='" + w_date + '\'' +
                ", w_temp_min_c='" + w_temp_min_c + '\'' +
                ", w_temp_max_c='" + w_temp_max_c + '\'' +
                ", w_temp_min_f='" + w_temp_min_f + '\'' +
                ", w_temp_max_f='" + w_temp_max_f + '\'' +
                ", w_day_cond='" + w_day_cond + '\'' +
                ", w_night_cond='" + w_night_cond + '\'' +
                ", more_data='" + more_data + '\'' +
                ", day_image='" + day_image + '\'' +
                ", night_image='" + night_image + '\'' +
                '}';
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("favourite",fav);
        result.put("temperature",getW_temp_min_c());
        result.put("date",getCurrentTimeStamp());
        return  result;
    }

    public String getW_date() {
        return w_date;
    }

    public void setW_date(String w_date) {
        this.w_date = w_date;
    }

    public String getW_temp_min_c() {
        return w_temp_min_c;
    }

    public void setW_temp_min_c(String w_temp_min_c) {
        this.w_temp_min_c = w_temp_min_c;
    }

    public String getW_temp_max_c() {
        return w_temp_max_c;
    }

    public void setW_temp_max_c(String w_temp_max_c) {
        this.w_temp_max_c = w_temp_max_c;
    }

    public String getW_temp_min_f() {
        return w_temp_min_f;
    }

    public void setW_temp_min_f(String w_temp_min_f) {
        this.w_temp_min_f = w_temp_min_f;
    }

    public String getW_temp_max_f() {
        return w_temp_max_f;
    }

    public void setW_temp_max_f(String w_temp_max_f) {
        this.w_temp_max_f = w_temp_max_f;
    }

    public String getW_day_cond() {
        return w_day_cond;
    }

    public void setW_day_cond(String w_day_cond) {
        this.w_day_cond = w_day_cond;
    }

    public String getW_night_cond() {
        return w_night_cond;
    }

    public void setW_night_cond(String w_night_cond) {
        this.w_night_cond = w_night_cond;
    }

    public String getMore_data() {
        return more_data;
    }

    public void setMore_data(String more_data) {
        this.more_data = more_data;
    }


    public String getDay_image() {
        return day_image;
    }

    public void setDay_image(String day_image) {
        this.day_image = day_image;
    }

    public String getNight_image() {
        return night_image;
    }

    public void setNight_image(String night_image) {
        this.night_image = night_image;
    }

    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp_c() {
        return temp_c;
    }

    public void setTemp_c(String temp_c) {
        this.temp_c = temp_c;
    }

    public String getTemp_f() {
        return temp_f;
    }

    public void setTemp_f(String temp_f) {
        this.temp_f = temp_f;
    }
}
