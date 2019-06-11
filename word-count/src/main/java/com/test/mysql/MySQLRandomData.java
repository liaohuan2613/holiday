package com.test.mysql;

import com.test.utils.HTMLFormatUtils;

import java.sql.*;

public class MySQLRandomData {
    public static void main(String[] args) throws SQLException {
        Connection connection = MySQLApplicationService.getConnection(1);
        int skip = 10000;
        for (int i = 0; i < 10; i++) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from log_data_v1 limit " + (skip * i) + ",1100");
            PreparedStatement ps = connection.prepareStatement("insert into log_data_v3(msg_id, title, body) values(?,?,?)");
            while (resultSet.next()) {
                String body = HTMLFormatUtils.clearHTMLContent(resultSet.getString("body"));
                if (body.length() > 50) {
                    ps.setString(1, resultSet.getString("msg_id"));
                    ps.setString(2, resultSet.getString("title"));
                    ps.setString(3, body);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }
}
