package com.test;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLReader {

    private static Gson gson = new Gson();

    private static AtomicInteger ai = new AtomicInteger(0);

    private static RestTemplate[] templates = new RestTemplate[22];

    public static void main(String[] args) throws SQLException {
        int size = 5000;
        URI tagUri = UriComponentsBuilder.fromUriString("http://47.101.222.163:7003/rest/block/general-analyse-recommend").build().encode().toUri();
        for (int i = 0; i < 22; i++) {
            int finalIndex = i;
            new Thread(() -> {
                templates[finalIndex] = new RestTemplate();
                Connection conn = MySQLApplicationService.getCollection(finalIndex);
                Statement statement = null;
                try {
                    statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery("select newsId, title from article_log limit " + (finalIndex * size) + ", " + size);
                    String insertSql = "insert into log_title_tag(title_tags_count,title_stock_count,title_concept_count,title_news_count,title_industry_count," +
                            "title_region_count,msg_id) values(?,?,?,?,?,?,?)";
                    PreparedStatement ps = conn.prepareStatement(insertSql);
                    int count = 0;
                    while (resultSet.next()) {
                        ai.addAndGet(1);
                        count++;
                        String newsId = resultSet.getString("newsId");
                        String title = resultSet.getString("title");
                        String content = "";
                        String mktCd = "*";
                        Map<String, Object> requestMap = new HashMap<>();
                        requestMap.put("mktCd", mktCd);
                        requestMap.put("title", title);
                        requestMap.put("content", content);
                        Object response = templates[finalIndex].postForEntity(tagUri, requestMap, Object.class).getBody();
                        int newsCount = 0;
                        int stockCount = 0;
                        int conceptCount = 0;
                        int industryCount = 0;
                        int regionCount = 0;
                        int totalCount = 0;
                        if (response instanceof Map) {
                            Map<String, Map<String, List<Map<String, Object>>>> listMap = (Map<String, Map<String, List<Map<String, Object>>>>) response;
                            List<Map<String, Object>> autoTags = listMap.get("result").get("autoTags");
                            totalCount = autoTags.size();
                            for (Map<String, Object> autoTag : autoTags) {
                                String category = (String) autoTag.get("category");
                                switch (category) {
                                    case "STOCK":
                                        stockCount++;
                                        break;
                                    case "INDUSTRY":
                                        industryCount++;
                                        break;
                                    case "NEWS":
                                        newsCount++;
                                        break;
                                    case "REGION":
                                        regionCount++;
                                        break;
                                    case "CONCEPT":
                                        conceptCount++;
                                        break;
                                    default:
                                }
                            }
                            listMap = null;
                        }
                        ps.setInt(1, totalCount);
                        ps.setInt(2, stockCount);
                        ps.setInt(3, conceptCount);
                        ps.setInt(4, newsCount);
                        ps.setInt(5, industryCount);
                        ps.setInt(6, regionCount);
                        ps.setString(7, newsId);
                        ps.addBatch();
                        System.out.println("====================>>" + ai.get());
                        if (count % 100 == 0) {
                            ps.executeBatch();
                            System.out.println("commit ====================>>");
                        }
                    }
                    ps.executeBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).start();

        }

//        Type listType = new TypeToken<List<String>>() {
//        }.getType();
//        String sql = "insert into log_data(msg_id,title,body,title_length,body_length,supplier,type_name,publish_time,enter_time,enter_delay," +
//                "tags_count,tag_category_count,stock_tag_count,concept_tag_count,news_tag_count,industry_tag_count,region_tag_count,emotion," +
//                "first_tag_category,show_tags)" +
//                "values(?,?,?,?,?,?,?,?,?,?," +
//                "?,?,?,?,?,?,?,?,?,?)";
//        PreparedStatement ps = MySQLApplicationService.getCollection(0).prepareStatement(sql);
//
//        int count = 0;
//        while (resultSet.next()) {
//            count++;
//            String newsId = resultSet.getString("newsId");
//            String title = resultSet.getString("title");
//            String content = resultSet.getString("content");
//            String supplier = resultSet.getString("supplier");
//            String source = resultSet.getString("source");
//            String publishTime = resultSet.getString("date") + " " + resultSet.getString("time");
//            String enterTime = resultSet.getString("createTs");
//            long enterDelay = LocalDateTime.parse(enterTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")).toEpochSecond(ZoneOffset.UTC) -
//                    LocalDateTime.parse(publishTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(ZoneOffset.UTC);
//            String stockCodes = resultSet.getString("stockCodes");
//            String showTags = resultSet.getString("showTags");
//            String industryCodes = resultSet.getString("industryCodes");
//            String conceptCodes = resultSet.getString("conceptCodes");
//            String newsCodes = resultSet.getString("newsCodes");
//            String regionCodes = resultSet.getString("regionCodes");
//
//            String emotion = resultSet.getString("nature");
//
//            switch (emotion) {
//                case "Positive":
//                    emotion = "正面";
//                    break;
//                case "Negative":
//                    emotion = "负面";
//                    break;
//                default:
//                    emotion = "中性";
//            }
//
//            String showGroup = "";
//            String[] showTagGroup = showTags.split("\"tagName\":\"");
//            String category = "OTHER";
//            boolean isFirst = true;
//            if (showTagGroup.length > 1) {
//                category = showTags.split("\"category\":\"")[1].split("\",\"tagCode\"")[0];
//                for (int i = 1; i < showTagGroup.length; i++) {
//                    if (isFirst) {
//                        isFirst = false;
//                        showGroup = showTagGroup[i].split("\",\"source\"")[0];
//                    } else {
//                        showGroup += "|" + showTagGroup[i].split("\",\"source\"")[0];
//                    }
//                }
//            }
//
//            int tagsCount = 0;
//            int tagCategoryCount = 0;
//            List<String> stockList;
//            try {
//                stockList = gson.fromJson(stockCodes, listType);
//            } catch (Exception e) {
//                stockList = new ArrayList<>();
//            }
//            if (stockList.size() > 0) {
//                tagsCount += stockList.size();
//                tagCategoryCount++;
//            }
//
//            List<String> industryList;
//            try {
//                industryList = gson.fromJson(industryCodes, listType);
//            } catch (Exception e) {
//                industryList = new ArrayList<>();
//            }
//
//            if (industryList.size() > 0) {
//                tagsCount += industryList.size();
//                tagCategoryCount++;
//            }
//
//            List<String> conceptList;
//            try {
//                conceptList = gson.fromJson(conceptCodes, listType);
//            } catch (Exception e) {
//                conceptList = new ArrayList<>();
//            }
//
//            if (conceptList.size() > 0) {
//                tagsCount += conceptList.size();
//                tagCategoryCount++;
//            }
//
//            List<String> newsList;
//            try {
//                newsList = gson.fromJson(newsCodes, listType);
//            } catch (Exception e) {
//                newsList = new ArrayList<>();
//            }
//
//            if (newsList.size() > 0) {
//                tagsCount += newsList.size();
//                tagCategoryCount++;
//            }
//
//            List<String> regionList;
//            try {
//                regionList = gson.fromJson(regionCodes, listType);
//            } catch (Exception e) {
//                regionList = new ArrayList<>();
//            }
//
//            if (regionList.size() > 0) {
//                tagsCount += regionList.size();
//                tagCategoryCount++;
//            }
//
//            ps.setString(1, newsId);
//            ps.setString(2, title);
//            ps.setString(3, content);
//            ps.setInt(4, title.length());
//            ps.setInt(5, content.length());
//            ps.setString(6, supplier);
//            ps.setString(7, source);
//            ps.setString(8, publishTime);
//            ps.setString(9, enterTime);
//            ps.setLong(10, enterDelay);
//            ps.setInt(11, tagsCount);
//            ps.setInt(12, tagCategoryCount);
//            ps.setInt(13, stockList.size());
//            ps.setInt(14, conceptList.size());
//            ps.setInt(15, newsList.size());
//            ps.setInt(16, industryList.size());
//            ps.setInt(17, regionList.size());
//            ps.setString(18, emotion);
//            ps.setString(19, category);
//            ps.setString(20, showGroup);
//            ps.addBatch();
//
//            if (count % 1000 == 0) {
//                ps.executeBatch();
//            }
//        }
//        ps.executeBatch();
    }

}
