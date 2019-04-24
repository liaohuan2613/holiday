package com.test;

import java.sql.Connection;
import java.sql.Statement;

public class SplitMysqlData {
    public static void main(String[] args) throws Exception {
        int totalNum = 3452364;
        int tempNum = 2044000;
        int count = 0;
        while (tempNum < totalNum) {
            String sql = "insert into action_log_new(`id`,`user_category`,`user_id`,`item_id`,`action`,`plan_id`,`task_id`,`acted_at`,`time_span`,`value`," +
                    "`policy_id`,`extra`,`category`,`customerid`,`usercode`,`tdid`) " +
                    "select `id`,`user_category`,`user_id`,`item_id`,`action`,`plan_id`,`task_id`,`acted_at`,`time_span`,`value`,`policy_id`,`extra`," +
                    "SUBSTRING_INDEX(SUBSTRING_INDEX(user_id,':',2),':',-1) category," +
                    " SUBSTRING_INDEX(SUBSTRING_INDEX(user_id,':',4),':',-1) customerid ,SUBSTRING_INDEX(SUBSTRING_INDEX(user_id,':',6),':',-1) usercode," +
                    " SUBSTRING_INDEX(SUBSTRING_INDEX(user_id,':',8),':',-1) tdid from action_log limit " + tempNum + ",4000";
            splitInsertSql(sql);
            tempNum += 4000;
            System.out.println("count: " + ++count);
        }
        System.out.println("finished ....");
    }

    public static void splitInsertSql(String sql) throws Exception {
        Connection connection = MySQLApplicationService.getCollection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }
}
