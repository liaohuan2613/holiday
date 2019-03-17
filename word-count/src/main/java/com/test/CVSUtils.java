package com.test;

import com.csvreader.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CVSUtils {
    //添加数据，添加完数据后，把这条数据保存到csv文件
    public static void addRecord() {
        String[] fieldNameGroup = new String[]{"id", "source", "pub_dt", "title", "content"};
//        writeCSV(list, "D:/tmp/tmp_csv.csv", fieldNameGroup);
    }

    public static void writeCSV(List<List<String>> dataSet, String csvFilePath, String[] csvHeaders) {
        try {
            //判断文件是否存在,存在则删除,然后创建新表格
            File tmp = new File(csvFilePath);
            if (tmp.exists()) {
                tmp.delete();
            }
            //定义路径，分隔符，编码
            CsvWriter csvWriter = new CsvWriter(csvFilePath, ',', Charset.forName("UTF-8"));

            //写表头
            csvWriter.writeRecord(csvHeaders);

            //遍历集合 写内容
            //获取类属性
            String[] csvContent = new String[csvHeaders.length];
            for (List<String> tempList : dataSet) {
                for (int i = 0; i < csvHeaders.length; i++) {
                    csvContent[i] = tempList.get(i);
                }
                csvWriter.writeRecord(csvContent);
            }
            csvWriter.close();
            System.out.println("<--------CSV文件写入成功-------->");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
