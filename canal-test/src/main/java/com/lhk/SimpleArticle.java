package com.lhk;

public class SimpleArticle {
    private String id;
    private String title;
    private String content;
    private String source;
    private String url;
    private String weight;
    private String platform;
    private String type;
    private long ctime;
    private String recommend;
    private String jpush;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getJpush() {
        return jpush;
    }

    public void setJpush(String jpush) {
        this.jpush = jpush;
    }

    @Override
    public String toString() {
        return "SimpleArticle{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", weight='" + weight + '\'' +
                ", platform='" + platform + '\'' +
                ", type='" + type + '\'' +
                ", ctime=" + ctime +
                ", recommend='" + recommend + '\'' +
                ", jpush='" + jpush + '\'' +
                '}';
    }
}
