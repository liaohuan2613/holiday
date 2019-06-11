package com.test.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDeduplication {
    public static void main(String[] args) throws SQLException {
        Connection connection = MySQLApplicationService.getConnection(1);
        for (int i = 0; i < 10; i++) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from POC_NEWS_TEST");
        }
    }
}
