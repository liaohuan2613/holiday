package com.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MySQLApplicationService {

    private static Connection conn;
    private static AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger runningTime = new AtomicInteger(0);
    private static ReentrantLock lock = new ReentrantLock(true);
    private static Gson gson = new Gson();
    private static final String REMOVAL_URL = "http://47.96.26.149:5002/api/deduplication";
    private static final String DELETE_URL = "http://47.96.26.149:5002/api/redis-operation";
    private static final String KAFKA_URL = "http://203.156.205.101:11303/rest/block/general-analyse-kafka";
    private static List<String> booleanStrList = Arrays.asList("TRUE", "FALSE", "True", "False", "true", "false", "0", "1");

    private static Map<String, Integer> polarMap = new LinkedHashMap<>();


    private static List<String> rsIdList = null;

    private static RestTemplate restTemplate = new RestTemplate();
    private static RestTemplate localRestTemplate = new RestTemplate();


    public static void updateAction(Connection conn) {
        String sql = "select CONTENT_ID,CONTENT_RS_ID,CONTENT_TYP_NAME from log_reader_merge where CONTENT_OPT_TYP = '0' and MSG_TYPE = 'NWS'";
        String updateSql = "update action_count set rs_id = ?,typ_name=? where item_id = ?";
        try {
            Statement statement = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement(updateSql);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String itemId = resultSet.getString("CONTENT_ID");
                String rsId = resultSet.getString("CONTENT_RS_ID");
                String typName = resultSet.getString("CONTENT_TYP_NAME");
                ps.setString(1, rsId);
                ps.setString(2, typName);
                ps.setString(3, itemId);
                ps.addBatch();
                int countNum = count.getAndAdd(1);
                if (countNum % 10000 == 0) {
                    ps.executeBatch();
                }
                System.out.println(countNum);
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDownHtml() {
        try {
            FileOutputStream fos = new FileOutputStream("D:/tmp/tmp_haitong_0_50.html");
            String htmlHeader = "<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>\n";
            fos.write(htmlHeader.getBytes());
            String htmlBody = "<body>" +
//                    "<div id='timeliness' style='width:100%;height:550px;margin: 5% 0;'></div>" +
//                    "<div id='updateCount' style='width:100%;height:550px;margin: 5% 0;'></div>" +
//                    "<div id='updateRate' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<div id='dupNum' style='width:100%;height:550px;margin: 5% 0;'></div>" +
//                    "<div id='dupRate' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<div id='dupRateScatterChart' style='width:100%;height:550px;margin: 5% 0;'></div>" +
//                    "<div id='dayTrend' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<div id='hourTrend' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<div id='weekendTrend' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<div id='workDayTrend' style='width:100%;height:550px;margin: 5% 0;'></div>" +
                    "<script type='text/javascript' src='echarts.js' ></script>\n<script type='text/javascript'>";
//            htmlBody += printTimeliness(getCollection());
//            htmlBody += printUpdateRate(getCollection());
            htmlBody += printDupRate(getCollection());
//            htmlBody += printDayTrend(getCollection());
            htmlBody += printHourTrend(getCollection());
            htmlBody += printAVGHourTrend(getCollection(), "2019-01-27", 2);
            htmlBody += printScatterChart(getCollection());
            htmlBody += "</script></body></html>";
            fos.write(htmlBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Connection getCollection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://203.156.205.101:11306/GJ_PAGE_VIEW?useUnicode=true&characterEncoding=utf8", "root", "password1!");
//                conn = DriverManager.getConnection("jdbc:mysql://10.104.15.109:3306/HAITONG?useUnicode=true&characterEncoding=utf8", "root", "password!");
//                conn = DriverManager.getConnection("jdbc:mysql://203.156.205.101:10906/MINGSHENG?useUnicode=true&characterEncoding=utf8", "root", "password!");
//                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/log_source?useUnicode=true&characterEncoding=utf8",
//                        "root", "");
                System.out.println("[MySQL Client]: MySQL Client create SUCCESS");
            } catch (Exception e) {
                System.out.println("[MySQL Client]: MySQL Client create FAILED");
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void deleteOneDocument(Connection conn, String tableName, String msgId) {
        String sql = "delete from " + tableName + " where MSG_ID = '" + msgId + "'";
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert ERROR! ");
            System.err.println(sql);
        }
    }

    public static void insertOneDocument(Connection conn, String tableName, Map<String, Object> itemMap) {
        String sql = "INSERT INTO " + tableName + "(";
        String valueSql = "VALUES (";
        try {
            Statement statement = conn.createStatement();
            boolean isFirst = true;
            for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                    sql += entry.getKey();
                    valueSql += "'" + entry.getValue() + "'";
                } else {
                    sql += ", " + entry.getKey();
                    if (entry.getValue() == null || "".equals(entry.getValue())) {
                        valueSql += ", " + "null";
                    } else if (booleanStrList.contains(entry.getValue().toString())) {
                        valueSql += ", " + entry.getValue();
                    } else {
                        valueSql += ", '" + entry.getValue().toString().replace("'", "\\'") + "'";
                    }
                }
            }
            sql += ") " + valueSql + ")";
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

    public static int updateDocumentByMsgId(Connection conn, String tableName, Map<String, Object> itemMap, String msgId) {
        String sql = "UPDATE " + tableName + " set ";
        try {
            Statement statement = conn.createStatement();
            boolean isFirst = true;
            for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sql += ", ";
                }
                if (entry.getValue() == null || "".equals(entry.getValue())) {
                    sql += entry.getKey() + " = " + null + "";
                } else if (booleanStrList.contains(entry.getValue().toString())) {
                    sql += entry.getKey() + " = " + entry.getValue() + "";
                } else {
                    sql += entry.getKey() + " = '" + entry.getValue() + "'";
                }
            }
            sql += " where MSG_ID = '" + msgId + "'";
            int update = statement.executeUpdate(sql);
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert SUCCESS! ");
            System.out.println(count.getAndAdd(1));
            return update;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert ERROR! ");
            System.err.println(sql);
        }
        return 0;
    }

    public static void operation(Connection conn, int day) {
        int databaseCount = 5;
        String firstDay, secondDay;
        if (day < 10) {
            firstDay = "2019010" + day;
        } else {
            firstDay = "201901" + day;
        }
        if (day + 2 < 10) {
            secondDay = "2019010" + (day + 2);
        } else {
            secondDay = "201901" + (day + 2);
        }
        String sql = "select MSG_ID, CONTENT_RS_ID, CONTENT_TYP_NAME, CONTENT_TIT, CONTENT_CONT, CONTENT_ENT_TIME," +
                " UNIX_TIMESTAMP(CONTENT_ENT_TIME)-UNIX_TIMESTAMP(CONTENT_PUB_DT) PUB_DT_TO_ENT_TIME " +
                " from log_reader where MSG_TYPE = 'NWS' and CONTENT_OPT_TYP = '0' order by CONTENT_ENT_TIME";
        System.out.println("===============================" + sql);
        Map<String, Integer> databaseMap = new LinkedHashMap<>();

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String rsId = rs.getString("CONTENT_RS_ID") == null ? " " : rs.getString("CONTENT_RS_ID");
                String sendDate = rs.getString("CONTENT_ENT_TIME").split(" ")[0];
                Integer thisDatabase = databaseMap.get(rsId);
                if (thisDatabase == null) {
                    thisDatabase = databaseCount;
                    databaseMap.put(rsId, thisDatabase);
                    databaseCount++;
                }
                String id = rs.getString("MSG_ID");
                String title = rs.getString("CONTENT_TIT") == null ? " " : rs.getString("CONTENT_TIT");
                String content = rs.getString("CONTENT_CONT") == null ? " " : rs.getString("CONTENT_CONT");
                String source = rs.getString("CONTENT_TYP_NAME") == null ? " " : rs.getString("CONTENT_TYP_NAME");
                String entTime = rs.getString("CONTENT_ENT_TIME") == null ? " " : rs.getString("CONTENT_ENT_TIME");
                String pubDtToEntTime = rs.getString("PUB_DT_TO_ENT_TIME") == null ? " " : rs.getString("PUB_DT_TO_ENT_TIME");
                Map<String, Object> requestMap = new LinkedHashMap<>();
                if (id == null || "".equals(id)) {
                    continue;
                }
                requestMap.put("id", id);
                requestMap.put("title", title);
                requestMap.put("content", content);
                requestMap.put("source", source);
                requestMap.put("database", 4);

                Map<String, Object> resultMap;
                Map<String, Object> selfResultMap = null;
                try {
                    lock.lock();
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            + "====================== >>>>> ENTER!!");
                    resultMap = RemovalMap(requestMap);
//                    requestMap.put("database", thisDatabase);
//                    selfResultMap = RemovalMap(requestMap);
                } catch (Exception e) {
                    System.err.println(requestMap);
                    throw new RuntimeException(e);
                } finally {
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            + "====================== >>>>> LEAVE!!");
                    lock.unlock();
                }
                insertDupMsg(id, secondDay, sendDate, entTime, pubDtToEntTime, rsId, resultMap, selfResultMap, day);
            }
            Map<String, Object> deleteMap = new LinkedHashMap<>();
            for (int del = 4; del < databaseCount; del++) {
                deleteMap.put("type", "delete");
                deleteMap.put("database", del);
                restTemplate.postForEntity(DELETE_URL, deleteMap, Object.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertDupMsg(String id, String secondDay, String sendDate, String contentEntTime, String pubDtToEntTime,
                                     String rsId, Map<String, Object> resultMap, Map<String, Object> selfResultMap, int day) {
        System.out.println("day:" + secondDay + "================" + sendDate);
        System.out.println("RUNNING TIME: " + runningTime.getAndAdd(1));
        Map<String, Object> map = new LinkedHashMap<>();
        if (resultMap == null) {
            resultMap = new LinkedHashMap<>();
        }
        if (selfResultMap == null) {
//            selfResultMap = new LinkedHashMap<>();
            selfResultMap = resultMap;
        }

        map.put("ID", UUID.randomUUID().toString());
        map.put("MSG_ID", id);
        map.put("DUP_IDS", resultMap.get("dupIds") == null ? "[]" : resultMap.get("dupIds"));
        map.put("IS_DUP", resultMap.get("isDup") == null ? false : resultMap.get("isDup"));
        map.put("LOCAL_DUP_IDS", selfResultMap.get("dupIds") == null ? "[]" : selfResultMap.get("dupIds"));
        map.put("LOCAL_IS_DUP", selfResultMap.get("isDup") == null ? false : selfResultMap.get("isDup"));
        map.put("CONTENT_ENT_TIME", contentEntTime);
        map.put("PUB_DT_TO_ENT_TIME", pubDtToEntTime);
        map.put("CONTENT_RS_ID", rsId);
        insertOneDocument(conn, "log_refresh_operation_" + day, map);
    }

    private static Map<String, Object> RemovalMap(Map<String, Object> requestMap) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        ResponseEntity<Object> selfResponseEntity = localRestTemplate.postForEntity(REMOVAL_URL, requestMap, Object.class);
        Map<String, Object> selfRemovalMap = gson.fromJson(gson.toJson(selfResponseEntity.getBody()), type);
        return gson.fromJson(gson.toJson(selfRemovalMap.get("result")), type);
    }

    public static String printTimeliness(Connection conn) {
//        String rsIdInSql = getRsIdInSql(conn);
        String sql = "select case  when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < -1800 then '-.05' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= -1800 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 0 then '-0.5 - 0' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 0 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 1800 then '0 - 0.5' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 1800 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 3600 then '0.5 - 1' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 3600 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 5400 then '1 - 1.5'\n" +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 5400 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 7200 then '1.5 - 2' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 7200 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 9000 then '2 - 2.5' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 9000 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 10800 then '2.5 - 3' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 10800 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 12600 then '3 - 3.5' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 12600 " +
                "and UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) < 14400 then '3.5 - 4' " +
                "when UNIX_TIMESTAMP(ENT_DT)-UNIX_TIMESTAMP(PUB_DT) >= 14400 then '4 - 4.5' " +
                "else 'null' end X,SOURCE,count(*) countNum, SOURCE " +
                "from POC_NEWS " +
                "where SOURCE in ('巨潮资讯','新浪财经','证券时报','格隆汇','智通财经','经济通','上海证券报','中国证券报'," +
                "'生意社','商务部','新华社','证券日报','全景网','国际衍生品智库','99期货','人民日报','中财网','红刊财经','21世纪经济报道'," +
                "'外汇中心','上交所','卓创资讯','中国政府网','工信部','深交所') " +
                "group by X,SOURCE " +
                "order by X";
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            Set<String> set = new LinkedHashSet<>();
            Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
            while (rs.next()) {
                String x = rs.getString("X");
                Map<String, Object> map = resultMap.computeIfAbsent(x, k -> new LinkedHashMap<>());
                int countNum = rs.getInt("countNum");
                String rsId = rs.getString("SOURCE");
                map.put(rsId, countNum);
                set.add(rsId);
            }
            return CreateHtmlUtil.createBarGraph("时效性", "", "timeliness", set, resultMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "\n";
    }

    public static String printDupRate(Connection conn) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String rsIdInSql = getRsIdInSql(conn);
        try {
            String sql = "select t1.CONTENT_RS_ID CONTENT_RS_ID, t1.dupNum dupNum, t2.dupNum localDupNum, t3.totalNum totalNum " +
                    " from (select count(*) dupNum,CONTENT_RS_ID from log_operation where IS_DUP = true group by CONTENT_RS_ID) t1," +
                    "(select count(*) dupNum,CONTENT_RS_ID from log_operation where LOCAL_IS_DUP = true group by CONTENT_RS_ID) t2, " +
                    "(select count(*) totalNum,CONTENT_RS_ID from log_operation group by CONTENT_RS_ID) t3 " +
                    "where t1.CONTENT_RS_ID = t2.CONTENT_RS_ID and t2.CONTENT_RS_ID = t3.CONTENT_RS_ID and t1.CONTENT_RS_ID in (" + rsIdInSql + ") ";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            Set<String> numSet = new LinkedHashSet<>();
            numSet.add("总渠道重复量");
//            numSet.add("本渠道重复量");
            numSet.add("数据总量");
            Set<String> set = new LinkedHashSet<>();
            set.add("总渠道重复率");
//            set.add("本渠道重复率");
            Map<String, Map<String, Object>> sourceNumMap = new LinkedHashMap<>();
            Map<String, Map<String, Object>> sourceMap = new LinkedHashMap<>();
            int allTotalNum = 0;
            int allDupNum = 0;
            while (rs.next()) {
                int dupNum = rs.getInt("dupNum");
//                int localDupNum = rs.getInt("localDupNum");
                int totalNum = rs.getInt("totalNum");
                allTotalNum += totalNum;
                allDupNum += dupNum;
                String rsId = rs.getString("CONTENT_RS_ID");
                Map<String, Object> tempNumMap = new LinkedHashMap<>();
                tempNumMap.put("总渠道重复量", dupNum);
//                tempNumMap.put("本渠道重复量", localDupNum);
                tempNumMap.put("数据总量", totalNum);
                sourceNumMap.put(rsId, tempNumMap);
                Map<String, Object> tempMap = new LinkedHashMap<>();
                tempMap.put("总渠道重复率", df.format(dupNum * 100.0 / totalNum));
//                tempMap.put("本渠道重复率", df.format(localDupNum * 100.0 / totalNum));
                sourceMap.put(rsId, tempMap);
            }
            return /*CreateHtmlUtil.createBarGraph("重复率图", "", "dupRate", set, sourceMap) +*/
                    CreateHtmlUtil.createBarGraph("重复数量图", "数据总量："
                            + allTotalNum + "  重复总量：" + allDupNum, "dupNum", numSet, sourceNumMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "\n";
    }


    public static String printUpdateRate(Connection conn) {
        String rsIdInSql = getRsIdInSql(conn);
        DecimalFormat df = new DecimalFormat("######0.00");
        try {
            Map<String, Map<String, Integer>> rsIdMap = new LinkedHashMap<>();
            for (String rsId : Objects.requireNonNull(findAllRsId(conn))) {
                Map<String, Integer> tempMap = new LinkedHashMap<>();
                tempMap.put("0", 0);
                tempMap.put("2", 0);
                rsIdMap.put(rsId, tempMap);
            }
            String sql = "select CONTENT_RS_ID, count(*) countNum, " +
                    " case when CONTENT_OPT_TYP = '0' then '0' " +
                    " else '2' end OPT_TYP " +
                    " from log_reader where MSG_TYPE = 'NWS' and CONTENT_RS_ID in (" + rsIdInSql + ") group by OPT_TYP,CONTENT_RS_ID";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String rsId = rs.getString("CONTENT_RS_ID");
                Map<String, Integer> tempMap = rsIdMap.get(rsId);
                tempMap.put(rs.getString("OPT_TYP"), rs.getInt("countNum"));
            }

            System.out.println("===================== 更新数据量 ================================");
            Set<String> numSet = new LinkedHashSet<>();
            numSet.add("新增数据量");
            numSet.add("更新数据量");
            numSet.add("总数据量");
            Map<String, Map<String, Object>> sourceNumMap = new LinkedHashMap<>();
            for (Map.Entry<String, Map<String, Integer>> entry : rsIdMap.entrySet()) {
                int newCount = entry.getValue().get("0");
                int updateCount = entry.getValue().get("2");
                int totalCount = newCount + updateCount;
                Map<String, Object> tempMap = new LinkedHashMap<>();
                tempMap.put("新增数据量", newCount);
                tempMap.put("更新数据量", updateCount);
                tempMap.put("总数据量", totalCount);
                sourceNumMap.put(entry.getKey(), tempMap);
            }
            Set<String> set = new LinkedHashSet<>();
            set.add("新增比率");
            set.add("更新比率");
            Map<String, Map<String, Object>> sourceRateMap = new LinkedHashMap<>();
            System.out.println("===================== 更新比率 ================================");
            for (Map.Entry<String, Map<String, Integer>> entry : rsIdMap.entrySet()) {
                int newCount = entry.getValue().get("0");
                int updateCount = entry.getValue().get("2");
                int totalCount = newCount + updateCount;
                Map<String, Object> tempMap = new LinkedHashMap<>();
                tempMap.put("新增比率", df.format(newCount * 100.0 / totalCount));
                tempMap.put("更新比率", df.format(updateCount * 100.0 / totalCount));
                sourceRateMap.put(entry.getKey(), tempMap);
            }
            return CreateHtmlUtil.createBarGraph("数据量统计图", "", "updateCount", numSet, sourceNumMap) +
                    CreateHtmlUtil.createBarGraph("更新比率图", "", "updateRate", set, sourceRateMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "\n";
    }

    public static List<String> findAllRsId(Connection conn) {
        String sql = "select CONTENT_RS_ID,count(*) countNum from log_reader where MSG_TYPE = 'NWS' and CONTENT_OPT_TYP = '0' " +
                " group by CONTENT_RS_ID order by countNum desc limit 50";
        if (rsIdList == null || rsIdList.size() == 0) {
            rsIdList = new ArrayList<>();
            try {
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    rsIdList.add(rs.getString("CONTENT_RS_ID"));
                }
                return rsIdList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rsIdList;
    }

    private static String getRsIdInSql(Connection conn) {
        String rsIdInSql = "";
        boolean first = true;
        for (String rsId : findAllRsId(conn)) {
            if (first) {
                first = false;
                rsIdInSql = "'" + rsId + "'";
            } else {
                rsIdInSql += ", '" + rsId + "'";
            }
        }
        return rsIdInSql;
    }

    public static void achieveDataByFormat(Connection conn, List<String> formats, Map<String, String> resultMap) {
        String rsIdInSql = getRsIdInSql(conn);
        DecimalFormat df = new DecimalFormat("########0.00");
        String likeContentSql = "(";
        for (int i = 0; i < formats.size(); i++) {
            if (i == 0) {
                likeContentSql += " CONTENT_ENT_TIME like '" + formats.get(i) + "%' ";
            } else {
                likeContentSql += " or CONTENT_ENT_TIME like '" + formats.get(i) + "%' ";
            }
        }
        likeContentSql += ")";
        String sql = "select count(*) countNum, CONTENT_RS_ID from log_reader where "
                + likeContentSql + " and MSG_TYPE = 'NWS' and CONTENT_OPT_TYP = '0' and CONTENT_RS_ID in (" + rsIdInSql + ") group by CONTENT_RS_ID";
        Map<String, Integer> rsIdMap = new LinkedHashMap<>();
        for (String rsId : Objects.requireNonNull(findAllRsId(conn))) {
            rsIdMap.put(rsId, 0);
        }
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            System.out.println("=====================" + formats + "======================");
            while (rs.next()) {
                int countNum = rs.getInt("countNum");
                String rsId = rs.getString("CONTENT_RS_ID");
                rsIdMap.put(rsId, countNum);
            }
            for (Map.Entry<String, Integer> entry : rsIdMap.entrySet()) {
                String countNumGroup = resultMap.get(entry.getKey());
                if (countNumGroup == null) {
                    countNumGroup = df.format(entry.getValue() * 1.0 / formats.size());
                } else {
                    countNumGroup += ", " + df.format((entry.getValue() * 1.0 / formats.size()));
                }
                resultMap.put(entry.getKey(), countNumGroup);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String printDayTrend(Connection conn) {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-01-27 00:00:00:000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        Map<String, String> resultMap = new LinkedHashMap<>();
        Set<String> legendDataSet = new LinkedHashSet<>(findAllRsId(conn));
        Set<String> xAxisDataSet = new LinkedHashSet<>();
        for (int i = 0; i < 2; i++) {
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<String> list = new ArrayList<>();
            list.add(format);
            achieveDataByFormat(conn, list, resultMap);
            localDateTime = localDateTime.plusDays(1);
            xAxisDataSet.add(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return CreateHtmlUtil.createLineChart("每日数据走势", "", "dayTrend", legendDataSet, xAxisDataSet, resultMap);
    }

    public static String printHourTrend(Connection conn) {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-01-27 00:00:00:000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        Map<String, String> resultMap = new LinkedHashMap<>();
        Set<String> legendDataSet = new LinkedHashSet<>(findAllRsId(conn));
        Set<String> xAxisDataSet = new LinkedHashSet<>();
        for (int i = 0; i < 2; i++) {
            System.out.println("======================" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "==============================");
            for (int j = 0; j < 24; j++) {
                String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                List<String> list = new ArrayList<>();
                list.add(format);
                achieveDataByFormat(conn, list, resultMap);
                xAxisDataSet.add(format);
                localDateTime = localDateTime.plusHours(1);
            }
        }
        System.out.println("\n\n\n");
        return CreateHtmlUtil.createLineChart("每日每小时数据走势", "", "hourTrend", legendDataSet, xAxisDataSet, resultMap);
    }

    public static String printAVGHourTrend(Connection conn, String startDate, String endDate) {
        LocalDate recordDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<String> weekendDateGroup = new ArrayList<>();
        List<String> workDayGroup = new ArrayList<>();
        while (!startDate.equals(endDate)) {
            int value = recordDate.getDayOfWeek().getValue();
            if (value == 6 || value == 7) {
                weekendDateGroup.add(startDate);
            } else {
                workDayGroup.add(startDate);
            }
            recordDate = recordDate.plusDays(1);
            startDate = recordDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return printDate(conn, weekendDateGroup, workDayGroup);
    }

    public static String printAVGHourTrend(Connection conn, String startDate, int daySize) {
        LocalDate recordDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<String> weekendDateGroup = new ArrayList<>();
        List<String> workDayGroup = new ArrayList<>();
        for (int i = 0; i < daySize; i++) {
            int value = recordDate.getDayOfWeek().getValue();
            if (value == 6 || value == 7) {
                weekendDateGroup.add(startDate);
            } else {
                workDayGroup.add(startDate);
            }
            recordDate = recordDate.plusDays(1);
            startDate = recordDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return printDate(conn, weekendDateGroup, workDayGroup);
    }

    private static String printDate(Connection conn, List<String> weekendDateGroup, List<String> workDayGroup) {
        Map<String, String> weekendResultMap = new LinkedHashMap<>();
        Map<String, String> workDayResultMap = new LinkedHashMap<>();
        Set<String> legendDataSet = new LinkedHashSet<>(findAllRsId(conn));
        Set<String> xAxisDataSet = new LinkedHashSet<>();
        for (int j = 0; j < 24; j++) {
            String hour = "";
            if (j < 10) {
                hour = "0";
            }
            hour += j;
            xAxisDataSet.add(hour);
            List<String> weekendFormats = getFormatList(weekendDateGroup, hour);
            List<String> workDayFormats = getFormatList(workDayGroup, hour);
            achieveDataByFormat(conn, weekendFormats, weekendResultMap);
            achieveDataByFormat(conn, workDayFormats, workDayResultMap);
        }
        return CreateHtmlUtil.createLineChart("工作日平均每小时走势", "", "workDayTrend", legendDataSet, xAxisDataSet, workDayResultMap)
                + CreateHtmlUtil.createLineChart("周末平均每小时走势", "", "weekendTrend", legendDataSet, xAxisDataSet, weekendResultMap);
    }

    private static List<String> getFormatList(List<String> dateList, String hour) {
        List<String> dateFormatList = new ArrayList<>();
        for (String date : dateList) {
            date += " " + hour;
            dateFormatList.add(date);
        }
        return dateFormatList;
    }


    public static void insertAutoTags(Connection conn) {
        try {
            boolean isFinish = false;
            int i = 0;
            while (!isFinish) {
                isFinish = true;
                String sql = "select MSG_ID,CONTENT_TIT,CONTENT_CONT,CONTENT_RS_ID from log_reader_merge " +
                        " where CONTENT_OPT_TYP = '0' and MSG_TYPE = 'NWS' limit " + i + ",1000";
                Statement statement = conn.createStatement();
                String insertSql = "insert into log_auto_tags(msg_id, rs_id, title, content, weight, tag_code, tag_name) values(?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    isFinish = false;
                    String msgId = resultSet.getString("MSG_ID");
                    String rsId = resultSet.getString("CONTENT_RS_ID");
                    if (msgId == null || "".equals(msgId) || rsId == null || "".equals(rsId)) {
                        continue;
                    }
                    String title = resultSet.getString("CONTENT_TIT");
                    String content = resultSet.getString("CONTENT_CONT");
                    Map<String, Object> tagPostMap = new LinkedHashMap<>(4);
                    tagPostMap.put("mktCd", "*");
                    tagPostMap.put("title", title);
                    tagPostMap.put("content", content);
                    List<Map<String, Object>> tagList = achieveWeightList(tagPostMap);
                    for (Map<String, Object> tagMap : tagList) {
                        preparedStatement.setString(1, msgId);
                        preparedStatement.setString(2, rsId);
                        preparedStatement.setString(3, title);
                        preparedStatement.setString(4, content);
                        preparedStatement.setDouble(5, Double.valueOf(tagMap.get("weight").toString()));
                        preparedStatement.setString(6, tagMap.get("tagCode") == null ? "" : tagMap.get("tagCode").toString());
                        preparedStatement.setString(7, tagMap.get("tagName") == null ? "" : tagMap.get("tagName").toString());
                        preparedStatement.addBatch();
                        System.out.println("Add Batch....SUCCESS");
                    }
                }
                preparedStatement.executeBatch();
                System.out.println("Execute Batch 1000....SUCCESS");
                i += 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, Object>> achieveWeightList(Map<String, Object> tagPostMap) {
        URI kafkaUri = UriComponentsBuilder.fromUriString(KAFKA_URL).build().encode().toUri();
        Object obj = null;
        try {
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(kafkaUri, tagPostMap, Object.class);
            obj = responseEntity.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> tagList = new ArrayList<>();
        double maxWeight = 0;
        if (obj instanceof Map<?, ?>) {
            Map<String, Object> tagMap = (Map<String, Object>) obj;
            if (tagMap.get("status") != null && "SUCCESS".equals(tagMap.get("status").toString())) {
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) tagMap.get("result");
                for (Map<String, Object> tempMap : mapList) {
                    double thisWeight = tempMap.get("weight") == null ? 0 : Double.valueOf(tempMap.get("weight").toString());
                    if (thisWeight > maxWeight) {
                        maxWeight = thisWeight;
                        tagList.clear();
                        tagList.add(tempMap);
                    } else if (thisWeight == maxWeight) {
                        tagList.add(tempMap);
                    }
                }
            }
        }
        return tagList;
    }

    public static void updateTextPolar(Connection conn) {
        try {
            polarMap.put("NEGATIVE", -1);
            polarMap.put("POSITIVE", 1);
            polarMap.put("NEUTRAL", 0);
            boolean isFinish = false;
            int i = 0;
            while (!isFinish) {
                isFinish = true;
                String sql = "select title,content from click_count limit " + i + ",1000";
                Statement statement = conn.createStatement();
                PreparedStatement preparedStatement = conn.prepareStatement("update click_count " +
                        "set text_polar = ?, title = ?, content = ? where title = ? and content = ?");
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    isFinish = false;
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    Map<String, Object> tagPostMap = new LinkedHashMap<>(4);
                    tagPostMap.put("mktCd", "*");
                    tagPostMap.put("title", title);
                    tagPostMap.put("content", content);
                    preparedStatement.setInt(1, achieveTextPolar(tagPostMap));
                    preparedStatement.setString(2, noHtmlStr(title));
                    preparedStatement.setString(3, noHtmlStr(content));
                    preparedStatement.setString(4, title);
                    preparedStatement.setString(5, content);
                    preparedStatement.addBatch();
                    System.out.println("Add Batch....SUCCESS");
                }
                preparedStatement.executeBatch();
                System.out.println("Execute Batch 1000....SUCCESS");
                i += 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int achieveTextPolar(Map<String, Object> tagPostMap) {
        URI kafkaUri = UriComponentsBuilder.fromUriString(KAFKA_URL).build().encode().toUri();
        Object obj = null;
        try {
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(kafkaUri, tagPostMap, Object.class);
            obj = responseEntity.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        int textPolar = 0;
        if (obj instanceof Map<?, ?>) {
            Map<String, Object> tagMap = (Map<String, Object>) obj;
            if (tagMap.get("status") != null && "SUCCESS".equals(tagMap.get("status").toString())) {
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) tagMap.get("result");
                String tagCode = mapList.get(mapList.size() - 1).get("tagCode").toString();
                textPolar = polarMap.get(tagCode);
                System.out.println("Enter textPolar.....");
            }
        }
        return textPolar;
    }

    public static String noHtmlStr(String value) {
        final String REGEX_HTML_1 = "<[^<>P[/P]p[/p]]+>";
        final String REGEX_HTML_2 = "<!--[^<>]+-->";
        final String REGEX_HTML_3 = "\n|\\n|\\r|\t|\\t|[\\s]+";
        return value.replaceAll(REGEX_HTML_1, " ").replaceAll(REGEX_HTML_2, " ").replaceAll(REGEX_HTML_3, " ");
    }

    public static String printScatterChart(Connection conn) {
        DecimalFormat df = new DecimalFormat("######0.00");
        try {
            Map<String, Integer> rsIdDupNumMap = new HashMap<>();
            Map<String, Integer> rsIdTotalNumMap = new HashMap<>();
            for (String rsId : findAllRsId(conn)) {
                rsIdDupNumMap.put(rsId, 0);
                rsIdTotalNumMap.put(rsId, 0);
            }
            String sql = "select count(*) countNum, CONTENT_RS_ID from log_operation where IS_DUP = true "
                    + " and CONTENT_RS_ID in (" + getRsIdInSql(conn) + ") group by CONTENT_RS_ID ";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String rsId = resultSet.getString("CONTENT_RS_ID");
                int dupNum = resultSet.getInt("countNum");
                rsIdDupNumMap.put(rsId, dupNum);
            }
            String totalSql = "select count(*) countNum, CONTENT_RS_ID from log_operation where "
                    + " CONTENT_RS_ID in (" + getRsIdInSql(conn) + ") group by CONTENT_RS_ID ";
            ResultSet totalResultSet = statement.executeQuery(totalSql);
            while (totalResultSet.next()) {
                String rsId = totalResultSet.getString("CONTENT_RS_ID");
                int dupNum = totalResultSet.getInt("countNum");
                rsIdTotalNumMap.put(rsId, dupNum);
            }
            Map<String, List<Object>> resultMap = new LinkedHashMap<>();
            int maxSize = 0;
            for (String rsId : findAllRsId(conn)) {
                List<Object> tempList = new ArrayList<>();
                int dupNum = rsIdDupNumMap.get(rsId);
                int totalNum = rsIdTotalNumMap.get(rsId);
                String dupRate = df.format(dupNum * 100.0 / totalNum);
                tempList.add(totalNum);
                tempList.add(dupRate);
                tempList.add(totalNum - dupNum);
                tempList.add(dupNum);
                if (totalNum > maxSize) {
                    maxSize = totalNum;
                }
                resultMap.put(rsId, tempList);
            }
            Set<String> itemSet = new LinkedHashSet<>();
            itemSet.add("资讯总量");
            itemSet.add("重复率");
            itemSet.add("有效资讯量");
            itemSet.add("重复资讯量");
            return CreateHtmlUtil.createScatterChart("重复率散点图", "", "dupRateScatterChart",
                    "资讯总量", "重复率", "有效资讯量", itemSet, resultMap, maxSize / 3 + maxSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "\n";
    }


    public static String removeHtmlTag(String text, boolean notRemovePageHtml) {
        final String REGEX_HTML_1;
        if (notRemovePageHtml) {
            REGEX_HTML_1 = "<([^<>p/pP/P]+)>";
        } else {
            REGEX_HTML_1 = "<[^<>]+>";
        }
        final String REGEX_HTML_2 = "<!--[^<>]+-->";
        final String REGEX_HTML_3 = "\n|\\n|\\r|[\\s]+";
        return text.replaceAll(REGEX_HTML_1, " ").replaceAll(REGEX_HTML_2, " ").replaceAll(REGEX_HTML_3, " ");
    }

    public static void main(String[] args) {
//        String sql = "select * from POC_NEWS_0304 where ID = 'NW201901071021057779'";
//        try {
//            Statement statement = getCollection().createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//            Map<String, Object> requestMap = new HashMap<>();
//            while (resultSet.next()) {
//                requestMap.put("id", "5c8325734379bd761d31bfc9");
//                requestMap.put("title", "如何做好读书笔记？");
//                requestMap.put("content", "电影推荐：【绿皮书】\\n\\n「世界上有太多孤独的人害怕先>踏出第一步」\\n\\n\\n\\n\\n\\n传统做读书笔记的方法，" +
//                        "大抵上，可以分为三种：\\n\\n\\n\\n\\n\\n\\n\\n1）关键词。记录下关键词和它的出处和来源；\\n\\n2）摘>录。像我们读书时候经常做得那样，" +
//                        "整个句子、段落、知识点，直接抄录下来\\n\\n3）标注。在原文或者摘录的段落旁，记录下自己的思考和总结\\n\\n\\n\\n\\n\\n这三类方法很常规，" +
//                        "也很符合直觉，但你有没有发现，无论采用哪种方法，都会遇到一个问题：\\n\\n\\n\\n\\n\\n当笔记堆积如山时，你要怎么对他们进>行整理分类呢？" +
//                        "\\n\\n\\n\\n\\n\\n尤其当笔记数量达到成千上万条时，这个问题会更明显。\\n\\n\\n\\n\\n\\n要么，就是堆在一边积灰，再也不去翻看它们；要么>，" +
//                        "就是在需要时，根本找不到，甚至根本不记得「自己做过笔记」，笔记白记了。\\n\\n\\n\\n\\n\\n你或许会说，可以给笔记分类。但本质上，" +
//                        "不仅解决不了>问题，还把问题弄得更复杂了。\\n\\n\\n\\n\\n\\n");
//                requestMap.put("source", "中欧基金");
//            }
//            requestMap.put("database", "9");
//            Type mapType = new TypeToken<Map<String, Object>>() {
//            }.getType();
//            String postForObject = restTemplate.postForObject("http://47.96.26.149:5002/api/deduplication", requestMap, String.class);
//            Map<String, Object> map = gson.fromJson(postForObject, mapType);
//            ArticleDuplicationResponse result = gson.fromJson(map.get("result").toString(), ArticleDuplicationResponse.class);
//            System.out.println(result.getDupIds());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String cont = "<p><br/></p><table border=\"0\" cellspacing=\"1\" cellpadding=\"5\" align=\"center\" class=\"cms_autoformat_table\"><tbody><tr><td>品名</td><td>材质</td><td>价格区间</td><td>单位</td><td>涨跌</td><td>产地/牌号</td><td>发布日期</td><td>备注</td></tr><tr><td><br /></td><td>干净</td><td>37400-37600</td><td>元/吨</td><td>500</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr><tr><td>干净通讯线铜米</td><td>-</td><td>44100-44300</td><td>元/吨</td><td>500</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr><tr><td>1#光亮铜线</td><td>通货</td><td>42300-42500</td><td>元/吨</td><td>500</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr><tr><td>电机线</td><td>一级</td><td>39100-39300</td><td>元/吨</td><td>500</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr><tr><td>黄铜大件</td><td>普通</td><td>31500-31700</td><td>元/吨</td><td>300</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr><tr><td>黄铜水箱</td><td>常规</td><td>27300-27500</td><td>元/吨</td><td>300</td><td>重庆</td><td>2019-01-07</td><td>不含税</td></tr></tbody></table><p class=\"em_media\">（文章来源：全球金属网）</p>";
//        System.out.println(HTMLFormatUtils.filterHtml(cont));
//
//        String parentCode = "C003";
//        String code = "CLS_10101003;XG_A00002;CLS_10700013;CLS_10700012;CLS_10108003;CLS_10108009;CLS_10108008;CLS_10108007;CLS_10108001;CLS_10108006;CLS_10108002;CLS_10108005;CLS_10108011";

        printTimeliness(getCollection());
    }

}
