package com.test;

/**
 * @author CHEN FAN
 * @create 2018/9/12 15:18
 */
public class ArticleDuplicationResponseId {
    private String id;
    private String source;

    public ArticleDuplicationResponseId() {
    }

    public ArticleDuplicationResponseId(String id, String source) {
        this.id = id;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}