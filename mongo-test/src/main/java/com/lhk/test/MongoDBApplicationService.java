package com.lhk.test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MongoDBApplicationService {

    public static Map<String, MongoCollection<Document>> collectionMap = new Hashtable<>();


    private static void test() {
        MongoCollection<Document> checkCollection = getCollection("CLS", "Article");
        Document document = new Document("$gt","2019-05-17");
        document.append("$lte","2019-05-24");
        Document dateDoc = new Document("date", document);
        Document d1 = new Document("stockNames","/.*基金.*/i");
        Document d2 = new Document("conceptNames","/.*基金.*/i");
        Document d3 = new Document("industryNames","/.*基金.*/i");
        Document d4 = new Document("regionNames","/.*基金.*/i");
        Document d5 = new Document("newsNames","/.*基金.*/i");
        List<Document> orList = new ArrayList<>();
        orList.add(d1);orList.add(d2);orList.add(d3);orList.add(d4);orList.add(d5);
        List<Document> andList = new ArrayList<>();
        andList.add(dateDoc);
        andList.add(new Document("$or", orList));
        Document allDoc = new Document("$and", andList);
        System.out.println(checkCollection.count(allDoc));
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        test();
////        MongoCollection<Document> collection = getCollection("SSE", "Article_0327_0509");
//        MongoCollection<Document> checkCollection = getCollection("SSE", "Article_0320_0327");
//        Document projection = new Document("newsId", 1);
////        Document findDocument = new Document("similarCount", new Document("$lt", 2));
//        FindIterable<Document> iterable = checkCollection.find().projection(projection).batchSize(100);
//        AtomicInteger updateCount = new AtomicInteger(0);
//        for (Document document : iterable) {
//            List<String> similarIds = new ArrayList<>();
//            String newsId = document.getString("newsId");
//            similarIds.add(newsId);
////            Iterator<String> iterator = similarIds.iterator();
////            while (iterator.hasNext()) {
////                long tempCount = checkCollection.count(new Document("newsId", iterator.next()));
////                if (tempCount <= 0) {
////                    iterator.remove();
////                }
////            }
//
//            Document updateDocument = new Document("similarIds", similarIds);
//            updateDocument.append("similarCount", similarIds.size());
//            updateDocument.append("url", "https://www.cls.cn/roll/" + newsId);
////            checkCollection.updateMany(new Document("newsId", new Document("$in", similarIds)), new Document("$set", updateDocument));
//            checkCollection.updateOne(new Document("newsId", newsId), new Document("$set", updateDocument));
//            System.out.println("dateTime: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//                    + ", count: " + updateCount.addAndGet(1) + ", newsId: " + newsId + ", update SUCCESS ...");
//        }
    }


    public static MongoCollection<Document> getCollection(String dbName, String collection) {
        if (collectionMap.get(collection) == null) {
            try {
                ServerAddress serverAddress = new ServerAddress("dds-2zef7cd1cc1887b41.mongodb.rds.aliyuncs.com", 3717);
                List<ServerAddress> addresses = new ArrayList<>();
                addresses.add(serverAddress);

                MongoCredential credential = MongoCredential.createScramSha1Credential("cls_root", dbName,
                        "password1!".toCharArray());
                MongoClientOptions.Builder build = new MongoClientOptions.Builder();
                MongoClient mongoClient = new MongoClient(addresses, credential, build.build());
                MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
                collectionMap.put(collection, mongoDatabase.getCollection(collection));
                System.out.println("[mongodb client]: mongodb Create SUCCESS");
            } catch (Exception e) {
                System.err.println("[mongodb client]: mongodb Create FAILED");
                e.printStackTrace();
            }
        }
        return collectionMap.get(collection);
    }

}
