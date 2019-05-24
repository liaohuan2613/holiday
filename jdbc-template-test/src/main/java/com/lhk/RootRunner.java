package com.lhk;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RootRunner {
    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/deepq_tag?useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("password1!");
        jdbcTemplate.setDataSource(new HikariDataSource(hikariConfig));
        List<Map<String, Object>> allTypeMapList = jdbcTemplate.queryForList("select * from test_all_type limit 1000");
        Map<String, Object> testMap = new LinkedHashMap<>();
        testMap.put("a", "2");
        testMap.put("b", "2019-01-01");
        testMap.put("c", "01:00:00");
        allTypeMapList.add(testMap);

        StringBuilder sql = new StringBuilder("update test_all_type set ");
        Set<String> primaryKeySet = new LinkedHashSet<>();
        Set<String> removeKeySet = new LinkedHashSet<>();
        removeKeySet.add("b");

        LinkedHashSet<String> updateSet = new LinkedHashSet<>();

        primaryKeySet.add("a");
        boolean isFirst = true;
        Set<String> valueSet = allTypeMapList.get(0).keySet();
        for (String key : valueSet) {
            if (!removeKeySet.contains(key)) {
                if (!primaryKeySet.contains(key)) {
                    updateSet.add(key);
                    if (isFirst) {
                        sql.append(key).append(" = ?");
                        isFirst = false;
                    } else {
                        sql.append(", ").append(key).append(" = ?");
                    }
                }
            }
        }
        StringBuilder whereSql = new StringBuilder();
        isFirst = true;
        for (String primaryKey : primaryKeySet) {
            updateSet.add(primaryKey);
            if (isFirst) {
                whereSql.append(" where ").append(primaryKey).append(" = ?");
                isFirst = false;
            } else {
                whereSql.append(" and ").append(primaryKey).append(" = ?");
            }
        }
        sql.append(" ").append(whereSql);
        System.out.println(sql);


        StringBuilder insertSql = new StringBuilder("insert into test_all_type(");
        StringBuilder valueSql = new StringBuilder(" values(");
        isFirst = true;
        for (String updateStr : updateSet) {
            if (isFirst) {
                insertSql.append(updateStr);
                valueSql.append("?");
                isFirst = false;
            } else {
                insertSql.append(", ").append(updateStr);
                valueSql.append(", ?");
            }
        }
        insertSql.append(") ").append(valueSql).append(")");

        System.out.println(insertSql);

        int[] updateIndexList = batchOperation(jdbcTemplate, allTypeMapList, sql.toString(), updateSet);
        List<Map<String, Object>> insertList = new ArrayList<>();
        for (int i = 0; i < updateIndexList.length; i++) {
            if (updateIndexList[i] == 0) {
                insertList.add(allTypeMapList.get(i));
            }
        }

        int[] insertIndexList = batchOperation(jdbcTemplate, insertList, insertSql.toString(), updateSet);
        System.out.println(Arrays.toString(insertIndexList));
    }

    private static int[] batchOperation(JdbcTemplate jdbcTemplate, List<Map<String, Object>> mapList, String sql, LinkedHashSet<String> updateSet) {
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                int keyCount = 0;
                for (String key : updateSet) {
                    preparedStatement.setString(++keyCount, mapList.get(i).get(key).toString());
                }
            }

            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
    }
}
