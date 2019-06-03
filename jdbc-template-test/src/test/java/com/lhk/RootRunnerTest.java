package com.lhk;

import org.junit.Test;

import java.util.*;

public class RootRunnerTest {

    @Test
    public void basicTest() {
        List<String> a = new ArrayList<>();
        a.add("1");
        a.add("2");
        a.add("3");
        a.add("4");
        Iterator<String> iterator = a.iterator();
        while (iterator.hasNext()) {
            if ("1".equals(iterator.next())) {
                System.out.println(1);
            }
        }

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("a", new String("a"));
        testMap.put("b", new String("b"));
        testMap.put("c", testMap.get("a"));
        testMap.remove("a");
        System.out.println(testMap);
    }
}
