package com.lhk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ESCreate {
    private static Gson gson = new Gson();
    private static Type mapType = new TypeToken<Map<String, List<Map<String, Object>>>>() {
    }.getType();

    private static Set<String> zbyz = new HashSet(Arrays.asList("CLS_10101003", "XG_A00002", "CLS_10700013", "CLS_10700012", "CLS_10108003", "CLS_10108009",
            "CLS_10108008", "CLS_10108007", "CLS_10108001", "CLS_10108006", "CLS_10108002", "CLS_10108005", "CLS_10108011"));
    private static Set<String> ppcp = new HashSet<>(Arrays.asList("XG_A00001"));
    private static Set<String> zcjd = new HashSet<>(Arrays.asList("CLS_A00001", "CLS_20103018", "CLS_20103001", "CLS_20103006", "CLS_20101008", "CLS_20103008", "CLS_20102001",
            "CLS_20102002", "CLS_20102005", "CLS_20103012", "CLS_20102003", "CLS_20102004", "CLS_20103005", "CLS_20101002", "CLS_20101001",
            "CLS_20101003", "CLS_20101005", "CLS_20101004", "CLS_20101006", "CLS_20103010", "CLS_20103011", "CLS_20101007", "CLS_20103009",
            "CLS_20103003", "CLS_20103007", "CLS_20103013", "CLS_20103004", "CLS_20103002", "CLS_20103015"));


    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            String readJson = FileUtils.readFileToString(new File(args[0]), "UTF-8");
            Map<String, List<Map<String, Object>>> testMap = gson.fromJson(readJson, mapType);
            List<Map<String, Object>> recordList = testMap.get("RECORDS");
            Set<String> resultSet = new HashSet<>();
            recordList.forEach(record -> {
                if ("false".equals(record.get("isDup").toString())) {
                    ((ArrayList<String>) record.get("stockCodes")).forEach(s -> addStr(record, s, resultSet));
                    ((ArrayList<String>) record.get("industryCodes")).forEach(s -> addStr(record, s, resultSet));

                    ((ArrayList<String>) record.get("productCodes")).forEach(s -> addStr(record, s, resultSet));
                    ((ArrayList<String>) record.get("conceptCodes")).forEach(s -> addStr(record, s, resultSet));

                    ((ArrayList<String>) record.get("newsCodes")).forEach(s -> addStr(record, s, resultSet));
                    ((ArrayList<String>) record.get("regionCodes")).forEach(s -> addStr(record, s, resultSet));
                }
            });
            System.out.println(resultSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addStr(Map<String, Object> record, String s, Set<String> resultSet) {
        if (zbyz.contains(s)) {
            resultSet.add(record.get("newsId") + "\t" + record.get("title") + "\t" + record.get("source") + "\t资本运作\t"
                    + record.get("date") + " " + record.get("time") + "\n");
        }
        if (zcjd.contains(s)) {
            resultSet.add(record.get("newsId") + "\t" + record.get("title") + "\t" + record.get("source") + "\t政策解读\t"
                    + record.get("date") + " " + record.get("time") + "\n");
        }
        if (ppcp.contains(s)) {
            resultSet.add(record.get("newsId") + "\t" + record.get("title")  + "\t" + record.get("source") + "\t品牌产品\t"
                    + record.get("date") + " " + record.get("time") + "\n");
        }

    }
}
