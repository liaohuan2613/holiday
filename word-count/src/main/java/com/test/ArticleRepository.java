package com.test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class ArticleRepository {

    public MongoCollection<Document> collection;

    public ArticleRepository() {
        collection = MongoDBApplicationService.getCollection();
    }

    public static void main(String[] args) {
        new ArticleRepository().findDrawHTML("2019-03-01 00:00:00", "2019-03-07 00:00:00");
    }

    public List<SupplierSourceCounter> getSupplierSourceCounterList(String startTime, String endTime) {
        System.out.println("=======================>" + collection.count());

        Document timeScopeDocument = new Document("$gte", startTime);
        timeScopeDocument.append("$lt", endTime);
        Document createTsDocument = new Document("createTs", timeScopeDocument);

        Document matchDocument = new Document("$match", createTsDocument);

        Document idDocument = new Document("source", "$source");
        idDocument.append("supplier", "$supplier");
        Document groupValueDocument = new Document("countNum", new Document("$sum", 1));
        groupValueDocument.put("_id", idDocument);
        Document groupDocument = new Document("$group", groupValueDocument);

        Document sortDocument = new Document("$sort", new Document("countNum", -1));
        Document limitDocument = new Document("$limit", 20);

        List<Document> pipelines = new ArrayList<>();
        pipelines.add(matchDocument);
        pipelines.add(groupDocument);
        pipelines.add(sortDocument);
        pipelines.add(limitDocument);
        MongoCursor<Document> sourceSupplierIterator = collection.aggregate(pipelines).iterator();
        Map<String, String> supplierSourceMap = new HashMap<>();
        while (sourceSupplierIterator.hasNext()) {
            Document document = sourceSupplierIterator.next();
            Document _id = document.get("_id", Document.class);
            supplierSourceMap.put(_id.getString("source"), _id.getString("supplier"));
        }

        List<Document> filterList = new ArrayList<>();
        filterList.add(createTsDocument);

        List<Document> supplierSourceOrList = new ArrayList<>();
        for (Map.Entry<String, String> entry : supplierSourceMap.entrySet()) {
            Document supplierDocument = new Document("supplier", entry.getValue());
            Document sourceDocument = new Document("source", entry.getKey());
            List<Document> supplierSourceAndList = new ArrayList<>();
            supplierSourceAndList.add(supplierDocument);
            supplierSourceAndList.add(sourceDocument);
            supplierSourceOrList.add(new Document("$and", supplierSourceAndList));
        }

        filterList.add(new Document("$or", supplierSourceOrList));

        Document filterDocument = new Document("$and", filterList);

        Document projectDocument = new Document("source", 1);
        projectDocument.put("supplier", 1);
        projectDocument.put("createTs", 1);
        projectDocument.put("date", 1);
        projectDocument.put("time", 1);
        projectDocument.put("isDup", 1);
        MongoCursor<Document> iterator = collection.find(filterDocument).projection(projectDocument).iterator();
        List<SupplierSourceCounter> supplierSourceCounterList = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            String supplier = document.getString("supplier");
            String source = document.getString("source");
            LocalDateTime enterTime = LocalDateTime.parse(document.getString("createTs"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime publishTime = LocalDateTime.parse(document.getString("date") + " " + document.getString("time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            boolean isDup = document.get("isDup") == null ? false : document.getBoolean("isDup");
            supplierSourceCounterList.add(new SupplierSourceCounter(supplier, source, enterTime, publishTime, isDup));
        }
        System.out.println(supplierSourceCounterList);
        return supplierSourceCounterList;
    }

    public String findDrawHTML(String startTime, String endTime) {
        List<SupplierSourceCounter> supplierSourceCounterList = getSupplierSourceCounterList(startTime, endTime);

        Set<String> timelinessSet = new LinkedHashSet<>();
        timelinessSet.add("-10.0 <--> -0.5");
        timelinessSet.add("-0.5 <--> 0");
        timelinessSet.add("0 <--> 0.5");
        timelinessSet.add("0.5 <--> 1.0");
        timelinessSet.add("1.0 <--> 1.5");
        timelinessSet.add("1.5 <--> 2.0");
        timelinessSet.add("2.0 <--> 10.0");

        Set<String> hourKeySet = new LinkedHashSet<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hourKeySet.add("0" + i);
            } else {
                hourKeySet.add("" + i);
            }
        }

        Set<String> dayKeySet = new LinkedHashSet<>();
        Set<String> dayAndHourKeySet = new LinkedHashSet<>();

        LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String startDateStr = startLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDateStr = endLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDateAndHourStr = startLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
        String endDateAndHourStr = endLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
        LocalDateTime startDate = startLocalDateTime;
        while (!endDateStr.equals(startDateStr)) {
            dayKeySet.add(startDateStr);
            startDate = startDate.plusDays(1);
            startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        LocalDateTime startDateAndHour = startLocalDateTime;
        while (!endDateAndHourStr.equals(startDateAndHourStr)) {
            dayAndHourKeySet.add(startDateAndHourStr);
            startDateAndHour = startDateAndHour.plusHours(1);
            startDateAndHourStr = startDateAndHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
        }


        Map<String, Integer> mixSourceMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> dayMixSourceMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> hourMixSourceMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> weekendSourceMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> workdaySourceMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> timelinessMixSourceMap = new LinkedHashMap<>();
        Map<String, Integer> dupSourceMap = new LinkedHashMap<>();

        supplierSourceCounterList.forEach(supplierSourceCounter -> {
            String supplier = supplierSourceCounter.getSupplier() == null ? "NN" : supplierSourceCounter.getSupplier();
            String source = supplierSourceCounter.getSource() == null ? "未知源" : supplierSourceCounter.getSource();
            LocalDateTime publishTime = supplierSourceCounter.getPublishTime();
            LocalDateTime enterTime = supplierSourceCounter.getEnterTime();
            String mixSource = supplier + " - " + source;
            boolean isDup = supplierSourceCounter.isDup();
            String date = enterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dateAndHour = enterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
            String hour = enterTime.format(DateTimeFormatter.ofPattern("HH"));

            int mixTempValue = mixSourceMap.getOrDefault(mixSource, 0) + 1;
            mixSourceMap.put(mixSource, mixTempValue);

            if (isDup) {
                int dupTempValue = dupSourceMap.getOrDefault(mixSource, 0) + 1;
                dupSourceMap.put(mixSource, dupTempValue);
            }

            if (DayOfWeek.SATURDAY.equals(enterTime.getDayOfWeek()) || DayOfWeek.SUNDAY.equals(enterTime.getDayOfWeek())) {
                mapCounter(weekendSourceMap, mixSource, hour, hourKeySet);
            } else {
                mapCounter(workdaySourceMap, mixSource, hour, hourKeySet);
            }
            mapCounter(dayMixSourceMap, mixSource, date, dayKeySet);

            mapCounter(hourMixSourceMap, mixSource, dateAndHour, dayAndHourKeySet);


            String timeDiffStr = timeDiffStr(enterTime.toEpochSecond(ZoneOffset.ofHours(8)) - publishTime.toEpochSecond(ZoneOffset.ofHours(8)));
            mapCounter(timelinessMixSourceMap, mixSource, timeDiffStr, timelinessSet);

        });
        HTMLBuilder builder = new HTMLBuilder("D:/tmp/tmp1.html");
        HTMLBuildUtils.printScatterChart(builder, mixSourceMap, dupSourceMap);
        long timeDiff = endLocalDateTime.toEpochSecond(ZoneOffset.ofHours(8)) - startLocalDateTime.toEpochSecond(ZoneOffset.ofHours(8));
        if (timeDiff > 0 && timeDiff < 7 * 3600 * 24) {
            HTMLBuildUtils.printEveryHourTrend(builder, hourMixSourceMap, hourMixSourceMap.keySet(), dayAndHourKeySet);
        }
        if (timeDiff > 2 * 3600 * 24 && timeDiff < 365 * 3600 * 24) {
            HTMLBuildUtils.printEveryDayTrend(builder, dayMixSourceMap, dayMixSourceMap.keySet(), dayKeySet);
        }
        HTMLBuildUtils.printWeekendTread(builder, weekendSourceMap, weekendSourceMap.keySet(), hourKeySet);
        HTMLBuildUtils.printWorkdayTread(builder, workdaySourceMap, workdaySourceMap.keySet(), hourKeySet);
        HTMLBuildUtils.printTimelinessTrend(builder, timelinessMixSourceMap, timelinessMixSourceMap.keySet(), timelinessSet);
        System.out.println(builder.printHtml());
        return builder.printHtml();
    }

    private Map<String, Integer> initValueMap(Set<String> keySet) {
        Map<String, Integer> valueMap = new LinkedHashMap<>();
        keySet.forEach(key -> valueMap.put(key, 0));
        return valueMap;
    }

    private String timeDiffStr(long timeDiff) {
        if (timeDiff < -1800) {
            return "-10 <--> -0.5";
        } else if (timeDiff < 0) {
            return "-0.5 <--> 0";
        } else if (timeDiff < 1800) {
            return "0 <--> 0.5";
        } else if (timeDiff < 3600) {
            return "0.5 <--> 1.0";
        } else if (timeDiff < 5400) {
            return "1.0 <--> 1.5";
        } else if (timeDiff < 7200) {
            return "1.5 <--> 2.0";
        } else {
            return "2.0 <--> 10.0";
        }
    }

    private void mapCounter(Map<String, Map<String, Integer>> mixSourceMap, String mixSource, String secondKey, Set<String> valueSet) {
        Map<String, Integer> tempMap = mixSourceMap.computeIfAbsent(mixSource, key -> initValueMap(valueSet));
        int mixValue = tempMap.getOrDefault(secondKey, 0) + 1;
        tempMap.put(secondKey, mixValue);
    }

}
