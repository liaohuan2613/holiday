package com.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FirstLogReader {

    private static Gson gson = new Gson();
    static String[] originStrGroup = new String[]{"MSG_ID", "CONTENT_TIT", "CONTENT_CONT", "CONTENT_AUTO_TAGS", "CONTENT_RS_ID", "CONTENT_TYP_NAME",
            "CONTENT_PUB_DT", "CONTENT_ENT_TIME"};
    static String[] newStrGroup = new String[]{"msg_id", "title", "content", "tags", "supplier", "type_name", "publish_time", "enter_time"};

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
                        lines.stream().filter(FirstLogReader::filter).forEach(FirstLogReader::printForEach);
                        lines = new ArrayList<>();
                    }
                }
                lines.stream().filter(FirstLogReader::filter).forEach(FirstLogReader::printForEach);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        MySQLApplicationService.main(new String[5]);
    }

    protected static Map<String, Object> mapper(String st) {
        String value = st.substring(st.indexOf(" |$| ") + " |$| ".length());
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(value, type);
    }

    protected static boolean filter(Map<String, Object> filterMap) {
        Object msgType = filterMap.get("MSG_TYPE");
        if (msgType != null && "NWS_TAG".equals(msgType.toString())) {
            Object content = filterMap.get("CONTENT");
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
            contentMap.putIfAbsent("RS_ID", "NM");
            return true;
        }
        return false;
    }

    private static void printForEach(Map<String, Object> itemMap) {
        Object content = itemMap.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        LocalDateTime enterTime = LocalDateTime.parse(contentMap.get("ENT_TIME").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        LocalDateTime publishTime = LocalDateTime.parse(contentMap.get("PUB_DT").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        contentMap.put("ENT_TIME", enterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        contentMap.put("PUB_DT", publishTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        contentMap.put("UPD_TIME", LocalDateTime.parse(contentMap.get("UPD_TIME").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
            if ("CONT".equals(entry.getKey())) {
                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue().toString().replaceAll("<.*?>", ""));
            } else {
                itemMap.put("CONTENT_" + entry.getKey(), entry.getValue());
            }
        }
        itemMap.remove("CONTENT");
        Map<String, Object> targetMap = new HashMap<>();
        for (int i = 0; i < originStrGroup.length; i++) {
            targetMap.put(newStrGroup[i], itemMap.get(originStrGroup[i]));
        }

//        MySQLApplicationService.deleteOneDocument(MySQLApplicationService.getCollection(), "log_reader", itemMap.get("MSG_ID").toString());
        MySQLApplicationService.insertOneDocument(MySQLApplicationService.getCollection(), "log_reader", targetMap);
    }
}
