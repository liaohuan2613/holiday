package com.lhk;

import java.util.ArrayList;
import java.util.List;

public class TableOperation {
    private String tableName;
    private String syncDatetimeFiled = "last_modified_date";
    private int model = 0;
    private List<String> syncFiledList = new ArrayList<>();
    private List<String> cloudPrimaryFiledList = new ArrayList<>();
    private List<String> clientPrimaryFiledList = new ArrayList<>();
    private List<String> removeFiledList = new ArrayList<>();
    private List<String> replaceFromFiledList = new ArrayList<>();
    private List<String> replaceToFiledList = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSyncDatetimeFiled() {
        return syncDatetimeFiled;
    }

    public void setSyncDatetimeFiled(String syncDatetimeFiled) {
        this.syncDatetimeFiled = syncDatetimeFiled;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public List<String> getSyncFiledList() {
        return syncFiledList;
    }

    public void setSyncFiledList(List<String> syncFiledList) {
        this.syncFiledList = syncFiledList;
    }

    public List<String> getCloudPrimaryFiledList() {
        return cloudPrimaryFiledList;
    }

    public void setCloudPrimaryFiledList(List<String> cloudPrimaryFiledList) {
        this.cloudPrimaryFiledList = cloudPrimaryFiledList;
    }

    public List<String> getClientPrimaryFiledList() {
        return clientPrimaryFiledList;
    }

    public void setClientPrimaryFiledList(List<String> clientPrimaryFiledList) {
        this.clientPrimaryFiledList = clientPrimaryFiledList;
    }

    public List<String> getRemoveFiledList() {
        return removeFiledList;
    }

    public void setRemoveFiledList(List<String> removeFiledList) {
        this.removeFiledList = removeFiledList;
    }

    public List<String> getReplaceFromFiledList() {
        return replaceFromFiledList;
    }

    public void setReplaceFromFiledList(List<String> replaceFromFiledList) {
        this.replaceFromFiledList = replaceFromFiledList;
    }

    public List<String> getReplaceToFiledList() {
        return replaceToFiledList;
    }

    public void setReplaceToFiledList(List<String> replaceToFiledList) {
        this.replaceToFiledList = replaceToFiledList;
    }

    @Override
    public String toString() {
        return "TableOperation{" +
                "tableName='" + tableName + '\'' +
                ", syncDatetimeFiled='" + syncDatetimeFiled + '\'' +
                ", model=" + model +
                ", syncFiledList=" + syncFiledList +
                ", cloudPrimaryFiledList=" + cloudPrimaryFiledList +
                ", clientPrimaryFiledList=" + clientPrimaryFiledList +
                ", removeFiledList=" + removeFiledList +
                ", replaceFromFiledList=" + replaceFromFiledList +
                ", replaceToFiledList=" + replaceToFiledList +
                '}';
    }
}
