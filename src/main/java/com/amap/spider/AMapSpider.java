package com.amap.spider;

import com.amap.entity.Config;
import com.amap.entity.Shop;
import com.amap.util.XmlUtil;
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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LucasX on 2016/5/3.
 * <p>
 * Powered by HttpComponents 4.5
 * </p>
 * <p>
 * 爬取<strong>一定范围</strong>内数据
 * </p>
 */
@SuppressWarnings("ALL")
public class AMapSpider {

    private static final Logger logger = LogManager.getLogger();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) " +
            "Chrome/51.0.2700.0 Safari/537.36";
    private static int sleepMs = 10000;

    private List<Shop> crawl(String cityId, String range, String keyword, double latitude, double longtitude) throws
            UnsupportedEncodingException {

        List<Shop> shopList = new ArrayList<>();
        int pageNumber = 1;
        boolean flag = true;
        String refererUrl = "";

        while (flag) {
            String requestUrl = "http://ditu.amap.com/service/poiInfo?query_type=RQBXY&city=" + cityId +
                    "&keywords=" +
                    URLEncoder.encode(keyword, "UTF-8") + "&pagesize=20&pagenum=" + pageNumber +
                    "&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000" +
                    "&div=PC1000&addr_poi_merge=true&is_classify=true&longitude=" + longtitude +
                    "&latitude=" + latitude + "&range=" + range;

            switch (pageNumber) {
                case 1:
                    refererUrl = "http://ditu.amap" +
                            ".com/search?query=%E8%BF%9E%E4%BA%91%E6%B8%AF%E5%B8%82%E4%B8%9C%E6%B5%B7%E5%8E" +
                            "%BF%E7%89%9B%E5%B1%B1%E9%95%87%E7%89%9B%E5%B1%B1%E5%8C%97%E8%B7%AF2%E5%8F%B7" +
                            "&query_type=RQBXY&longitude=" + longtitude +
                            "&latitude=" + latitude + "&city=" + cityId;
                    break;
                case 2:
                    refererUrl = "http://ditu.amap.com/search?query=%E7%BE%8E%E9%A3%9F&city=" + cityId;
                    break;
                default:
                    refererUrl = "http://ditu.amap" +
                            ".com/search?query=%E7%BE%8E%E9%A3%9F&city=" + cityId + "&pagenum=" +
                            (pageNumber - 1);
                    break;
            }

            logger.debug("正在爬取第 " + pageNumber + " 页的数据...");
            logger.debug("请求链接为 " + requestUrl);

            CloseableHttpResponse closeableHttpResponse = null;
            try {
                HttpGet httpGet = new HttpGet(requestUrl);
                httpGet.addHeader("Referer", refererUrl);
                closeableHttpResponse = AMapSpider.newHttpClient().execute(httpGet);

                if (closeableHttpResponse.getStatusLine().getStatusCode() < 403) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();

                    String responseJson = EntityUtils.toString(httpEntity);

                    JSONObject jsonObject = (JSONObject) JSONValue.parse(responseJson);

                    if ("1".equals(jsonObject.get("status").toString())) {
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data"); //根节点,虽然这里Array Size只有1个...


                        JSONArray jsonArray1 = (JSONArray) ((JSONObject) jsonArray.get(0)).get("list");
                        //每一页的商户详情
                        if (jsonArray1.size() <= 1) {
                            flag = false;
                        }

                        String rating = "", tel = "", cityname = "", address = "", name = "", distance = "",
                                bigType = keyword, smallType = "", price = "";

                        for (int i = 0; i < jsonArray1.size() - 1; i++) {
                            JSONObject shopJson = (JSONObject) jsonArray1.get(i);
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
                            if (null != shopJson.get("templateData")) {
                                JSONObject jsonObject1 = (JSONObject) shopJson.get("templateData");
                                if (null != jsonObject1.get("tag")) {
                                    smallType = Jsoup.parse(jsonObject1.get("tag").toString()).text();
                                }
                                if (null != jsonObject1.get("price")) {
                                    price = Jsoup.parse(jsonObject1.get("price").toString()).text();
                                }
                            }

                            Shop shop = new Shop(rating, tel, cityname, address, name, distance, bigType,
                                    smallType,
                                    price);
                            shopList.add(shop);
                            logger.info(shop);

                            outPut(shopList, "../AMapData/酒店.csv");
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

    public static CloseableHttpClient newHttpClient() {

        return HttpClients.custom().setUserAgent(USER_AGENT).build();
    }

    public static void main(String[] args) {
        try {
            List<Config> configList = XmlUtil.readCityXmlConfig
                    ("7c57fac5d671deb6e84fbc34c613d2fc", "config.xml");

            configList.forEach(config -> {
                try {
                    new AMapSpider().crawl(config.getId(), config.getDistance(), config.getType(), Double.parseDouble
                            (config.getLatitude().trim()), Double.parseDouble(config.getLongtitude().trim()));

//                    Thread.sleep(1000 * 60 * 5); //每抓取一个地点的一个类型的数据，让其休眠5min
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
