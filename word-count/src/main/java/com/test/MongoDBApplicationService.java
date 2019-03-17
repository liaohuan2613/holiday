package com.test;

import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Projections.*;

public class MongoDBApplicationService {

    public static MongoCollection<Document> collection;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {


//        collection = getCollection();
//        BasicDBObject query = new BasicDBObject();
//        query.put("audit", "PASS");
//        FindIterable<Document> iterable = collection.find(query).projection(fields(include("hotspot"), excludeId()));
//        for (Document d : iterable) {
//            Document hotspot = d.get("hotspot", Document.class);
//            for (Map.Entry<String, Object> entry : hotspot.entrySet()) {
//                LocalDate localDate = LocalDate.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//                long epochDay1 = localDate.toEpochDay();
//                LocalDateTime localDateTime = localDate.atTime(0, 0);
//                long epochDay2 = localDateTime.toLocalDate().toEpochDay();
//
//                System.out.println(epochDay2);
//            }
//        }
//        printTimelinessForEach(collection);
//        System.out.println("\n\n\n");
//
//        printRepetitionRateForEach(collection);
//        System.out.println("\n\n\n");
//
//        System.out.println("=================Hour Trad======================");
//        printStabilityForHourForEach(collection);
//        System.out.println("\n\n\n");
//
//        System.out.println("================= Day Trad ======================");
//        printStabilityForDayForEach(collection);
//        System.out.println("\n\n\n");
        findArticle(getCollection());
    }


    public static MongoCollection<Document> getCollection() {
        if (collection == null) {
            try {
                ServerAddress serverAddress = new ServerAddress("203.156.205.101", 10917);
//                ServerAddress serverAddress = new ServerAddress("47.96.26.118", 27017);
                List<ServerAddress> addresses = new ArrayList<>();
                addresses.add(serverAddress);

                MongoCredential credential = MongoCredential.createScramSha1Credential("dev", "NET_DEV", "password1!".toCharArray());
                List<MongoCredential> credentials = new ArrayList<>();
                credentials.add(credential);

                MongoClientOptions.Builder build = new MongoClientOptions.Builder();
                MongoClient mongoClient = new MongoClient(addresses, credential, build.build());
                MongoDatabase mongoDatabase = mongoClient.getDatabase("NET_DEV");
                collection = mongoDatabase.getCollection("Article");
                System.out.println("[mongodb client]: mongodb Create SUCCESS");
            } catch (Exception e) {
                System.err.println("[mongodb client]: mongodb Create FAILED");
                e.printStackTrace();
            }
        }
        return collection;
    }

    public static void findArticle(MongoCollection<Document> collection) {
        BasicDBObject query = new BasicDBObject();
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("source", new BasicDBObject("$in", new String[]{"证券时报网", "中国政府网", "少数派策略君", "央视财经", "债市覃谈",
                "人民日报", "新华社", "IPO观察", "证监会发布", "券商中国"}));
        BasicDBObject createDBObject = new BasicDBObject();

        createDBObject.put("$and", new BasicDBObject[]{new BasicDBObject("createTs", new BasicDBObject("$gte", "2019-02-26 00:00:00")),
                new BasicDBObject("createTs", new BasicDBObject("$lte", "2019-03-06 00:00:00"))});
        query.put("$and", new BasicDBObject[]{createDBObject, basicDBObject});
        FindIterable<Document> documents = collection.find(query);
        List<List<String>> resultList = new ArrayList<>();
        for (Document document : documents) {
            List<String> itemList = new ArrayList<>();
            itemList.add(document.getString("newsId"));
            itemList.add(document.getString("title"));
            itemList.add(document.getString("content"));
            itemList.add(document.getString("source"));
            itemList.add(document.getString("createTs"));
            resultList.add(itemList);
        }
        String[] fieldNameGroup = new String[]{"newsId", "title", "content", "source", "pub_dt",};
        CVSUtils.writeCSV(resultList, "D:/tmp/tmp_csv.csv", fieldNameGroup);
    }

    public static void insertOneDocument(MongoCollection<Document> collection, Document document) {
        try {
            document = new Document();
            collection.insertOne(document);
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "============Document insert SUCCESS! ");
        } catch (Exception e) {
            try {
                collection.insertOne(document);
            } catch (Exception e2) {
                e2.printStackTrace();
                System.err.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        + "============Document insert ERROR! ");
            }
        }
    }

    public static void print(MongoCollection<Document> collection) {
        Pattern pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
        BasicDBObject query = new BasicDBObject();
        query.put("content", pattern);
        FindIterable<Document> documents = collection.find(query).projection(fields(include("title"), excludeId())).batchSize(1000);
        Set<String> set = new HashSet<>();
        for (Document document : documents) {
            String title = document.getString("title");
            set.add(title);
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("D:/tmp/title.txt"));
            for (String title : set) {
                bw.write(title);
                System.out.println(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTimelinessForEach(MongoCollection<Document> collection) {
        FindIterable<Document> documents = collection.find(and(ne("CONTENT.OPT_TYP", "2"))).projection(fields(include("CONTENT.PUB_DT",
                "CONTENT.ENT_TIME", "CONTENT.RS_ID"), excludeId())).batchSize(1000);
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Document document : documents) {
            Document contentDocument = document.get("CONTENT", Document.class);
            long entTime = LocalDateTime.parse(contentDocument.getString("ENT_TIME"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).toEpochSecond(ZoneOffset.UTC);
            long pubDt = LocalDateTime.parse(contentDocument.getString("PUB_DT"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")).toEpochSecond(ZoneOffset.UTC);
            String rsId = contentDocument.getString("RS_ID");
            Map<String, Object> valueMap = resultMap.computeIfAbsent(rsId, k -> new HashMap<>());
            long timeDiff = entTime - pubDt;
            if (timeDiff < 0) {
                long errorTime = valueMap.get("errorTime") == null ? 0 : Integer.valueOf(valueMap.get("errorTime").toString());
                valueMap.put("errorTime", ++errorTime);
            } else {
                long totalTime = valueMap.get("totalTime") == null ? 0 : Long.valueOf(valueMap.get("totalTime").toString());
                long countNum = valueMap.get("countNum") == null ? 0 : Integer.valueOf(valueMap.get("countNum").toString());
                valueMap.put("totalTime", totalTime + timeDiff);
                valueMap.put("countNum", ++countNum);
                valueMap.put("avgTime", (totalTime + timeDiff) / countNum);
            }
        }
        System.out.println("=================TIMELINESS======================");
        for (Map.Entry<String, Map<String, Object>> entry : resultMap.entrySet()) {
            System.out.println(entry);
        }
    }

    public static void printRepetitionRateForEach(MongoCollection<Document> collection) {
        FindIterable<Document> documents = collection.find(and(ne("CONTENT.OPT_TYP", "2"))).projection(fields(include("IS_DUP", "CONTENT.RS_ID"), excludeId())).batchSize(2000);
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Document document : documents) {
            Document contentDocument = document.get("CONTENT", Document.class);
            boolean isDup = document.getBoolean("IS_DUP");
            String rsId = contentDocument.getString("RS_ID");
            Map<String, Object> valueMap = resultMap.computeIfAbsent(rsId, k -> new HashMap<>());
            long countNum = valueMap.get("countNum") == null ? 0 : Integer.valueOf(valueMap.get("countNum").toString());
            valueMap.put("countNum", ++countNum);
            if (isDup) {
                long dupNum = valueMap.get("dupNum") == null ? 0 : Integer.valueOf(valueMap.get("dupNum").toString());
                valueMap.put("dupNum", ++dupNum);
                valueMap.put("dupRate", dupNum * 100.0 / countNum);
//                valueMap.put("dupIds", document.get("DUP_IDS", new ArrayList<>()));
            }
        }
        System.out.println("=================DUP_RATE======================");
        for (Map.Entry<String, Map<String, Object>> entry : resultMap.entrySet()) {
            System.out.println(entry);
        }
    }

    public static void printStabilityForHourForEach(MongoCollection<Document> collection) {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-01-07 00:00:00:000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        for (int i = 0; i < 3; i++) {
            System.out.println("======================" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "==============================");
            for (int j = 0; j < 24; j++) {
                String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                selectRsIdByPattern(format, collection);
                localDateTime = localDateTime.plusHours(1);
            }
            System.out.println("\n\n\n");
        }
    }

    public static void printStabilityForDayForEach(MongoCollection<Document> collection) {
        LocalDateTime localDateTime = LocalDateTime.parse("2019-01-07 00:00:00:000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        for (int i = 0; i < 3; i++) {
            String format = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            selectRsIdByPattern(format, collection);
            localDateTime = localDateTime.plusDays(1);
        }
    }

    private static void selectRsIdByPattern(String format, MongoCollection<Document> collection) {
        Pattern pattern = Pattern.compile("^" + format + ".*$", Pattern.CASE_INSENSITIVE);
        BasicDBObject query = new BasicDBObject();
        query.put("CONTENT.ENT_TIME", pattern);
        query.put("CONTENT.OPT_TYP", new BasicDBObject("$ne", "2"));
        FindIterable<Document> documents = collection.find(query).projection(fields(include("CONTENT.ENT_TIME", "CONTENT.RS_ID"),
                excludeId())).batchSize(2000);
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        for (Document document : documents) {
            Document contentDocument = document.get("CONTENT", Document.class);
            String rsId = contentDocument.getString("RS_ID");
            Map<String, Object> valueMap = resultMap.computeIfAbsent(rsId, k -> new HashMap<>());
            long countNum = valueMap.get("countNum") == null ? 0 : Integer.valueOf(valueMap.get("countNum").toString());
            valueMap.put("countNum", ++countNum);
        }
        System.out.println("======================" + format + "============================");
        for (Map.Entry<String, Map<String, Object>> entry : resultMap.entrySet()) {
            System.out.println(entry);
        }
    }

}
