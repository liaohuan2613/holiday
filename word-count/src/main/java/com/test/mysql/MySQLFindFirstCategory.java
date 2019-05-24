package com.test.mysql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class MySQLFindFirstCategory {
    private static Gson gson = new Gson();

    private static Type listMapType = new TypeToken<List<Map<String, Object>>>() {
    }.getType();

    public static void main(String[] args) {
        try {
            String tableName = "POC_NEWS_ANXIN";
            Connection conn = MySQLApplicationService.getConnection(0);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select ID, STOCK_TAGS, CONCEPT_TAGS, INDUSTRY_TAGS, REGION_TAGS, " +
                    " NEWS_TAGS from " + tableName);
            PreparedStatement ps = conn.prepareStatement("update " + tableName + " set FIRST_TAG_CATEGORY = ? where ID = ?");
            int count = 0;
            while (resultSet.next()) {
                count++;
                String id = resultSet.getString("ID");
                List<Map<String, Object>> stockTags = gson.fromJson(resultSet.getString("STOCK_TAGS"), listMapType);
                List<Map<String, Object>> conceptTags = gson.fromJson(resultSet.getString("CONCEPT_TAGS"), listMapType);
                List<Map<String, Object>> industryTags = gson.fromJson(resultSet.getString("INDUSTRY_TAGS"), listMapType);
                List<Map<String, Object>> regionTags = gson.fromJson(resultSet.getString("REGION_TAGS"), listMapType);
                List<Map<String, Object>> newsTags = gson.fromJson(resultSet.getString("NEWS_TAGS"), listMapType);
                double maxWeight = 0;
                String firstTagCategory = "OTHER";
                double thisWeight = maxWeight(stockTags);
                if (thisWeight > maxWeight) {
                    firstTagCategory = "STOCK";
                    maxWeight = thisWeight;
                }
                thisWeight = maxWeight(conceptTags);
                if (thisWeight > maxWeight) {
                    firstTagCategory = "CONCEPT";
                    maxWeight = thisWeight;
                }
                thisWeight = maxWeight(industryTags);
                if (thisWeight > maxWeight) {
                    firstTagCategory = "INDUSTRY";
                    maxWeight = thisWeight;
                }
                thisWeight = maxWeight(regionTags);
                if (thisWeight > maxWeight) {
                    firstTagCategory = "REGION";
                    maxWeight = thisWeight;
                }
                thisWeight = maxWeight(newsTags);
                if (maxWeight(newsTags) > maxWeight) {
                    firstTagCategory = "NEWS";
                    maxWeight = thisWeight;
                }
                ps.setString(1, firstTagCategory);
                ps.setString(2, id);
                ps.addBatch();
                if (count % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double maxWeight(List<Map<String, Object>> tagList) {
        double maxWeight = 0;
        for (Map<String, Object> tag : tagList) {
            double thisWeight = (double) tag.get("weight");
            if (thisWeight > maxWeight) {
                maxWeight = thisWeight;
            }
        }
        return maxWeight;

    }
}
