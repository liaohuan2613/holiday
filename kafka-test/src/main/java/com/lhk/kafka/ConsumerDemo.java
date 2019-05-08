package com.lhk.kafka;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

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
//        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "test-update-remark-status");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
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
        kafkaConsumer.subscribe(Collections.singletonList("abcd"));
        try {
            int count = 0;
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
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
