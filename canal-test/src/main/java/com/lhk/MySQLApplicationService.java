package com.lhk;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLApplicationService {

    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
          resetConnection();
        }
        return conn;
    }

    public static Connection resetConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/NEWS_FEED?useUnicode=true&characterEncoding=utf8", "root", "password1!");
            System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
        } catch (Exception e) {
            System.out.println("[MySQL Client]: MySQL Client create FAILED");
            e.printStackTrace();
        }
        return conn;
    }


}
