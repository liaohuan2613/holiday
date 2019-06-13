package com.lhk.test;

import com.lhk.util.DateUtils;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoDBApplicationService {

    public static Map<String, MongoCollection<Document>> collectionMap = new Hashtable<>();


    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("[a-z]+");
        Matcher matcher = pattern.matcher("3a1b2c b");
        while (matcher.find()) {
            String matcherString = matcher.group();
            System.out.println(matcherString);
        }
        MongoCollection<Document> collection = getCollection("");
//        new BasicDBObject("createTs", new BasicDBObject("$gt", "2019-05-13"))
        BasicDBObject basicDBObject = new BasicDBObject("$gte", "2019-05-13");
        basicDBObject.append("$lt", "2019-06-11");
        MongoCursor<Document> iterator = collection.find(new BasicDBObject("date", basicDBObject))
                .projection(new BasicDBObject("hourInformation", 1)).iterator();
        Map<String, Long> countMap = new TreeMap<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                countMap.put("0" + i, 0L);
            } else {
                countMap.put("" + i, 0L);
            }
        }
        while (iterator.hasNext()) {
            Document document = iterator.next();
            Map<String, Long> tempMap = document.get("hourInformation", new HashMap<>());
            for (Map.Entry<String, Long> entry : tempMap.entrySet()) {
                countMap.put(entry.getKey(), countMap.getOrDefault(entry.getKey(), 0L) + entry.getValue());
            }
        }
        System.out.println(countMap);
    }


//    public static MongoCollection<Document> getCollection(String dbName, String collection) {
//        if (collectionMap.get(collection) == null) {
//            try {
//                ServerAddress serverAddress = new ServerAddress("dds-2zef7cd1cc1887b41.mongodb.rds.aliyuncs.com", 3717);
//                List<ServerAddress> addresses = new ArrayList<>();
//                addresses.add(serverAddress);
//
//                MongoCredential credential = MongoCredential.createScramSha1Credential("cls_root", dbName,
//                        "password1!".toCharArray());
//                MongoClientOptions.Builder build = new MongoClientOptions.Builder();
//                MongoClient mongoClient = new MongoClient(addresses, credential, build.build());
//                MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
//                collectionMap.put(collection, mongoDatabase.getCollection(collection));
//                System.out.println("[mongodb client]: mongodb Create SUCCESS");
//            } catch (Exception e) {
//                System.err.println("[mongodb client]: mongodb Create FAILED");
//                e.printStackTrace();
//            }
//        }
//        return collectionMap.get(collection);
//    }

    public static MongoCollection<Document> getCollection(String type) {
        if (collectionMap.get(type) == null) {
            try {
                ServerAddress serverAddress = new ServerAddress("203.156.205.101", 10917);
//                ServerAddress serverAddress = new ServerAddress("47.96.26.118", 27017);
                List<ServerAddress> addresses = new ArrayList<>();
                addresses.add(serverAddress);

                MongoCredential credential = MongoCredential.createScramSha1Credential("root", "TEBON",
                        "#FGJoW^A3u*SSTbP".toCharArray());
                MongoClientOptions.Builder build = new MongoClientOptions.Builder();
                MongoClient mongoClient = new MongoClient(addresses, credential, build.build());
                MongoDatabase mongoDatabase = mongoClient.getDatabase("TEBON");
                collectionMap.put(type, mongoDatabase.getCollection("ArticleEvaluation"));
                System.out.println("[mongodb client]: mongodb Create SUCCESS");
            } catch (Exception e) {
                System.err.println("[mongodb client]: mongodb Create FAILED");
                e.printStackTrace();
            }
        }
        return collectionMap.get(type);
    }
}
