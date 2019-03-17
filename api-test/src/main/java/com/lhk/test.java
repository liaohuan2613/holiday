package com.lhk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

public class test {

    private static final String GENERAL_ANALYSE_KAFKA_URL = "http://203.156.205.101:11303/rest/block/general-analyse-kafka";
    private static final String COMPANY_EMOTION_URL = "http://203.156.205.101:11303/rest/block/company-emotion";
    private static RestTemplate restTemplate = new RestTemplate();
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        Map<String, Object> tagPostMap = new HashMap<>();
        String title = "测试";
        String content = "市委网信办全面开展网站平台涉保健品虚假宣传专项清理整治行动，即日起天津网信网正式推出涉保健品虚假宣传举报窗口，" +
                "通过“88908890”热线电话和官方网站链接，为集中打击清理整顿保健品市场乱象提供线索。桂东电力产品价格下跌";
        if (title == null || "".equals(title)) {
            return;
        }
        tagPostMap.put("mktCd", "*");
        tagPostMap.put("title", title);
        tagPostMap.put("content", content == null ? "" : content);

        System.out.println(System.currentTimeMillis());
        URI generalAnalyseKafkaUri = UriComponentsBuilder.fromUriString(GENERAL_ANALYSE_KAFKA_URL).build().encode().toUri();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(generalAnalyseKafkaUri, tagPostMap, String.class);
        System.out.println(getIndustries(responseEntity.getBody()));
        generalAnalyseKafkaUri = UriComponentsBuilder.fromUriString(COMPANY_EMOTION_URL).build().encode().toUri();
        responseEntity = restTemplate.postForEntity(generalAnalyseKafkaUri, tagPostMap, String.class);
        System.out.println(getCompanies(responseEntity.getBody()));
        System.out.println(System.currentTimeMillis());
    }

    private static List<Map<String, Object>> getCompanies(String body) {
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Type mapMapType = new TypeToken<Map<String, Map<String, Object>>>() {
        }.getType();
        Map<String, Object> map = gson.fromJson(body, mapType);
        Map<String, Object> resultMap = new HashMap<>();
        if (map.get("result") instanceof Map) {
            resultMap = gson.fromJson(map.get("result").toString(), mapType);
        }
        Map<String, Map<String, Object>> companyMap = new HashMap<>();
        if (resultMap.get("companies") instanceof Map) {
            companyMap = gson.fromJson(resultMap.get("companies").toString(), mapMapType);
        }
        for (Map.Entry<String, Map<String, Object>> entry : companyMap.entrySet()) {
            Map<String, Object> tempMap = new LinkedHashMap<>();
            Map<String, Object> entryMap = entry.getValue();
            tempMap.put("code", entryMap.get("id"));
            tempMap.put("name", entryMap.get("name"));
            tempMap.put("emotion", entryMap.get("emotion"));
            resultMapList.add(tempMap);
        }
        return resultMapList;
    }

    public static List<Map<String, Object>> getIndustries(String body) {
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        Map<String, Object> map = gson.fromJson(body, mapType);
        List<Map<String, Object>> list = new ArrayList<>();
        if (map.get("result") instanceof List) {
            list = gson.fromJson(map.get("result").toString(), listType);
        }
        Map<String, Object> industryMap = null;
        Map<String, Object> emotionMap = null;
        for (Map<String, Object> tempMap : list) {
            if ("INDUSTRY".equals(tempMap.get("category").toString())) {
                industryMap = tempMap;
            }
            if ("EMOTION".equals(tempMap.get("category").toString())) {
                emotionMap = tempMap;
            }
        }
        String emotionName = "中性";
        if (emotionMap != null && emotionMap.size() != 0) {
            emotionName = emotionMap.get("tagName") == null ? "中性" : emotionMap.get("tagName").toString();
        }
        if (industryMap != null && industryMap.size() != 0) {
            Map<String, Object> tempMap = new LinkedHashMap<>();
            tempMap.put("code", industryMap.get("tagCode"));
            tempMap.put("name", industryMap.get("tagName"));
            tempMap.put("emotion", emotionName);
            resultMapList.add(tempMap);
        }
        return resultMapList;
    }
}
