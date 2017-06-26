package com.amap.entity;

/**
 * Created by LucasX on 2016/5/3.
 */
public class Shop {

    private String rating;
    private String tel;
    private String cityname;
    private String address;
    private String name;
    private String distance;
    private String bigType;
    private String smallType;
    private String price;

    public Shop(String rating, String tel, String cityname, String address, String name, String distance,
                String bigType, String smallType, String price) {
        this.rating = rating;
        this.tel = tel;
        this.cityname = cityname;
        this.address = address;
        this.name = name;
        this.distance = distance;
        this.bigType = bigType;
        this.smallType = smallType;
        this.price = price;
    }

    public Shop() {
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBigType() {
        return bigType;
    }

    public void setBigType(String bigType) {
        this.bigType = bigType;
    }

    public String getSmallType() {
        return smallType;
    }

    public void setSmallType(String smallType) {
        this.smallType = smallType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "rating='" + rating + '\'' +
                ", tel='" + tel + '\'' +
                ", cityname='" + cityname + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", distance='" + distance + '\'' +
                ", bigType='" + bigType + '\'' +
                ", smallType='" + smallType + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
