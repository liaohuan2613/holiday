package com.test.readers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForthLogReader {
    private static Gson gson = new Gson();

    private static LocalDateTime localDateTime = LocalDateTime.parse("2019-01-07 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("E:/kafka/kafka.2019-01-07.log"), StandardCharsets.UTF_8);
        Map<String, Object> resultMap = lines.stream().map(FirstLogReader::mapper).filter(FirstLogReader::filter).map(ForthLogReader::mapper).reduce(new LinkedHashMap<>(), ForthLogReader::reducer);
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            System.out.println(entry);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> reducer(Map<String, Object> resultMap, Map<String, Object> itemMap) {
        String rsId = itemMap.get("rsId").toString();
        Map<String, Object> timeMap;
        if (resultMap.get(rsId) instanceof Map<?, ?>) {
            timeMap = (Map<String, Object>) resultMap.get(rsId);
        } else {
            timeMap = new HashMap<>();
        }
        long itemDateTime = ((LocalDateTime) itemMap.get("enterTime")).toEpochSecond(ZoneOffset.UTC);
        long startTime = localDateTime.toEpochSecond(ZoneOffset.UTC);
        long endTime = localDateTime.plusDays(1).toEpochSecond(ZoneOffset.UTC);
        if (itemDateTime > startTime && itemDateTime < endTime) {
            int countNum = timeMap.get("countNum") == null ? 0 : Integer.valueOf(timeMap.get("countNum").toString());
            timeMap.put("countNum", ++countNum);
        }
        resultMap.put(rsId, timeMap);
        return resultMap;
    }


    private static Map<String, Object> mapper(Map<String, Object> map) {
        Map<String, Object> itemMap = new HashMap<>();
        Object content = map.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        itemMap.put("rsId", contentMap.get("RS_ID"));
        LocalDateTime localDateTime = LocalDateTime.parse(contentMap.get("ENT_TIME").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        itemMap.put("enterTime", localDateTime);
        return itemMap;
    }

}
