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

    public static void main(String[] args) {
        Properties properties = new Properties();
        System.setProperty("java.security.auth.login.config", "C:/software/kafka_2.11-2.1.1/config/kafka_client_jaas.conf");
        Gson gson = new Gson();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
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
        try {
            producer = new KafkaProducer<>(properties);
            int i = 0;
            int maxSize = 10 * 3600;
            while (i <= maxSize) {
                i++;
                String msg = "This is Message " + i;
                producer.send(new ProducerRecord<>("article", "msgCount", msg));
                System.out.println("Sent:" + msg);
//                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }

    }

}