package com.amap.util;

import com.amap.spider.AMapSpider;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;

/**
 * Created by Administrator on 2016/7/8.
 */
public class GeoUtils {

    public static String[] getRestfulLocationInfo(String appKey, String locationName) {

        String[] configAttributes = new String[3];
        String restfulUrl = "http://restapi.amap.com/v3/geocode/geo?key=" + appKey + "&s=rsv3&address=" + locationName;
        CloseableHttpClient closeableHttpClient = AMapSpider.newHttpClient();
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(new HttpGet(restfulUrl));
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            JSONObject jsonObject = (JSONObject) JSONValue.parse(EntityUtils.toString(httpEntity));

            JSONObject jsonObjectInfo = (JSONObject) ((JSONArray) jsonObject.get("geocodes")).get(0);

            String cityId = jsonObjectInfo.get("adcode").toString();
            String locationInfo = jsonObjectInfo.get("location").toString();
            String longtitude = locationInfo.split(",")[0];
            String latitude = locationInfo.split(",")[1];

            configAttributes[0] = cityId;
            configAttributes[1] = longtitude;
            configAttributes[2] = latitude;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configAttributes;
    }
}
