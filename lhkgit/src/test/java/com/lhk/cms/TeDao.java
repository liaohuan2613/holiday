package com.lhk.cms;

import com.lhk.cms.dao.IBaseDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TeDao {
    @Before
    public void before() {
        String[] path = {"/spring.xml", "spring-mybatis.xml", "spring-mvc.xml"};

        ApplicationContext ac = new ClassPathXmlApplicationContext(path);
        String[] name = ac.getBeanDefinitionNames();

        // 用于获取到当前mvc容器中的所有的bean的name
        for (int i = 0; i < name.length; i++)
            System.out.println(name[i]);

        IBaseDao baseDao = (IBaseDao) ac.getBean("IBaseDao");
        System.out.println(baseDao);
        System.out.println(baseDao.findAllBeans("select * from tbl_user"));
    }

    @Test
    public void test() {
        try {
            System.out.println("1234212");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
