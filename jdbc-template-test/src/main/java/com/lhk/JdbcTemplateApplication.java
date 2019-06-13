package com.lhk;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.client.RestTemplate;

public class JdbcTemplateApplication {

    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {

    }

    private static void findOriginText() {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select title,content,category,block from Eastmoney");
        while (sqlRowSet.next()) {
            sqlRowSet.getString("title");
            sqlRowSet.getString("content");
            sqlRowSet.getString("category");
            sqlRowSet.getString("block");
        }
    }

    public static JdbcTemplate getJdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://47.96.3.207:3306/NEWS_FEED?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        hikariConfig.setUsername("canal");
        hikariConfig.setPassword("ui#xctk!mzOc$aOC");
        jdbcTemplate.setDataSource(new HikariDataSource(hikariConfig));
        return jdbcTemplate;
    }
}
