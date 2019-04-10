package com.lhk.kafka;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * @author LHK
 */

public class ConsumerDemo {

    public static void main(String[] args) {
        Properties properties = new Properties();
        System.setProperty("java.security.auth.login.config", "C:/software/kafka_2.11-2.1.1/config/kafka_client_jaas.conf");
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "cls-owl-article");
        properties.put("enable.auto.commit", "false");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("testTopic"));
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    if (record.value().contains("334746")) {
                        System.out.println("" + gson.fromJson(record.value(), mapType));
                    }
                    System.out.println("--------------------------------");
                    System.out.println(String.format("Consume partition:%d offset:%d value:%s", record.partition(), record.offset(), record.value()));
                }
                kafkaConsumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}