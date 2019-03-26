package com.lhk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LHK
 */

public class ConsumerDemo {

    private static Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);
    private static Gson gson = new Gson();
    private static final String REMOVAL_URL = "http://47.96.26.149:5002/api/deduplication";
    private static RestTemplate restTemplate = new RestTemplate();

    private static AtomicLong totalDupTime = new AtomicLong();
    private static AtomicLong totalInitTime = new AtomicLong();
    private static AtomicLong counter = new AtomicLong();


    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "39.96.24.162:9092");
        properties.put("group.id", "cls-owl-article");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("owl"));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<?, ?> record : records) {
                Optional<?> kafkaMessage = Optional.ofNullable(record.value());
                if (kafkaMessage.isPresent()) {
                    counter.getAndAdd(1);
                    long initStartTime = System.currentTimeMillis();

                    String recordValue = record.value().toString();
                    if ("\"\"".equals(recordValue) || "''".equals(recordValue) || "".equals(recordValue)) {
                        continue;
                    }
                    Type type = new TypeToken<Hashtable<String, Object>>() {
                    }.getType();
                    Map<String, Object> requestMap;
                    try {
                        requestMap = gson.fromJson(recordValue, type);
                    } catch (RuntimeException e) {
                        continue;
                    }
                    Object newsId = requestMap.get("id");
                    Object title = requestMap.get("title");
                    if (newsId == null || "".equals(newsId.toString()) || title == null || "".equals(title.toString())) {
                        continue;
                    }
                    requestMap.putIfAbsent("source", "财联社");
                    requestMap.putIfAbsent("content", title);
                    requestMap.putIfAbsent("database", "10");
                    long initEndTime = System.currentTimeMillis();
                    long initTime = initEndTime - initStartTime;

                    long dupStartTime = System.currentTimeMillis();
                    Object object = SimpleDupDemo.requestEntity(requestMap, REMOVAL_URL, restTemplate);
                    long dupEndTime = System.currentTimeMillis();
                    long dupTime = dupEndTime - dupStartTime;
                    totalInitTime.getAndAdd(initTime);
                    totalDupTime.getAndAdd(dupTime);

                    System.out.println("- - - - - -initTime: " + initTime + "- - - - - -dupTime: " + dupTime);


                    if (counter.get() % 1000 == 0) {
                        System.out.println("- - - - - - - - - - 第" + counter.get() / 1000 + "次 - - - - - - - - - - - - - -");
                        System.out.println("- - - - - -totalInitTime: " + totalInitTime.get() + "- - - - - -totalDupTime: " + totalDupTime.get());
                    }
                }
            }
            try {
                kafkaConsumer.commitSync();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
