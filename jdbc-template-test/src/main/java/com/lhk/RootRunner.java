package com.lhk;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class RootRunner {
    public static void main(String[] args) {
        

        TableOperation tableOperation = new TableOperation();
        tableOperation.setSyncDatetimeFiled("to_char(b,'')");
        List<String> clientPrimaryFiledList = new ArrayList<>();
        clientPrimaryFiledList.add("a");
        tableOperation.setClientPrimaryFiledList(clientPrimaryFiledList);
        List<String> cloudPrimaryFiledList = new ArrayList<>();
        cloudPrimaryFiledList.add("a");
        tableOperation.setCloudPrimaryFiledList(cloudPrimaryFiledList);
        tableOperation.setModel(2);
        tableOperation.setRemoveFiledList(new ArrayList<>());
        tableOperation.setReplaceFromFiledList(new ArrayList<>());
        tableOperation.setReplaceToFiledList(new ArrayList<>());
        tableOperation.setTableName("test_all_type");

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("oracle.jdbc.OracleDriver");
        hikariConfig.setJdbcUrl("jdbc:oracle:thin:@192.168.11.88:1521:orcl");
        hikariConfig.setUsername("grcloud");
        hikariConfig.setPassword("741UaZJh1yK26h9JgR*I");
//        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
//        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/deepq_tag?useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true");
//        hikariConfig.setUsername("root");
//        hikariConfig.setPassword("password1!");
        jdbcTemplate.setDataSource(new HikariDataSource(hikariConfig));
//        jdbcTemplate.batchUpdate("insert into test_all_type(a,b,c) values(?,?,?)", new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
//                preparedStatement.setObject(1, "10");
//                preparedStatement.setTimestamp(2, new Timestamp(1559207601000L));
//                preparedStatement.setTimestamp(3, new Timestamp(1559207601000L));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return 1;
//            }
//        });
        List<Map<String, Object>> allTypeMapList = jdbcTemplate.queryForList("select * from test_all_type");
        Map<String, Object> testMap = new LinkedHashMap<>();
        testMap.put("a", "3");
        testMap.put("b", "2019-02-01 00:00:00");
        testMap.put("c", "02:00:00");
        allTypeMapList.add(testMap);

        List<String> cloudPrimaryKeyList = tableOperation.getCloudPrimaryFiledList();
        List<String> removeKeyList = tableOperation.getRemoveFiledList();
        removeKeyList.add("b");
        removeKeyList.add("c");
        List<String> syncFiledList = tableOperation.getSyncFiledList();

        // 这个是所有的字段，但是顺序会有问题
        List<String> syncFiledNoPrimaryList = new ArrayList<>(allTypeMapList.get(0).keySet());
        syncFiledNoPrimaryList.removeAll(removeKeyList);
        syncFiledNoPrimaryList.removeAll(cloudPrimaryKeyList);

        syncFiledList.addAll(syncFiledNoPrimaryList);
        syncFiledList.addAll(cloudPrimaryKeyList);
        String updateSql = parseUpdateSql(tableOperation.getTableName(), syncFiledNoPrimaryList, cloudPrimaryKeyList);

        int[] updateIndexGroup;
        if (tableOperation.getModel() == 1) {
            updateIndexGroup = batchOperation(jdbcTemplate, allTypeMapList, updateSql, syncFiledList);
            clearList(allTypeMapList, updateIndexGroup);

            String checkedSetSql = parseUpdateSql(tableOperation.getTableName(), clientPrimaryFiledList, clientPrimaryFiledList);
            System.out.println(checkedSetSql);
            List<String> doubleClientPrimaryFiledList = new ArrayList<>(clientPrimaryFiledList);
            doubleClientPrimaryFiledList.addAll(clientPrimaryFiledList);
            updateIndexGroup = batchOperation(jdbcTemplate, allTypeMapList, checkedSetSql, doubleClientPrimaryFiledList);
            clearList(allTypeMapList, updateIndexGroup);
        } else if (tableOperation.getModel() == 2) {
            Set<String> deleteDateSet = new HashSet<>();
            allTypeMapList.forEach(allTypeMap -> deleteDateSet.add("%" + allTypeMap.get(tableOperation.getSyncDatetimeFiled())
                    .toString().split(" ")[0] + "%"));
            List<String> deleteDateList = new ArrayList<>(deleteDateSet);
            String sql = "delete from " + tableOperation.getTableName() + " where " + tableOperation.getSyncDatetimeFiled() + " like ?";
            batchDeleteOperation(jdbcTemplate, deleteDateList, sql);
        } else {
            updateIndexGroup = batchOperation(jdbcTemplate, allTypeMapList, updateSql, syncFiledList);
            clearList(allTypeMapList, updateIndexGroup);
        }

        String insertSql = parseInsertSql(tableOperation.getTableName(), syncFiledList);
        int[] insertIndexList = batchOperation(jdbcTemplate, allTypeMapList, insertSql, syncFiledList);
        System.out.println(Arrays.toString(insertIndexList));
        jdbcTemplate.execute("insert into sync_in_status() values(?,?,?)");
    }

    private static void clearList(List list, int[] removeIndexList) {
        int index = 0;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (removeIndexList[index++] == 1) {
                iterator.remove();
            }
        }
    }

    private static String parseUpdateSql(String tableName, List<String> setFiledList, List<String> valueFiledList) {
        StringBuilder updateSetSql = new StringBuilder("update " + tableName + " set ");
        boolean isFirst = true;
        for (String clientFiled : setFiledList) {
            if (isFirst) {
                updateSetSql.append(clientFiled).append(" = ?");
                isFirst = false;
            } else {
                updateSetSql.append(", ").append(clientFiled).append(" = ?");
            }
        }
        StringBuilder updateWhereSql = new StringBuilder();
        isFirst = true;
        for (String primaryKey : valueFiledList) {
            if (isFirst) {
                updateWhereSql.append(" where ").append(primaryKey).append(" = ?");
                isFirst = false;
            } else {
                updateWhereSql.append(" and ").append(primaryKey).append(" = ?");
            }
        }

        return updateSetSql.append(" ").append(updateWhereSql).toString();
    }

    private static String parseInsertSql(String tableName, List<String> keyList) {
        StringBuilder insertSql = new StringBuilder("insert into " + tableName + "(");
        StringBuilder valueSql = new StringBuilder(" values(");
        boolean isFirst = true;
        for (String keyStr : keyList) {
            if (isFirst) {
                insertSql.append(keyStr);
                valueSql.append("?");
                isFirst = false;
            } else {
                insertSql.append(", ").append(keyStr);
                valueSql.append(", ?");
            }
        }
        return insertSql.append(") ").append(valueSql).append(")").toString();
    }

    private static int[] batchOperation(JdbcTemplate jdbcTemplate, List<Map<String, Object>> mapList, String sql, List<String> updateList) {
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                int keyCount = 0;
                for (String key : updateList) {
                    preparedStatement.setString(++keyCount, mapList.get(i).get(key).toString());
                }
            }

            @Override
            public int getBatchSize() {
                return mapList.size();
            }
        });
    }

    private static int[] batchDeleteOperation(JdbcTemplate jdbcTemplate, List<String> dateSet, String sql) {
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, dateSet.get(i));
            }

            @Override
            public int getBatchSize() {
                return dateSet.size();
            }
        });
    }
}
