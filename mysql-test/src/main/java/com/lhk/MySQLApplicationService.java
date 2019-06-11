package com.lhk;

import com.google.gson.Gson;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MySQLApplicationService {

    private static Connection[] conn = new Connection[100];
    private static Gson gson = new Gson();

    public static Connection getConnection(int thisIndex) {
        if (conn[thisIndex] == null) {
            try {
                conn[thisIndex] = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/deepq_tag?useUnicode=true&characterEncoding=utf8", "root", "password1!");
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
        Connection connection = getConnection(1);
        Statement statement = connection.createStatement();

        String[] categories = new String[]{"STOCK", "INDUSTRY", "NEWS", "REGION", "CONCEPT"};
        for (int j = 0; j < categories.length; j++) {
            Map<String, BlockHotValue> blockHotMap = null;
            Map<String, BlockHotValue> oldBlockHotMap = null;
            for (int i = 1; i < 32; i++) {
                String day;
                if (i < 10) {
                    day = "0" + i;
                } else {
                    day = "" + i;
                }
                LocalDate startDate = LocalDate.parse("2019-05-" + day);
                String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String end = startDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String oldStart = startDate.plusDays(-1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (blockHotMap == null || blockHotMap.size() == 0) {
                    ResultSet resultSet = statement.executeQuery("select block_code, block_name, hot_value from block_hot_statistic_dimension " +
                            " where last_modified_date > '" + start + "' and last_modified_date < '" + end + "' " +
                            " and category = '" + categories[j] + "'");

                    blockHotMap = blockHotResult(resultSet);
                }

                if (oldBlockHotMap == null || oldBlockHotMap.size() == 0) {
                    ResultSet oldResultSet = statement.executeQuery("select block_code, block_name, hot_value from block_hot_statistic_dimension " +
                            " where last_modified_date > '" + oldStart + "' and last_modified_date < '" + start + "' " +
                            " and category = '" + categories[j] + "'");
                    oldBlockHotMap = blockHotResult(oldResultSet);
                }

                PreparedStatement ps = connection.prepareStatement("insert into block_hot_value(code,name,category,hot_weight," +
                        "last_hot_weight,hot_value,hot_target,created_date) values(?,?,?,?,?,?,?,?)");
                for (Map.Entry<String, BlockHotValue> entry : blockHotMap.entrySet()) {
                    double lastHotWeight = oldBlockHotMap.get(entry.getKey()) == null ? 0.0 : oldBlockHotMap.get(entry.getKey()).getHotWeight();
                    entry.getValue().setLastHotWeight(lastHotWeight);
                    entry.getValue().setHotTarget(entry.getValue().getHotWeight() - lastHotWeight);
                    ps.setString(1, entry.getValue().getCode());
                    ps.setString(2, entry.getValue().getName());
                    ps.setString(3, categories[j]);
                    ps.setDouble(4, entry.getValue().getHotWeight());
                    ps.setDouble(5, entry.getValue().getLastHotWeight());
                    ps.setDouble(6, entry.getValue().getHotValue());
                    ps.setDouble(7, entry.getValue().getHotTarget());
                    ps.setString(8, start);
                    ps.addBatch();
                }
                ps.executeBatch();
                oldBlockHotMap.clear();
                oldBlockHotMap.putAll(blockHotMap);
                blockHotMap.clear();
            }

        }
//        System.out.println(blockHotMap);
    }

    private static Map<String, BlockHotValue> blockHotResult(ResultSet resultSet) throws SQLException {
        Map<String, BlockHotValue> resultMap = new HashMap<>();
        int maxValue = Integer.MIN_VALUE;
        int minValue = Integer.MAX_VALUE;
        while (resultSet.next()) {
            BlockHotValue blockHotValue = new BlockHotValue();
            String blockCode = resultSet.getString("block_code");
            String blockName = resultSet.getString("block_name");
            int hotValue = resultSet.getInt("hot_value");
            if (maxValue < hotValue) {
                maxValue = hotValue;
            }
            if (minValue >= hotValue) {
                minValue = hotValue;
            }
            blockHotValue.setCode(blockCode);
            blockHotValue.setName(blockName);
            blockHotValue.setHotValue(hotValue);
            resultMap.put(blockCode, blockHotValue);
        }
        double value = maxValue - minValue;
        for (Map.Entry<String, BlockHotValue> entry : resultMap.entrySet()) {
            double hotWeight = (entry.getValue().getHotValue() - minValue) / value;
            entry.getValue().setHotWeight(hotWeight);
        }
        return resultMap;
    }

}
