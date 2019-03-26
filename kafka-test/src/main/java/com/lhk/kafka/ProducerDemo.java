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
        Gson gson = new Gson();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 100);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 1024000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = null;
        try {
            producer = new KafkaProducer<>(properties);
            int i = 0;
            int maxSize = 10 * 3600;
            while (i <= maxSize) {
                Map<String, Object> respondMap = new HashMap<>(16);
                respondMap.put("id", "20190221");
                respondMap.put("title", "开心石楠");
                respondMap.put("content", "市委网信办全面开展网站平台涉保健品虚假宣传专项清理整治行动，即日起天津网" +
                        "信网正式推出涉保健品虚假宣传举报窗口，通过“88908890”热线电话和官方网站链接，为集中打击清理" +
                        "整顿保健品市场乱象提供线索。桂东电力产品价格下跌");
                respondMap.put("ctime", 1550739964);
                respondMap.put("url", "");
                respondMap.put("type", "create");
                respondMap.put("source", "test");
                i++;
                String msg = "This is Message " + i;
                producer.send(new ProducerRecord<>("article_sq", "msgCount", gson.toJson(respondMap)));
                System.out.println("Sent:" + msg);
                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            producer.close();
        }

    }

}