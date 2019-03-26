package com.lhk.kafka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLApplicationService {

    private static Connection conn;
    private static AtomicInteger count = new AtomicInteger(0);

    public static Connection getCollection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/CLIENT_TEST?useUnicode=true&characterEncoding=utf8",
                        "root", "password!");
                System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
            } catch (Exception e) {
                System.out.println("[MySQL Client]: MySQL Client create FAILED");
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void insertOneDocument(Connection conn, JsonBean jsonBean) {
        String id = UUID.randomUUID().toString();
        Object source = jsonBean.getSource() == null ? "" : jsonBean.getSource();
        Object platform = jsonBean.getPlatform() == null ? "" : jsonBean.getPlatform();
        int index = jsonBean.getUrl().indexOf("/");
        Object urlWebsite = "";
        if (index != -1) {
            urlWebsite = jsonBean.getUrl() == null ? "" : jsonBean.getUrl().substring(0, index);
        }
        long cTime;
        try {
            cTime = jsonBean.getCtime() == null ? 0 : jsonBean.getCtime();
        } catch (Exception e) {
            cTime = 0;
        }
        String publishTime = LocalDateTime.ofEpochSecond(cTime, 0, ZoneOffset.ofHours(8))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String enterTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("-----------------");
        System.out.printf("id = %s, source = %s, platform = %s, urlWebsite = %s, newsCount = %s, publishTime = %s, enterTime = %s \n",
                id, source, platform, urlWebsite, 1, publishTime, enterTime);
        String sql = "INSERT INTO owl_news_count (id, source, url_website, platform, newscount, publishTime, enterTime)" +
                " VALUES ('" + id + "','" + source + "','" + urlWebsite + "','" + platform + "',1,'" + publishTime + "','" + enterTime + "')";
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert SUCCESS! ");
            System.out.println(count.getAndAdd(1));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert ERROR! ");
            System.err.println(sql);
        }
    }

}
