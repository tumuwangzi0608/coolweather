package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析Json的工具类
 */
public class Utility {
    /**
     * 处理省数据并保存到数据库中
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);//将返回数据解析成JSONArray
                for(int i = 0 ;i < allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);//得到具体某条json对象
                    Province province = new Province();//封装数据
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//保存数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 处理市数据并保存到数据库
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                if(allCity.length()>0){
                    for (int i = 0 ; i <allCity.length(); i++){
                        JSONObject cityObject = allCity.getJSONObject(i);
                        City city = new City();
                        city.setCityName(cityObject.getString("name"));
                        city.setCityCode(cityObject.getInt("id"));
                        city.setProvinceId(provinceId);
                        city.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理县级数据并保存数据库
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty = new JSONArray(response);
                if(allCounty.length()>0){
                    for(int i = 0 ; i <allCounty.length() ; i++){
                        JSONObject countyObject = allCounty.getJSONObject(i);
                        County county = new County();
                        county.setCountyName(countyObject.getString("name"));
                        county.setCityId(countyObject.getInt("id"));
                        county.setWeatherId(countyObject.getString("weather_id"));
                        county.setCityId(cityId);
                        county.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
