package com.test.parse;

import com.mongodb.client.MongoCollection;
import com.test.MongoDBApplicationService;
import com.test.mysql.MySQLApplicationService;
import com.test.utils.HTMLFormatUtils;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImportOperation {
    private static String importUrl = "http://203.156.205.101:10974/rest/article/import";

    private static RestTemplate restTemplate = new RestTemplate();

    private static AtomicInteger countNum = new AtomicInteger(0);

    public static void main(String[] args) throws SQLException {
        Connection connection = MySQLApplicationService.getConnection(0);
        int size = 3000;
        int page = 27;
        for (int i = 0; i < page; i++) {
            Statement statement = connection.createStatement();
            MongoCollection<Document> collection = MongoDBApplicationService.getCollection("" + i);
            int finalIndex = i;
            new Thread(() -> {
                try {
                    insertDocument(collection, statement, size, finalIndex);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void insertDocument(MongoCollection<Document> collection, Statement statement, int size, int page) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select msg_id, title, body, supplier, publish_time, enter_time, type_name " +
                " from log_data_v1 where enter_time > '2019-04-22' and enter_time < '2019-04-29' limit " + (size * page) + "," + size);
        while (resultSet.next()) {
            System.out.println("==============>>>> this countNum: " + countNum.getAndAdd(1));
            String newsId = resultSet.getString("msg_id");
            String title = resultSet.getString("title");
            String source = resultSet.getString("type_name");
            String supplier = resultSet.getString("supplier");
            String content = HTMLFormatUtils.clearHTMLContent(resultSet.getString("body"));
            String publishTime = resultSet.getString("publish_time").split("\\.")[0];
            String enterTime = resultSet.getString("enter_time").split("\\.")[0];
            URI importUri = UriComponentsBuilder.fromUriString(importUrl).build().encode().toUri();
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("newsId", newsId);
            if (collection.count(new Document("newsId", newsId)) > 0) {
                continue;
            }
            requestMap.put("title", title);
            requestMap.put("source", source);
            requestMap.put("content", content);
            requestMap.put("url", "");
            requestMap.put("platform", "web");
            requestMap.put("publishTime", publishTime);
            requestMap.put("supplier", supplier);
            requestMap.put("tags", new ArrayList<>());
            List<String> similarIds = new ArrayList<>();
            similarIds.add(newsId);
            requestMap.put("similarIds", similarIds);
            requestMap.put("column", -999);
            requestMap.put("createTs", enterTime);
            Object responseBody = restTemplate.postForEntity(importUri, requestMap, Object.class).getBody();
            if (responseBody instanceof Map) {
                String status = ((Map) responseBody).get("status").toString();
                if ("SUCCESS".equals(status)) {
                    collection.updateOne(new Document("newsId", newsId), new Document("$set", new Document("createTs", enterTime)));
                }
            }
        }
    }
}
