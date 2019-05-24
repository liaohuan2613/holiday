package com.lhk;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;

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

    public static void clearConnection(int thisIndex) {
        conn[thisIndex] = null;
    }

    public static void main(String[] args) throws Exception {
    }

}
