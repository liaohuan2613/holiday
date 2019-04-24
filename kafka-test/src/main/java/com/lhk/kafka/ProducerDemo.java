package com.lhk.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LHK
 */
public class ProducerDemo {

    static Gson gson = new Gson();

    public static void main(String[] args) {
        Properties properties = new Properties();
        System.setProperty("java.security.auth.login.config", "C:/tmp/prod/kafka_client_jaas.conf");
        properties.put("bootstrap.servers", "47.96.26.149:9092,47.96.27.99:9092,47.96.3.207:9092");
//        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 100);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 1024000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        Producer<String, String> producer = null;
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("title", "测试");
        map.put("content", "测试数据，请别在意");
        map.put("source", "测试环境");
        map.put("url", "http://www.test.com");
        map.put("weight", "0");
        map.put("platform", "web");
        map.put("recommend", "0");
        map.put("type", "-1");
        map.put("jpush", "0");
        map.put("status", "0");
        map.put("ctime", "1555917538");
        try {
            producer = new KafkaProducer<>(properties);
            int i = 0;
            int maxSize = 1;
            while (i < maxSize) {
                i++;
                producer.send(new ProducerRecord<>("test", "msgCount", gson.toJson(map)));
                System.out.println("Sent:" + gson.toJson(map));
//                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (producer != null) {
                producer.close();
            }
        }

    }

}