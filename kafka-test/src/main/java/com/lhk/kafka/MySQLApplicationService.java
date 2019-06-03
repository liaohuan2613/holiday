package com.lhk.kafka;

import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLApplicationService {

    private static Connection gbkConn;
    private static Connection utfConn;
    private static AtomicInteger count = new AtomicInteger(0);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20,
            0, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static void main(String[] args) throws SQLException {
        Connection utfConn = getUtfConnection();
        int count = 0;
        int size = 1000;
        int totalSize = 715783 + 1000;
        while (count * size < totalSize) {
            int finalCount = count;
            executor.execute(() -> {
                try {
                    Statement statement = utfConn.createStatement();
                    ResultSet realResultSet = statement.executeQuery("select item_id, content from owl limit " + (finalCount * size) + "," + size);
                    PreparedStatement ps = utfConn.prepareStatement("update owl set content = ? where item_id = ? ");
                    Map<String, String> itemIdContentMap = new HashMap<>();

                    while (realResultSet.next()) {
                        String itemId = realResultSet.getString("item_id");
                        String content = realResultSet.getString("content");
                        itemIdContentMap.put(itemId, content);
                    }
                    for (Map.Entry<String, String> entry : itemIdContentMap.entrySet()) {
                        ps.setString(1, HTMLFormatUtils.filterHtml(entry.getValue()));
                        ps.setString(2, entry.getKey());
                        System.out.println("======================>>" + entry.getKey());
                        ps.addBatch();
                    }
                    ps.executeUpdate();
                    System.out.println("commit ======" + finalCount * size);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            count++;
        }
    }

    public static Connection getGbkConnection() {
        if (gbkConn == null) {
            try {
                gbkConn = DriverManager.getConnection("jdbc:mysql://203.156.205.101:11706/pubnews?useUnicode=true&characterEncoding=GBK",
                        "root", "Mdrz#F(K14(oLcsVd^cH");
                System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
            } catch (Exception e) {
                System.out.println("[MySQL Client]: MySQL Client create FAILED");
                e.printStackTrace();
            }
        }
        return gbkConn;
    }

    public static Connection getUtfConnection() {
        if (utfConn == null) {
            try {
                utfConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pubnews?useUnicode=true&characterEncoding=utf8",
                        "root", "Mdrz#F(K14(oLcsVd^cH");
                System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
            } catch (Exception e) {
                System.out.println("[MySQL Client]: MySQL Client create FAILED");
                e.printStackTrace();
            }
        }
        return utfConn;
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
        String sql = "INSERT INTO owl_news_count (id, source, url_website, platform, newscount, publish_time, enter_time)" +
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
