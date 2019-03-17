package com.test;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class HTMLFormatUtils {
    // 定义script
    // 的正则表达式{或<script[^>]*?>[//s//S]*?<///script> 清除所有的script标签以及内容
    private final static Pattern SCRIPT_PATTERN = Pattern.compile("<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>");

    // 定义style
    // 的正则表达式{或<style[^>]*?>[//s//S]*?<///style> 清除所有的style标签以及内容
    private final static Pattern STYLE_PATTERN = Pattern.compile("<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>");

    private final static Pattern SLASH_PATTERN = Pattern.compile("(\\\\r|\\\\n)");
    private final static Pattern MORE_ENTER_PATTERN = Pattern.compile("(\n\n|\r)");
    private final static Pattern SIMPLE_ENTER_PATTERN = Pattern.compile("\n");

    private final static Pattern HTML_PATTERN_PAGE = Pattern.compile("</p>");
    // 定义html的的正则表达式，清除html标签，html的注解以及html的空格换行换页符
    private final static Pattern HTML_PATTERN_1 = Pattern.compile("<[^<>]+>");
    private final static Pattern HTML_PATTERN_2 = Pattern.compile("<!--[^<>]+-->");
    private final static Pattern HTML_PATTERN_3 = Pattern.compile(" ");


    /**
     * 过滤文本中的所有 html标签
     *
     * @param htmlStr
     * @return
     */
    public static String filterHtml(String htmlStr) {
        if (StringUtils.isBlank(htmlStr)) {
            return "";
        }

        htmlStr = SCRIPT_PATTERN.matcher(htmlStr).replaceAll("");
        htmlStr = STYLE_PATTERN.matcher(htmlStr).replaceAll("");
        htmlStr = SLASH_PATTERN.matcher(htmlStr).replaceAll("\n");
        htmlStr = HTML_PATTERN_PAGE.matcher(htmlStr).replaceAll("\n");
        htmlStr = MORE_ENTER_PATTERN.matcher(htmlStr).replaceAll("\n");
        htmlStr = SIMPLE_ENTER_PATTERN.matcher(htmlStr).replaceAll("\r\n");
        htmlStr = HTML_PATTERN_1.matcher(htmlStr).replaceAll("");
        htmlStr = HTML_PATTERN_2.matcher(htmlStr).replaceAll("");
        htmlStr = HTML_PATTERN_3.matcher(htmlStr).replaceAll("");
        return  htmlStr;
    }
}
