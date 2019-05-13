package com.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FirstLogReader {

    private static AtomicInteger countNum = new AtomicInteger();

    private static Gson gson = new Gson();
//    static String[] originStrGroup = new String[]{"MSG_ID", "CONTENT_ID", "CONTENT_TIT", "CONTENT_CONT", "CONTENT_AUTO_TAGS", "CONTENT_RS_ID", "CONTENT_TYP_NAME",
//            "CONTENT_PUB_DT", "CONTENT_ENT_TIME", "CONTENT_SHOW_TAGS"};
//    static String[] newStrGroup = new String[]{"msg_id", "news_id","title", "body", "tags", "supplier", "type_name", "publish_time", "enter_time", "show_tags"};

    static String[] originStrGroup = new String[]{"MSG_ID", "CONTENT_ID"};
    static String[] newStrGroup = new String[]{"msg_id", "news_id"};

    static Set<String> categorySet = new HashSet<>(Arrays.asList("CONCEPT", "INDUSTRY", "STOCK", "NEWS", "REGION"));

    /**
     * 标题 title：CONTENT.TIT
     * 正文 body: CONTENT.CONT
     * 标签 tags: CONTENT.AUTO_TAGS
     * 标题长度 title_length:
     * 正文长度 body_length:
     * 供应商 supplier: CONTENT.RS_ID
     * 栏目 type_name: CONTENT.TYP_NAME
     * 发布时间 publish_time: CONTENT.PUB_DT
     * 落地时间 enter_time: CONTENT.ENT_TIME
     * 落地延时 enter_delay：落地时间 - 发布时间
     * 打出标签总量 tags_count:
     * 打出标签类别量 tag_category_count:
     * 打出个股标签量 stock_tag_count:
     * 打出主题标签量 concept_tag_count:
     * 打出新闻标签量 news_tag_count:
     * 打出行业标签量 industry_tag_count:
     * 打出地域标签量 region_tag_count:
     * 多空标识 emtion: EMOTION标签的tagName
     * 打出的首个标签类别 first_tag_category: showTags权重最高标签的category，没有showTag的为：other
     * 标题中打出标签总数 title_tags_count:
     * 标题中打出个股数 title_stock_count:
     * 标题中打出主题标签数 title_concept_count:
     * 标题中打出新闻标签数 title_news_count:
     * 标题中打出行业标签数 title_industry_count:
     * 标题中打出地域标签数 title_region_count:
     */

    public static void main(String[] args) {
        try {
            for (String path : args) {
                File file = new File(path);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String readJson;
                List<Map<String, Object>> lines = new ArrayList<>();
                int count = 0;
                while ((readJson = br.readLine()) != null) {
                    lines.add(mapper(readJson));
                    count++;
                    if (count % 1000 == 0) {
                        lines.stream().filter(FirstLogReader::filter).forEach(FirstLogReader::printForEach2);
                        lines = new ArrayList<>();
                    }
                }
                lines.stream().filter(FirstLogReader::filter).forEach(FirstLogReader::printForEach2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        tagRunner();
//        MySQLApplicationService.main(new String[5]);
    }

    protected static Map<String, Object> mapper(String st) {
        try {
//            if (st.contains("NWS_TAG")) {
            String value = st.substring(st.indexOf(" |$| ") + " |$| ".length());
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            return gson.fromJson(value, type);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    protected static boolean filter(Map<String, Object> filterMap) {
//        Object msgType = filterMap.get("MSG_TYPE");
//        if (msgType != null && "NWS_TAG".equals(msgType.toString())) {
        Object content = filterMap.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        contentMap.putIfAbsent("RS_ID", "NM");
        return true;
//        }
//        return false;
    }

//    private static void printForEach(Map<String, Object> itemMap) {
//        Object content = itemMap.get("CONTENT");
//        Type type = new TypeToken<Map<String, Object>>() {
//        }.getType();
//        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
//        LocalDateTime enterTime = LocalDateTime.parse(contentMap.get("ENT_TIME").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
//        LocalDateTime publishTime = LocalDateTime.parse(contentMap.get("PUB_DT").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
//        long enterDelay = enterTime.toEpochSecond(ZoneOffset.UTC) - publishTime.toEpochSecond(ZoneOffset.UTC);
//        contentMap.put("ENT_TIME", enterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        contentMap.put("PUB_DT", publishTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
//            if ("CONT".equals(entry.getKey())) {
//                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue().toString().replaceAll("<.*?>", ""));
//            } else {
//                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue());
//            }
//        }
//        itemMap.remove("CONTENT");
//        Map<String, Object> targetMap = new HashMap<>();
//        for (int i = 0; i < originStrGroup.length; i++) {
//            targetMap.put(newStrGroup[i], itemMap.get(originStrGroup[i]));
//        }
//        int tagsCount = 0;
//        int tagCategoryCount = 0;
//        int stockTagCount = 0;
//        int conceptTagCount = 0;
//        int newsTagCount = 0;
//        int industryTagCount = 0;
//        int regionTagCount = 0;
//        String emotion = "中性";
//        String firstTagCategory = "OTHER";
//        if (targetMap.get("tags") instanceof List<?>) {
//            List<Map<String, Object>> tagList = (List<Map<String, Object>>) targetMap.get("tags");
//            tagsCount = tagList.size() - 1;
//            for (Map<String, Object> tag : tagList) {
//                String category = (String) tag.get("category");
//                switch (category) {
//                    case "CONCEPT":
//                        conceptTagCount++;
//                        break;
//                    case "INDUSTRY":
//                        industryTagCount++;
//                        break;
//                    case "STOCK":
//                        stockTagCount++;
//                        break;
//                    case "NEWS":
//                        newsTagCount++;
//                        break;
//                    case "REGION":
//                        regionTagCount++;
//                        break;
//                    default:
//                        break;
//                }
//            }
//            emotion = (String) tagList.get(tagList.size() - 1).get("tagName");
//        }
//
//        if (targetMap.get("show_tags") instanceof List<?>) {
//            List<Map<String, Object>> showTagList = (List<Map<String, Object>>) targetMap.get("show_tags");
//            if (showTagList.size() > 0) {
//                firstTagCategory = (String) showTagList.get(0).get("category");
//            }
//        }
//        targetMap.put("tags_count", tagsCount);
//        targetMap.put("tag_category_count", tagCategoryCount);
//        targetMap.put("stock_tag_count", stockTagCount);
//        targetMap.put("concept_tag_count", conceptTagCount);
//        targetMap.put("news_tag_count", newsTagCount);
//        targetMap.put("industry_tag_count", industryTagCount);
//        targetMap.put("region_tag_count", regionTagCount);
//        targetMap.put("emotion", emotion);
//        targetMap.put("enter_delay", enterDelay);
//        targetMap.put("first_tag_category", firstTagCategory);
//
//        MySQLApplicationService.insertOneDocument(MySQLApplicationService.getCollection(0), "log_reader_", targetMap);
//    }


    private static void printForEach2(Map<String, Object> itemMap) {
        Object content = itemMap.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
            if ("CONT".equals(entry.getKey())) {
                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue().toString().replaceAll("<.*?>", ""));
            } else {
                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue());
            }
        }
        itemMap.remove("CONTENT");
//        Map<String, Object> targetMap = new HashMap<>();
//        for (int i = 0; i < originStrGroup.length; i++) {
//            targetMap.put(newStrGroup[i], itemMap.get(originStrGroup[i]));
//        }

        MongoDBApplicationService.insertOneDocument(MongoDBApplicationService.getCollection((String) itemMap.get("MSG_TYPE")), new Document(itemMap));
        System.out.println(countNum.getAndAdd(1));
//        MySQLApplicationService.insertOneDocument(MySQLApplicationService.getCollection(0), "log_fix", targetMap);
    }

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0,
            TimeUnit.HOURS, new LinkedBlockingDeque<>());

    private static void tagRunner() {
        String url = "http://47.101.222.163:7003/rest/block/general-analyse-kafka";
        try {
            int size = 46000;
            for (int i = 0; i < 10; i++) {
                Connection conn = MySQLApplicationService.getCollection(i);
                String sql = "select msg_id, title from log_data limit " + i * size + "," + size;
                Statement statement = conn.createStatement();
                new Thread(() -> {
                    RestTemplate restTemplate = new RestTemplate();
                    try {
                        ResultSet resultSet = statement.executeQuery(sql);
                        while (resultSet.next()) {
                            String msgId = resultSet.getString("msg_id");
                            String title = resultSet.getString("title");
                            String content = "";
                            Map<String, Object> requestMap = new HashMap<>();
                            requestMap.put("title", title);
                            requestMap.put("content", content);
                            Map<String, Object> resultMap;
                            try {
                                ResponseEntity<Map> listResponseEntity = restTemplate.postForEntity(url, requestMap, Map.class);
                                resultMap = listResponseEntity.getBody();
                            } catch (Exception e) {
                                e.printStackTrace();
                                resultMap = new HashMap<>();
                            }
                            Object result = resultMap.get("result");
                            List<Map<String, Object>> list = new ArrayList<>();
                            if (result instanceof List) {
                                list = (List<Map<String, Object>>) result;
                            }
                            int tagsCount;
                            int stockTagCount = 0;
                            int conceptTagCount = 0;
                            int newsTagCount = 0;
                            int industryTagCount = 0;
                            int regionTagCount = 0;
                            tagsCount = list.size();
                            for (Object tag : list) {
                                if (tag instanceof Map) {
                                    String category = (String) ((Map<String, Object>) tag).get("category");
                                    switch (category) {
                                        case "CONCEPT":
                                            conceptTagCount++;
                                            break;
                                        case "INDUSTRY":
                                            industryTagCount++;
                                            break;
                                        case "STOCK":
                                            stockTagCount++;
                                            break;
                                        case "NEWS":
                                            newsTagCount++;
                                            break;
                                        case "REGION":
                                            regionTagCount++;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            boolean execute = statement.execute("update log_data set title_tags_count = " + tagsCount + ",title_stock_count = " + stockTagCount
                                    + ",title_concept_count = " + conceptTagCount + ",title_industry_count = " + industryTagCount
                                    + ",title_region_count = " + regionTagCount + ",title_news_count = " + newsTagCount + " where msg_id = '" + msgId + "'");
                            System.out.println("result : " + execute);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
