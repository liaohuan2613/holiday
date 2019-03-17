package com.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateHtmlUtil {
    public static void main(String[] args) {
        String checkValue = "20190102 20:02:29开始";
        int index = 0;
        boolean existSpecialCharacter = false;

        for (char c : checkValue.toCharArray()) {
            if (c >= '0' && '9' >= c) {
                index++;
            } else if (c == '.' || c == ':' || c == '-' || c == ' ') {
                index++;
                existSpecialCharacter = true;
            } else {
                break;
            }
        }
        if (index >= 5 || existSpecialCharacter) {
            System.out.println(checkValue.substring(index, checkValue.length()));
        }
    }


    public static String createBarGraph(String title, String subtext, String name, Set<String> products, Map<String, Map<String, Object>> sourceMap) {
        String productStr = "";
        for (String product : products) {
            productStr += ",'" + product + "'";
        }
        String sourceStr = "";
        boolean isFirst = true;
        for (Map.Entry<String, Map<String, Object>> entry : sourceMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
                sourceStr = "{product: '" + entry.getKey() + "'";
            } else {
                sourceStr += ",\n{product: '" + entry.getKey() + "'";
            }
            for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
                sourceStr += ",'" + entry2.getKey() + "':" + entry2.getValue();
            }
            sourceStr += "}";
        }
        String barStr = "{type: 'bar'}";
        for (int i = 1; i < products.size(); i++) {
            barStr += ",\n{type: 'bar'}";
        }
        String htmlBody = "var " + name + " = echarts.init(document.getElementById('" + name + "'));\n" +
                "var " + name + "Options = {title : {text: '" + title + "', x:'left', subtext: '" + subtext + "' },\n" +
                "toolbox: {feature: {saveAsImage: {}}},\n" +
                "legend: {type: 'scroll', right: 200, left: 200},\n" +
                "tooltip: {},\n" +
                "dataset: {dimensions: ['product'" + productStr + "],\n" +
                "source: [\n" + sourceStr + "]},\n" +
                "xAxis: {type: 'category', axisLabel:{interval:0,rotate:-30}},\n" +
                "yAxis: {},\n" +
                "series: [" + barStr + "]\n" +
                "};\n" +
                name + ".setOption(" + name + "Options);";
        return htmlBody;
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

    public static String createLineChart(String title, String subtext, String name, Set<String> legendDataSet, Set<String> xAxisDataSet, Map<String, String> seriesMap) {
        String legendDataStr = getStringFromSet(legendDataSet);

        String xAxisDataStr = getStringFromSet(xAxisDataSet);
        String seriesStr = "";
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : seriesMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
                seriesStr = "{name:'" + entry.getKey() + "',type:'line',smooth: true,data: [" + entry.getValue() + "]}";
            } else {
                seriesStr += ",\n{name:'" + entry.getKey() + "',type:'line',smooth: true,data: [" + entry.getValue() + "]}";
            }
        }
        String htmlBody = "var " + name + " = echarts.init(document.getElementById('" + name + "'));\n" +
                "var " + name + "Options = {title : {text: '" + title + "',x:'left', subtext: '" + subtext + "' },\n" +
                "toolbox: {feature: {saveAsImage: {}}},\n" +
                "tooltip: {trigger: 'axis'},\n" +
                "legend: {type: 'scroll', right: 200, left: 200,\n" +
                "data:[" + legendDataStr + "]},\n" +
                "grid: {top: 70,bottom: 50},\n" +
                "toolbox: {feature: {saveAsImage: {}}},\n" +
                "xAxis: {type: 'category',boundaryGap: false,data: [" + xAxisDataStr + "]},\n" +
                "yAxis: {type: 'value'},\n" +
                "series: [" + seriesStr + "]};\n" +
                name + ".setOption(" + name + "Options);";
        return htmlBody;
    }

    public static String createScatterChart(String title, String subtext, String name, String xAxisName, String yAxisName,
                                            String circleName, Set<String> itemSet, Map<String, List<Object>> seriesMap, int maxSize) {
        boolean first = true;
        String seriesStr = "";
        for (Map.Entry<String, List<Object>> entry : seriesMap.entrySet()) {
            if (first) {
                seriesStr = "{name: '" + entry.getKey() + "', type: 'scatter', data: [" + entry.getValue() + "]}\n";
                first = false;
            } else {
                seriesStr += ",{name: '" + entry.getKey() + "', type: 'scatter', data: [" + entry.getValue() + "]}\n";
            }
        }

        String itemHtml = "";
        int itemCount = 0;
        for (String item : itemSet) {
            itemHtml += item + "：' + value[" + itemCount + "] + '<br>";
            itemCount++;
        }

        String htmlBody = "var " + name + " = echarts.init(document.getElementById('" + name + "'));\n" +
                "var " + name + "Options = {title : {text: '" + title + "',x:'left', subtext: '" + subtext + "' },\n" +
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
        return htmlBody;
    }

    public static String outTag(String s) {
        return s.replaceAll("<.*?>", "");
    }

}
