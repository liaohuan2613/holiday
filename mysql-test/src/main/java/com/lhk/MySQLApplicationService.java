package com.lhk;

import com.google.gson.Gson;

import java.sql.*;

public class MySQLApplicationService {

    private static Connection[] conn = new Connection[100];
    private static Gson gson = new Gson();

    public static Connection getConnection(int thisIndex) {
        if (conn[thisIndex] == null) {
            try {
                conn[thisIndex] = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/article?useUnicode=true&characterEncoding=utf8", "root", "password1!");
                System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
            } catch (Exception e) {
                System.out.println("[MySQL Client]: MySQL Client create FAILED");
                e.printStackTrace();
            }
        }
        return conn[thisIndex];
    }

    public static void main(String[] args) throws Exception {
        Connection conn = getConnection(0);
        Statement statement = conn.createStatement();
        new Thread(() -> {
            for (int i = 0; i < 70; i++) {
                ResultSet resultSet = null;
                try {
                    Thread.sleep(5 * 1000);
                    resultSet = statement.executeQuery("select * from article limit 1");
                    while (resultSet.next()) {
                        System.out.println("select * from article! ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
