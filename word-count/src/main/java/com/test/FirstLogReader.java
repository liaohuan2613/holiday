package com.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FirstLogReader {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            for (String path : args) {
                List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
                lines.stream().map(FirstLogReader::mapper).filter(FirstLogReader::filter).forEach(FirstLogReader::printForEach);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        MySQLApplicationService.main(new String[5]);
    }

    protected static Map<String, Object> mapper(String st) {
        String value = st.substring(st.indexOf(" |$| ") + " |$| ".length(), st.length());
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(value, type);
    }

    protected static boolean filter(Map<String, Object> filterMap) {
        Object msgType = filterMap.get("MSG_TYPE");
        if (msgType != null && ("NWS".equals(msgType.toString()) || "NWS_TAG".equals(msgType.toString()))) {
            Object content = filterMap.get("CONTENT");
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
            return contentMap.get("RS_ID") != null && !"".equals(contentMap.get("RS_ID").toString());
        }
        return false;
    }

    private static void printForEach(Map<String, Object> itemMap) {
        Object content = itemMap.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        contentMap.put("ENT_TIME", LocalDateTime.parse(contentMap.get("ENT_TIME").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        contentMap.put("PUB_DT", LocalDateTime.parse(contentMap.get("PUB_DT").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

//        MySQLApplicationService.deleteOneDocument(MySQLApplicationService.getCollection(), "log_reader", itemMap.get("MSG_ID").toString());
        MySQLApplicationService.insertOneDocument(MySQLApplicationService.getCollection(), "log_reader_second", itemMap);
    }
}
