package com.lhk;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewsFeedApp {
    private static Gson gson = new Gson();

    private static AtomicBoolean isClosed = new AtomicBoolean(false);

    private static KafkaProducer<String, String> kafkaProducer;

    private static Set<String> keySet = new HashSet<>(Arrays.asList("id", "title", "content", "source", "url", "weight", "platform",
            "recommend", "type", "jpush", "status", "ctime"));

    private static void initData() {
        System.setProperty("java.security.auth.login.config", "/root/canal/canal-client/config/kafka_client_jaas.conf");
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "47.96.26.149:9092,47.96.27.99:9092,47.96.3.207:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 100);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 1024000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        kafkaProducer = new KafkaProducer<>(properties);
    }

    public static void main(String[] args) {

        initData();

        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(), 11111),
                "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe("NEWS_FEED\\..*");
            connector.rollback();
            while (!isClosed.get()) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        } finally {
            connector.disconnect();
        }
    }

    public static void shutDown() {
        isClosed.set(false);
    }

    private static void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            if ("lian_v1_article".equals(entry.getHeader().getTableName())) {
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    if (eventType == CanalEntry.EventType.DELETE) {
                        sendColumn(rowData.getBeforeColumnsList(), "delete");
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        sendColumn(rowData.getAfterColumnsList(), "insert");
                    } else if (eventType == CanalEntry.EventType.UPDATE) {
                        sendColumn(rowData.getAfterColumnsList(), "update");
                    }
                }
            }
        }
    }

    private static void sendColumn(List<CanalEntry.Column> columns, String operationType) {
        final Map<String, Object> sendMap = new HashMap<>();
        keySet.forEach(key -> sendMap.put(key, ""));
        try {
            Set<String> subjectIdSet = new HashSet<>();
            boolean isPushSSE = true;
            for (CanalEntry.Column column : columns) {
                if ("is_push_sse".equals(column.getName())) {
                    try {
                        isPushSSE = Boolean.valueOf(column.getValue());
                    } catch (RuntimeException e) {
                        System.err.println(e.getMessage());
                    }
                }
                if ("id".equals(column.getName())) {
                    System.out.println("Get ID SUCCESS: " + column.getValue());
                    if ("".equals(column.getValue())) {
                        return;
                    } else {
                        String sql = "select subject_id from lian_subject_article_assoc where article_id = '" + column.getValue() + "'";
                        Connection conn = MySQLApplicationService.getConnection();
                        try (Statement statement = conn.createStatement()) {
                            ResultSet resultSet = statement.executeQuery(sql);
                            while (resultSet.next()) {
                                subjectIdSet.add(resultSet.getString("subject_id"));
                            }
                        }
                        System.out.println("Subject Ids is: " + subjectIdSet);
                    }
                }
                if (keySet.contains(column.getName())) {
                    sendMap.put(column.getName(), column.getValue());
                    System.out.println(column.getName() + " : " + column.getValue());
                }
            }
            sendMap.put("tags", subjectIdSet);
            sendMap.put("supplier", "\u8d22\u8054\u793e");
            sendMap.put("operationType", operationType);
            System.out.println(sendMap);
            if (isPushSSE) {
                kafkaProducer.send(new ProducerRecord<>("origin-article", "msgCount", gson.toJson(sendMap)));
            }
            kafkaProducer.send(new ProducerRecord<>("origin-article-all", "msgCount", gson.toJson(sendMap)));
        } catch (SQLException e) {
            e.printStackTrace();
            MySQLApplicationService.resetConnection();
            System.out.println("ERROR: reset connection.....");
        }
    }
}
