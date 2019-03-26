package com.test;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLBuildUtils {

    public static void main(String[] args) {
        new HTMLBuildUtils().createHtml("2019-01-01 00:00:00.0", "2019-01-31 00:00:00.0");
    }

    public void createHtml(String startTime, String endTime) {
        HTMLBuilder builder = new HTMLBuilder("D:/tmp/temp.html");
        LocalDateTime maxDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        LocalDateTime minDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        long timeDiff = maxDateTime.toEpochSecond(ZoneOffset.ofHours(8)) - minDateTime.toEpochSecond(ZoneOffset.ofHours(8));
        if (timeDiff > 0 && timeDiff < 7 * 3600 * 24) {
        }
        if (timeDiff > 2 * 3600 * 24 && timeDiff < 365 * 3600 * 24) {
        }
        if (timeDiff > 60 * 3600 * 24 && timeDiff < 60 * 60 * 3600 * 24) {
        }
        builder.build();
    }

    public static List<String> sortMapByValue(Map<String, Integer> map) {
        int size = map.size();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(size);
        list.addAll(map.entrySet());
        return list.stream()
                .sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void printDupNumChart(HTMLBuilder builder, Map<String, Integer> sourceNumMap,
                                        Map<String, Integer> dupSourceNumMap) {
        Set<String> legendDataSet = new LinkedHashSet<>();
        legendDataSet.add("有效资讯量");
        legendDataSet.add("重复资讯量");
        Map<String, List<Integer>> tempResultMap = new LinkedHashMap<>();
        Map<String, Integer> sortSourceMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sourceNumMap.entrySet()) {
            int dupNum = dupSourceNumMap.get(entry.getKey()) == null ? 0 : dupSourceNumMap.get(entry.getKey());
            int totalNum = entry.getValue();
            List<Integer> numList = new ArrayList<>();
            numList.add(totalNum - dupNum);
            numList.add(dupNum);
            tempResultMap.put(entry.getKey(), numList);
            sortSourceMap.put(entry.getKey(), totalNum - dupNum);
        }
        List<String> keyList = sortMapByValue(sortSourceMap);
        Map<String, String> resultMap = new LinkedHashMap<>();
        StringBuilder usefulNumStr = new StringBuilder();
        StringBuilder diffDupNumStr = new StringBuilder();
        boolean isFirst = true;
        for (String source : keyList) {
            if (isFirst) {
                usefulNumStr.append("[").append(tempResultMap.get(source).get(0));
                diffDupNumStr.append("[").append(tempResultMap.get(source).get(1));
                isFirst = false;
            } else {
                usefulNumStr.append(",").append(tempResultMap.get(source).get(0));
                diffDupNumStr.append(",").append(tempResultMap.get(source).get(1));
            }
        }
        usefulNumStr.append("]");
        diffDupNumStr.append("]");
        resultMap.put("有效资讯量", usefulNumStr.toString());
        resultMap.put("重复资讯量", diffDupNumStr.toString());
        builder.drawCombinationChart("重复量关系图", "", "dupNumChart", legendDataSet, new LinkedHashSet<>(keyList), resultMap);
    }

    public static void printScatterChart(HTMLBuilder builder, Map<String, Integer> sourceNumMap,
                                         Map<String, Integer> dupSourceNumMap) {
        printDupNumChart(builder, sourceNumMap, dupSourceNumMap);
        DecimalFormat df = new DecimalFormat("######0.00");
        int maxSize = 0;
        Map<String, List<Object>> resultMap = new LinkedHashMap<>();
        int allTotalNum = 0;
        int allUsefulNum = 0;
        int allDupNum = 0;
        for (Map.Entry<String, Integer> entry : sourceNumMap.entrySet()) {
            List<Object> tempList = new ArrayList<>();
            int dupNum = dupSourceNumMap.get(entry.getKey()) == null ? 0 : dupSourceNumMap.get(entry.getKey());
            int totalNum = entry.getValue();
            String dupRate = df.format(dupNum * 100.0 / totalNum);
            allTotalNum += totalNum;
            tempList.add(totalNum);
            tempList.add(dupRate);
            tempList.add(totalNum - dupNum);
            allUsefulNum += totalNum - dupNum;
            tempList.add(dupNum);
            allDupNum += dupNum;
            if ((totalNum - dupNum) > maxSize) {
                maxSize = totalNum - dupNum;
            }
            resultMap.put(entry.getKey(), tempList);
        }
        Set<String> itemSet = new LinkedHashSet<>();
        itemSet.add("资讯总量");
        itemSet.add("重复率");
        itemSet.add("有效资讯量");
        itemSet.add("重复资讯量");
        String subtext = "有效资讯总量：" + allUsefulNum + "\\n";
        subtext += "重复资讯总量：" + allDupNum + "\\n";
        subtext += "总资讯量：" + allTotalNum + "\\n";
        subtext += "总重复率：" + df.format(allDupNum * 100.0 / allTotalNum) + "\\n";
        builder.drawScatterChart("重复率散点图", subtext, "dupRateScatterChart",
                "资讯总量", "重复率", "有效资讯量", itemSet, resultMap, maxSize / 3 + maxSize);
    }

    public static void printWorkdayTread(HTMLBuilder builder, Map<String, Map<String, Integer>> workdayMap, Set<String> legendDataSet,
                                         Set<String> xAxisDataSet) {
        Map<String, String> workdayResultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : workdayMap.entrySet()) {
            workdayResultMap.put(entry.getKey(), entry.getValue().values().toString());
        }
        builder.drawCombinationChart("工作日平均每小时走势", "", "workdayTrend", legendDataSet, xAxisDataSet, workdayResultMap);
    }

    public static void printWeekendTread(HTMLBuilder builder, Map<String, Map<String, Integer>> weekendMap, Set<String> legendDataSet,
                                         Set<String> xAxisDataSet) {
        Map<String, String> weekendResultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : weekendMap.entrySet()) {
            weekendResultMap.put(entry.getKey(), entry.getValue().values().toString());
        }
        builder.drawCombinationChart("周末平均每小时走势", "", "weekendTrend", legendDataSet, xAxisDataSet, weekendResultMap);
    }

    public static void printEveryHourTrend(HTMLBuilder builder, Map<String, Map<String, Integer>> hourMap,
                                           Set<String> legendDataSet, Set<String> xAxisDataSet) {
        Map<String, String> hourResultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : hourMap.entrySet()) {
            hourResultMap.put(entry.getKey(), entry.getValue().values().toString());
        }
        builder.drawCombinationChart("每小时数据走势", "", "everyHourTrend", legendDataSet, xAxisDataSet, hourResultMap);
    }

    public static void printEveryDayTrend(HTMLBuilder builder, Map<String, Map<String, Integer>> dayMap,
                                          Set<String> legendDataSet, Set<String> xAxisDataSet) {
        Map<String, String> dayResultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : dayMap.entrySet()) {
            dayResultMap.put(entry.getKey(), entry.getValue().values().toString());
        }
        builder.drawCombinationChart("每天数据走势", "", "everyHourTrend", legendDataSet, xAxisDataSet, dayResultMap);
    }

    public static void printTimelinessTrend(HTMLBuilder builder, Map<String, Map<String, Integer>> timelinessMap, Set<String> legendDataSet,
                                            Set<String> xAxisDataSet) {
        Map<String, String> timelinessMapResultMap = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : timelinessMap.entrySet()) {
            timelinessMapResultMap.put(entry.getKey(), entry.getValue().values().toString());
        }
        builder.drawCombinationChart("时效性", "", "timelinessTrend", legendDataSet, xAxisDataSet, timelinessMapResultMap);
    }
}
