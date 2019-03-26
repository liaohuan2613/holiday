package com.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class HTMLBuilder {


    private String htmlHeader = "<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>\n";
    private StringBuilder htmlBody = new StringBuilder();
    private Set<String> nameSet = new LinkedHashSet<>();
    private String fileOutputPath = "tmp.html";

    public HTMLBuilder(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
    }

    public HTMLBuilder(String htmlHeader, String fileOutputPath) {
        this.htmlHeader = htmlHeader;
        this.fileOutputPath = fileOutputPath;
    }

    public HTMLBuilder() {
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public void setHtmlHeader(String htmlHeader) {
        this.htmlHeader = htmlHeader;
    }

    public String getFileOutputPath() {
        return fileOutputPath;
    }

    public void setFileOutputPath(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
    }

    public void build() {
        FileOutputStream fos = null;
        try {
            StringBuilder tempStringBuilder = new StringBuilder();
            for (String name : nameSet) {
                tempStringBuilder.append("<div id='").append(name).append("' style='width:100%;height:550px;margin: 5% 0;'></div>");
            }
            tempStringBuilder.append("<script type='text/javascript' src='echarts.js' ></script>\n<script type='text/javascript'>\n");
            tempStringBuilder.append("var colors = ['#c1232b', '#27727b', '#fcce10', '#e87c25', '#b5c334', '#fe8463', '#9bca63', '#fad860', '#f3a43b', '#60c0dd', '#d7504b', '#c6e579', '#f4e001', '#f0805a', '#26c0c0'];\n");
            fos = new FileOutputStream(fileOutputPath);
            fos.write(htmlHeader.getBytes());
            fos.write(("<body>" + tempStringBuilder.toString() + htmlBody.toString() + "</script></body></html>").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String printHtml() {
        StringBuilder tempStringBuilder = new StringBuilder();
        for (String name : nameSet) {
            tempStringBuilder.append("<div id='").append(name).append("' style='width:100%;height:550px;margin: 5% 0;'></div>");
        }
        tempStringBuilder.append("<script type='text/javascript' src='echarts.js' ></script>\n<script type='text/javascript'>\n");
        tempStringBuilder.append("var colors = ['#c1232b', '#27727b', '#fcce10', '#e87c25', '#b5c334', '#fe8463', '#9bca63', '#fad860', '#f3a43b', '#60c0dd', '#d7504b', '#c6e579', '#f4e001', '#f0805a', '#26c0c0'];\n");

        return tempStringBuilder.toString() + htmlBody.toString() + "</script>";
    }

    public HTMLBuilder drawScatterChart(String title, String subtext, String name, String xAxisName, String
            yAxisName, String circleName, Set<String> itemSet, Map<String, List<Object>> seriesMap, int maxSize) {
        if (nameSet.contains(name)) {
            name = new Date().getTime() + "";
        }
        nameSet.add(name);
        boolean first = true;
        StringBuilder seriesStr = new StringBuilder();
        for (Map.Entry<String, List<Object>> entry : seriesMap.entrySet()) {
            if (first) {
                seriesStr = new StringBuilder("{name: '" + entry.getKey() + "', type: 'scatter', data: [" + entry.getValue() + "]}\n");
                first = false;
            } else {
                seriesStr.append(",{name: '").append(entry.getKey()).append("', type: 'scatter', data: [").append(entry.getValue()).append("]}\n");
            }
        }

        StringBuilder itemHtml = new StringBuilder();
        int itemCount = 0;
        for (String item : itemSet) {
            itemHtml.append(item).append("：' + value[").append(itemCount).append("] + '<br>");
            itemCount++;
        }

        String tempHtmlBody = "var " + name + " = echarts.init(document.getElementById('" + name + "'));\n" +
                "var " + name + "Options = {title : {text: '" + title + "',x:'left', subtext: '" + subtext + "' },\n" +
                "color: colors,\n" +
                "toolbox: {feature: {saveAsImage: {}}},\n" +
                "legend: {type: 'scroll', right: 200, left: 200},\n" +
                "grid: {x: '10%',x2: 150,y: '18%',y2: '10%'},\n" +
                "tooltip: {padding: 10,backgroundColor: '#222',borderColor: '#777',borderWidth: 1,\n" +
                "formatter: function (obj) {\n" +
                "var value = obj.value;\n" +
                "return '<div style=\"border-bottom: 1px solid rgba(255,255,255,.3); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px\">'+obj.seriesName+'</div>" +
                itemHtml + "';}},\n" +
                "xAxis: {type: 'value',name: '" + xAxisName + "',splitLine: {show: false}},\n" +
                "yAxis: {type: 'value',name: '" + yAxisName + "',splitLine: {show: false}},\n" +
                "visualMap: [{left: 'right',top: '10%',dimension: 2,min: 0,max: " + maxSize + ",itemWidth: 30,itemHeight: 120," +
                "calculable: true,precision: 0.1,text: ['圆形大小：" + circleName + "'],textGap: 30,textStyle: {color: '#000'},\n" +
                "inRange: {symbolSize: [10, 70]},\n" +
                "outOfRange: {symbolSize: [10, 70],color: ['rgba(0,0,0,.2)']},\n" +
                "controller: {inRange: {color: ['#c23531']},outOfRange: {color: ['#444']}}}],\n" +
                "series: [" + seriesStr + "]};\n" +
                name + ".setOption(" + name + "Options);";
        htmlBody.append(tempHtmlBody);
        return this;
    }

    private static String getStringFromSet(Set<String> dataSet) {
        String resultDataStr = "";
        boolean isFirst = true;
        for (String data : dataSet) {
            if (isFirst) {
                isFirst = false;
                resultDataStr = "'" + data + "'";
            } else {
                resultDataStr += ",'" + data + "'";
            }
        }
        return resultDataStr;
    }

    public HTMLBuilder drawCombinationChart(String title, String subtext, String name, Set<String> legendDataSet,
                                            Set<String> xAxisDataSet, Map<String, String> seriesMap) {
        if (nameSet.contains(name)) {
            name = "option" + new Date().getTime();
        }
        nameSet.add(name);
        String legendDataStr = getStringFromSet(legendDataSet);

        String xAxisDataStr = getStringFromSet(xAxisDataSet);
        StringBuilder seriesStr = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : seriesMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
                seriesStr.append("{name:'").append(entry.getKey()).append("',type:'bar',stack: 'all',smooth: true,data: ").append(entry.getValue()).append("}").toString();
            } else {
                seriesStr.append(",\n{name:'").append(entry.getKey()).append("',type:'bar',stack: 'all',smooth: true,data: ").append(entry.getValue()).append("}");
            }
        }
        String tempHtmlBody = "var " + name + " = echarts.init(document.getElementById('" + name + "'));\n" +
                "var " + name + "Options = {title : {text: '" + title + "',x:'left', subtext: '" + subtext + "' },\n" +
                "color: colors,\n" +
                "toolbox: {show: true,feature: {dataView: {readOnly: false},magicType: {type: ['line', 'bar', 'stack', 'tiled']},restore: {},saveAsImage: {}}},\n" +
                "dataZoom: [{type: 'slider',xAxisIndex: 0,filterMode: 'empty'},{type: 'inside',xAxisIndex: 0,filterMode: 'empty'}],\n" +
                "tooltip: {trigger: 'axis'},\n" +
                "legend: {type: 'scroll', right: 200, left: 200,\n" +
                "data:[" + legendDataStr + "]},\n" +
                "grid: {top: 70,bottom: 50},\n" +
                "xAxis: {type: 'category',data: [" + xAxisDataStr + "]" + axisLabel(xAxisDataSet.size()) + "},\n" +
                "yAxis: {type: 'value'},\n" +
                "series: [" + seriesStr + "]};\n" +
                name + ".setOption(" + name + "Options);";
        htmlBody.append(tempHtmlBody);
        return this;
    }

    private String axisLabel(int axisSize) {
        if (axisSize < 8 || axisSize > 20) {
            return "";
        } else {
            return ", axisLabel:{interval:0,rotate:-30}";
        }
    }
}
