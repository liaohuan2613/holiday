package com.lhk.kafka;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * @author LHK
 */

public class ConsumerDemo {

    public static void main(String[] args) {
        Properties properties = new Properties();
        System.setProperty("java.security.auth.login.config", "C:/tmp/prod/kafka_client_jaas.conf");
        properties.put("bootstrap.servers", "47.96.26.149:9092,47.96.27.99:9092,47.96.3.207:9092");
//        properties.put("bootstrap.servers", "203.156.205.102:9092");
        properties.put("group.id", "test-owl-article");
        properties.put("enable.auto.commit", "false");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest");
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1024 * 1024 * 15);
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("article-with-show-tags"));
        try {
            int count = 0;
//            List<String> idList = Arrays.asList("5cee7e94c63f6e7120a0b031", "5cee9b976481f244f2cb20ac", "5ceeb2a36e4d7edb9134791b", "5cef18598689dc6b06ff27a9", "5cef1f8b00f54a843a358424", "5cef1dd993ffdbc1fc358437", "5cef1fd5a68992ca9c358424", "5ceebaf57154cf0480eca9f9", "5cef14872ede4fdeeb579568", "5cef1dd993ffdbc1fc358439", "5cef1dd993ffdbc1fc358434", "5ceec2d3e12b3f56ac3b63ae", "5cef1dd993ffdbc1fc358433", "5cef1dd993ffdbc1fc358436", "5cef1dd993ffdbc1fc358435", "5cef1dd993ffdbc1fc358430", "5cef1dd993ffdbc1fc358432", "5cee7b29a667f110472e87fe", "5cee9b6dcdccdeadb0cb20ae", "5cef1dd993ffdbc1fc35842f", "5cee86262bee128f71376359", "5ceead2787ec5e505392f5e0", "5ceea5221bf803c101c8a96a", "5cef1dd993ffdbc1fc35842e", "5cef1dd993ffdbc1fc35842d", "5cef1dd993ffdbc1fc35842a", "5cef1d611b70df50be358424", "5cef1dd993ffdbc1fc358449", "5cef1e69c5edc28d0a358424", "5cef1dd993ffdbc1fc358445", "5cee85eb0cd1830d0d376359", "5cef1e69c5edc28d0a358425", "5cef1dd993ffdbc1fc358446", "5cef19bf3df6aca028992652", "5cef1dd993ffdbc1fc358441", "5cef13b5a13410bcdea4ef07", "5cef1dd993ffdbc1fc358440", "5cef1dd993ffdbc1fc358443", "5cef1dd993ffdbc1fc358442", "5cee9e42f13d857074c475cf", "5cef140cd993f05d78579569", "5cee98daeda5fe9b2b5a4034", "5cee7f811a55617331919518", "5cef1549163b9bab2d5da7e0", "5cef1dd993ffdbc1fc35843f", "5cef1dd993ffdbc1fc35843e", "5cef1da464ed73ecc4358424", "5cee7be1818662377033301d", "5ceea794713d7f0067146d53", "5cef175aba1ae2ee1c5e13a0", "5cee7d99fd1162a9b6cf734b", "5cee7d99fd1162a9b6cf7358", "5cef1d219115385a19918a86", "5cef1e93b179d1b081358424", "5cee7f0d551a54921c919519", "5cef1e1d56f19a5df6358424", "5cef1f7cef0df1c851358424", "5cee7d99fd1162a9b6cf7350", "5cee7d99fd1162a9b6cf7354", "5cef1dd993ffdbc1fc35844f", "5cef1eb49f493d3a5f358426", "5cef17d3fc1ac10c0f5e139f", "5cef1dd993ffdbc1fc35844a", "5cef1eb49f493d3a5f358425", "5cef1dd993ffdbc1fc35844c", "5cef1dd993ffdbc1fc35844b", "5cef1da7cd5aa21f1a35842c", "5cef1da7cd5aa21f1a35842f", "5cef1da3b71d6032b2358424", "5cee893344fbcaef2b45abd5", "5ceea1f2b498e85d6c5cac5a", "5cef1e98bd89676bcd358424", "5cee9b5fe7e4d067e48253dd", "5ceea1e7cb2679c68b5cac5a", "5cee9426d40aaff0619596d8", "5cee87fc898d71033c45abd5", "5cee8725c406a2da54ca1da1", "5cef20d0bce76c7c1d71351a", "5cef0f3da94d15da4ab28984", "5cee886b284c12646745abd5", "5cee886b284c12646745abd6", "5cef20d7873f5e086571351a", "5cef18eb866c20d47aff27a8", "5cef0d9975ae658b1ab1fc0c", "5cef14ef896485ff9aedde8b", "5cee8d3e5f68410423d2c1a5", "5cef0f3d1a4839c46bb28984", "5ceea88d064c5d4739146d37", "5cee7d99fd1162a9b6cf735a", "5cef20f627e9905fee71351b", "5cee7d99fd1162a9b6cf735b", "5cef0fc2ae13c7e24bb28985", "5cef1fc4f206e750e4358424", "5cef0f3dc9ec01f7cdb28984", "5cef1da7cd5aa21f1a358426", "5cef1da7cd5aa21f1a358427", "5cee98511843dd4b145a4033", "5cef1e2aead6d0e13e358424", "5ceec33d110cf934c1cdbba2", "5cef22e058b469e62e3c60cf");
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> map = gson.fromJson(record.value(), mapType);
//                    if (idList.contains(map.get("id").toString())) {
//                        System.out.println("===================================================================================\n" + gson.toJson(map));
//                    }
                    int start = record.value().indexOf("\"ctime\":") + 6;
                    int end = record.value().indexOf(",\"operationType\":");
                    System.out.println("--------------------------------");
                    System.out.println(String.format("Consume partition:%d offset:%d value:%s", record.partition(), record.offset(), record.value()));
                    System.out.println(count++);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
