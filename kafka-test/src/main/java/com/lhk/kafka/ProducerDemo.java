package com.lhk.kafka;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LHK
 */
public class ProducerDemo {

    static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
//        System.setProperty("java.security.auth.login.config", "C:/tmp/prod/kafka_client_jaas.conf");
//        properties.put("bootstrap.servers", "47.96.26.149:9092,47.96.27.99:9092,47.96.3.207:9092");
        properties.put("bootstrap.servers", "192.168.0.240:9092");
//        String encode = Base64Utils.encodeToString(FileUtils.readFileToByteArray(new File("C:\\Users\\xww\\Desktop\\effectivejava-3rd.pdf")));
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 100);
        properties.put("linger.ms", 1);
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10485760);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("security.protocol", "SASL_PLAINTEXT");
//        properties.put("sasl.mechanism", "PLAIN");
        Producer<String, String> producer = null;
        Map<String, Object> map = new HashMap<>();
        map.put("id","1000");
        map.put("companyCode","CP_18833");
        map.put("companyName","北京兆泰集团股份有限公司");
        map.put("fileName","北京兆泰集团股份有限公司2019年4月月报");
        map.put("reportType","WEEKLY_REPORT");
        map.put("reportName","北京兆泰集团股份有限公司2019年4月月报.pdf");
//        map.put("reportSource",encode);
//        map.put("eventCode", "EVT00030");
//        map.put("company", "浙江仁智股份有限公司");
//        map.put("remarkStatus", "PASS");
//        map.put("articleIds", Arrays.asList("5cc2680ee2b7f2719133c634"));
//        map.put("originalTexts", Arrays.asList("卖壳梦断股价打回原形，仁智股份祸不单行被立案调查 30分钟前"));
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("result", map);
        requestMap.put("status", "SUCCESS");
        try {
            producer = new KafkaProducer<>(properties);
            int i = 0;
            int maxSize = 10;
            while (i < maxSize) {
                i++;
                String uuid = UUID.randomUUID().toString();
                producer.send(new ProducerRecord<>("testTopic", uuid, gson.toJson(requestMap)));
                System.out.println("Sent: SUCCESS ....");
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