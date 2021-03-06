package com.lhk.cms.dao;

import java.util.List;
import java.util.Map;

public interface IBaseDao {
    //保存评论对象
    public void saveBean(String sql);

    //查询所有评论对象
    public List<Map<String, Object>> findAllBeans(String sql);

    //查询所有评论对象
    public List<Map<String, Object>> findAllBeansByOtherId(String sql);

    //查询所有评论对象
    public Map<String, Object> findBeanById(String sql);

    public void updateBeans(String sql);

    public void exeSql(String sql);

    public Map<String, Object> exeMapQuery(String sql);

    public List<Map<String, Object>> exeListQuery(String sql);
}
