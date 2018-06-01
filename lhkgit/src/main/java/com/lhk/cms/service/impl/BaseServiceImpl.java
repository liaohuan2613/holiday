package com.lhk.cms.service.impl;

import com.lhk.cms.dao.BaseDao;
import com.lhk.cms.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    BaseDao baseDao;

    @Override
    public void saveBean(String sql) {
        baseDao.saveBean(sql);
    }

    @Override
    public List<Map<String, Object>> findAllBeans(String sql) {
        return baseDao.findAllBeans(sql);
    }

    @Override
    public List<Map<String, Object>> findAllBeansByOtherId(String sql) {
        return baseDao.findAllBeansByOtherId(sql);
    }

    @Override
    public Map<String, Object> findBeanById(String sql) {
        return baseDao.findBeanById(sql);
    }

    @Override
    public void updateBean(String sql) {
        baseDao.updateBean(sql);
    }

    @Override
    public void exeSql(String sql) {
        baseDao.exeSql(sql);
    }

    @Override
    public Map<String, Object> exeMapQuery(String sql) {
        return baseDao.exeMapQuery(sql);
    }

    @Override
    public List<Map<String, Object>> exeListQuery(String sql) {
        return baseDao.exeListQuery(sql);
    }
}
