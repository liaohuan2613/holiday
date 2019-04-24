package com.test;

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

public class SecondLogReader {

    private static Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        String path = args[0];
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        Map<String, Object> resultMap = lines.stream().map(FirstLogReader::mapper).filter(FirstLogReader::filter).map(SecondLogReader::mapper).reduce(new LinkedHashMap<>(), SecondLogReader::reducer);
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            Map<String, Object> map = (Map<String, Object>) entry.getValue();
            int countNum = Integer.valueOf(map.get("countNum").toString());
            long totalTime = Long.valueOf(map.get("totalTime").toString());
            map.put("avgTime", totalTime / countNum);
            map.remove("totalTime");
            System.out.println(entry);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> reducer(Map<String, Object> resultMap, Map<String, Object> itemMap) {
        Object rsIdObj = itemMap.get("rsId");
        String rsId = rsIdObj == null ? "null" : rsIdObj.toString();
        Object valueMapObj = resultMap.get(rsId);
        Map<String, Object> valueMap;
        if (valueMapObj instanceof Map<?, ?>) {
            valueMap = (Map<String, Object>) valueMapObj;
        } else {
            valueMap = new HashMap<>();
        }
        if (itemMap.get("timeDiff") == null) {
            return resultMap;
        }
        long timeDiff = Long.valueOf(itemMap.get("timeDiff").toString());
        if (timeDiff < 0) {
            int errorTime = valueMap.get("errorTime") == null ? 0 : Integer.valueOf(valueMap.get("errorTime").toString());
            errorTime++;
            valueMap.put("errorTime", errorTime);
        } else {
            long totalTime = valueMap.get("totalTime") == null ? 0 : Long.valueOf(valueMap.get("totalTime").toString());
            int countNum = valueMap.get("countNum") == null ? 0 : Integer.valueOf(valueMap.get("countNum").toString());
            valueMap.put("totalTime", totalTime + timeDiff);
            valueMap.put("countNum", ++countNum);
        }
        resultMap.put(rsId, valueMap);
        return resultMap;
    }


    private static Map<String, Object> mapper(Map<String, Object> map) {
        Map<String, Object> itemMap = new HashMap<>();
        Object content = map.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        itemMap.put("rsId", contentMap.get("RS_ID"));
        long enterTime = LocalDateTime.parse(contentMap.get("ENT_TIME").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).toEpochSecond(ZoneOffset.UTC);
        long publishTime = LocalDateTime.parse(contentMap.get("PUB_DT").toString(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).toEpochSecond(ZoneOffset.UTC);
        long timeDiff = (enterTime - publishTime);
        itemMap.put("timeDiff", timeDiff);
        if ("".equals(itemMap.get("rsId").toString())) {
            System.out.println(content.toString());
        }
        return itemMap;
    }

}
