package com.coolweather.android.db;

/**
 * 县表
 */
public class County {
    private int id;//县id
    private String countyName;//县名称
    private int weatherId;
    private int cityId;//所属市id

    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }


    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
