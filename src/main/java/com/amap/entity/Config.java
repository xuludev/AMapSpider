package com.amap.entity;

/**
 * Created by Administrator on 2016/6/1.
 */
public class Config {

    private String id;
    private String distance;
    private String latitude;
    private String longtitude;
    private String type;

    public Config(String id, String distance, String latitude, String longtitude, String type) {
        this.id = id;
        this.distance = distance;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id='" + id + '\'' +
                ", distance='" + distance + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longtitude='" + longtitude + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
