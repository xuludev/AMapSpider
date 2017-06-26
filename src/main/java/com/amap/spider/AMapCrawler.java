package com.amap.spider;

import com.amap.entity.Shop;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LucasX on 2016/5/3.
 * <p>
 * Powered by HttpComponents 4.5
 * </p>
 * <p>
 * 爬取<strong>指定城市</strong>的数据
 * </p>
 */
@SuppressWarnings("ALL")
public class AMapCrawler {

    private static final Logger logger = LogManager.getLogger();
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) " +
            "Chrome/51.0.2700.0 Safari/537.36";
    private static int sleepMs = 5000;

    private List<Shop> crawl(String cityId, String keyword, String geoobj) throws
            UnsupportedEncodingException {

        List<Shop> shopList = new ArrayList<>();
        int pageNumber = 1;
        boolean flag = true;
        String refererUrl = "";

        while (flag) {
            String requestUrl = "http://ditu.amap" +
                    ".com/service/poiInfo?query_type=TQUERY&city=" + cityId + "&keywords=" + URLEncoder
                    .encode(keyword, "UTF-8") +
                    "&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div" +
                    "=PC1000&addr_poi_merge=true&is_classify=true&geoobj=" + URLEncoder
                    .encode(geoobj, "UTF-8");

            switch (pageNumber) {
                case 1:
                    refererUrl = "http://ditu.amap.com/search?query=" + URLEncoder.encode(keyword, "UTF-8") +
                            "&city=" +
                            cityId;
                    break;
                case 2:
                    refererUrl = "http://ditu.amap.com/search?query=" + URLEncoder
                            .encode(keyword, "UTF-8") + "&city=" + cityId;
                    break;
                default:
                    refererUrl = "http://ditu.amap" +
                            ".com/search?query=" + URLEncoder
                            .encode(keyword, "UTF-8") + "&city=" + cityId + "&pagenum=" +
                            (pageNumber - 1);
                    break;
            }

            logger.debug("正在爬取第 " + pageNumber + " 页的数据...");
            logger.debug("请求链接为 " + requestUrl);

            CloseableHttpResponse closeableHttpResponse = null;
            try {
                HttpGet httpGet = new HttpGet(requestUrl);
                httpGet.addHeader("Referer", refererUrl);
                closeableHttpResponse = AMapCrawler.newHttpClient().execute(httpGet);

                if (closeableHttpResponse.getStatusLine().getStatusCode() < 403) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();

                    String responseJson = EntityUtils.toString(httpEntity);

                    JSONObject jsonObject = (JSONObject) JSONValue.parse(responseJson);

                    if ("1".equals(jsonObject.get("status").toString())) {
                        JSONArray jsonArray = (JSONArray) ((JSONObject) (jsonObject.get("data"))).get("poi_list"); //根节点,虽然这里Array Size只有1个...

                        String rating = "", tel = "", cityname = "", address = "", name = "", distance = "",
                                bigType = keyword, smallType = "", price = "";

                        for (int i = 0; i < jsonArray.size() - 1; i++) {
                            JSONObject shopJson = (JSONObject) jsonArray.get(i);
                            if (null != shopJson.get("rating")) {
                                rating = shopJson.get("rating").toString();
                            }
                            if (null != shopJson.get("tel")) {
                                tel = shopJson.get("tel").toString();
                            }
                            if (null != shopJson.get("cityname")) {
                                cityname = shopJson.get("cityname").toString();
                            }
                            if (null != shopJson.get("address")) {
                                address = shopJson.get("address").toString();
                            }
                            if (null != shopJson.get("name")) {
                                name = shopJson.get("name").toString();
                            }
                            if (null != shopJson.get("distance")) {
                                distance = shopJson.get("distance").toString();
                            }
                            if (null != shopJson.get("domain_list")) {
                                JSONArray jsonArrayDomain = (JSONArray) shopJson.get("domain_list");
                                Iterator<JSONObject> jsonArrayDomainIter = jsonArrayDomain.iterator();
                                while (jsonArrayDomainIter.hasNext()) {
                                    JSONObject domain = jsonArrayDomainIter.next();
                                    //element in each domain_list
                                    if (domain.get("id").equals("1006")) {
                                        try {
                                            smallType = Jsoup.parse(domain.get("value").toString()).getElementsByAttributeValue("color", "#999999").first().text();
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (domain.get("id").equals("1001")) {
                                        try {
                                            price = Jsoup.parse(domain.get("value").toString()).getElementsByAttributeValue("color", "#f53623").last().text();
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            Shop shop = new Shop(rating, tel, cityname, address, name, distance, bigType,
                                    smallType,
                                    price);
                            shopList.add(shop);
                            logger.info(shop);

                            outPut(shopList, "../AMapData/武汉——美食.csv");
                        }
                    } else if ("6".equals(jsonObject.get("status").toString())) {
                        sleepMs += 2000; //如果出现too fast,休眠时间就自增2s
                    }
                } else {
                    logger.error("您的请求受到限制...");
                }

            } catch (IOException e) {
                e.printStackTrace();

                return shopList;
            } finally {
                try {
                    if (null != closeableHttpResponse)
                        closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return shopList;
                }
            }

            pageNumber++;

            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return shopList;
    }

    public static void outPut(List<Shop> shopList, String filePath) throws IOException {

        Path path = Paths.get(filePath);
        if (!Files.exists(path.getParent()) || !Files.isDirectory(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder stringBuilder = new StringBuilder("商户,城市,评分,电话,地址,距离,商户大类,商户小类,价格\r\n");
        shopList.forEach(shop -> {
            stringBuilder.append(shop.getName()).append(",").append(shop.getCityname()).append(",").append
                    (shop.getRating()).append(",").append(shop.getTel()).append(",").append(shop.getAddress
                    ()).append(",").append(shop.getDistance()).append(",").append(shop.getBigType()).append
                    (",").append(shop.getSmallType()).append(",").append(shop.getPrice()).append("\r\n");
        });

        Files.write(path, stringBuilder.toString().getBytes("GBK"));

    }

    private static CloseableHttpClient newHttpClient() {

        return HttpClients.custom().setUserAgent(userAgent).build();
    }

    public static void main(String[] args) {
        try {
            new AMapCrawler().crawl("420100", "景点", "114.169482|30.358877|114.647388|30.821104");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
