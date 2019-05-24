package com.test.readers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.test.MongoDBApplicationService;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ThirdLogReader {

    private static ReentrantLock lock = new ReentrantLock(true);
    private static Gson gson = new Gson();
    private static final String REMOVAL_URL = "http://47.96.26.149:5002/api/deduplication";
    private static RestTemplate restTemplate = new RestTemplate();
    private static Map<String, Integer> databaseMap = new HashMap<>();
    private static Integer databaseNum = 5;
    private static MongoCollection<Document> collection;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        collection = MongoDBApplicationService.getCollection("NEWS");

        //连接到数据库
        System.out.println("Connect to database successfully");

        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(1000 * 60 * 5);
        clientHttpRequestFactory.setReadTimeout(1000 * 60 * 5);
        restTemplate = new RestTemplate(clientHttpRequestFactory);
        for (String path : args) {
            try {
                Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8)
                        .stream()
                        .map(FirstLogReader::mapper)
                        .filter(FirstLogReader::filter)
                        .map(ThirdLogReader::mapper)
                        .forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        Object isDupObj = itemMap.get("isDup");
        if (isDupObj instanceof Boolean && (Boolean) isDupObj) {
            Integer dupCount = valueMap.get("dupCount") == null ? 0 : Integer.valueOf(valueMap.get("dupCount").toString());
            valueMap.put("dupCount", ++dupCount);
        }
        Integer numCount = valueMap.get("numCount") == null ? 0 : Integer.valueOf(valueMap.get("numCount").toString());
        valueMap.put("numCount", ++numCount);
        resultMap.put(rsId, valueMap);
        return resultMap;
    }


    private static Map<String, Object> mapper(Map<String, Object> map) {
        Object content = map.get("CONTENT");
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> contentMap = gson.fromJson(gson.toJson(content), type);
        Map<String, Object> requestMap = new HashMap<>();
        String rsId = contentMap.get("RS_ID").toString();
        Integer selfDatabase = databaseMap.get(rsId);
        if (selfDatabase == null) {
            selfDatabase = databaseNum;
            databaseMap.put(rsId, selfDatabase);
            databaseNum++;
        }
        requestMap.put("id", contentMap.get("ID"));
        requestMap.put("title", contentMap.get("TIT"));
        requestMap.put("content", contentMap.get("CONT"));
        requestMap.put("source", contentMap.get("TYP_NAME"));
        requestMap.put("database", 4);
        Map<String, Object> resultMap;
        Map<String, Object> selfResultMap;
        try {
            lock.lock();
            System.out.println("====================== >>>>> ENTER!!");
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(REMOVAL_URL, requestMap, Object.class);
            Map<String, Object> removalMap = gson.fromJson(gson.toJson(responseEntity.getBody()), type);
            resultMap = gson.fromJson(gson.toJson(removalMap.get("result")), type);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ResponseEntity<Object> responseEntity = restTemplate.postForEntity(REMOVAL_URL, requestMap, Object.class);
                Map<String, Object> removalMap = gson.fromJson(gson.toJson(responseEntity.getBody()), type);
                resultMap = gson.fromJson(gson.toJson(removalMap.get("result")), type);
            } catch (Exception retryException) {
                resultMap = new HashMap<>();
            }
        } finally {
            lock.unlock();
        }
        try {
            lock.lock();
            requestMap.put("database", databaseNum);
            ResponseEntity<Object> selfResponseEntity = restTemplate.postForEntity(REMOVAL_URL, requestMap, Object.class);
            Map<String, Object> selfRemovalMap = gson.fromJson(gson.toJson(selfResponseEntity.getBody()), type);
            selfResultMap = gson.fromJson(gson.toJson(selfRemovalMap.get("result")), type);
        } catch (Exception retryException) {
            selfResultMap = new HashMap<>();
        } finally {
            System.out.println("====================== >>>>> LEAVE!!");
            lock.unlock();
        }
        map.put("DUP_IDS", resultMap.get("dupIds"));
        map.put("IS_DUP", resultMap.get("isDup") == null ? false : resultMap.get("isDup"));
        map.put("SELF_DUP_IDS", selfResultMap.get("dupIds"));
        map.put("SELF_IS_DUP", selfResultMap.get("isDup") == null ? false : resultMap.get("isDup"));
        Document document = new Document(map);
        MongoDBApplicationService.insertOneDocument(collection, document);
        return map;
    }

}
