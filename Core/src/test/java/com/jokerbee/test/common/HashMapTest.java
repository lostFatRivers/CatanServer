package com.jokerbee.test.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngineManager;
import java.util.HashMap;

public class HashMapTest {
    protected static Logger logger = LoggerFactory.getLogger("TEST");


    public static void main(String[] args) {
        strTest();
    }

    public static void strTest() {
        String name = """
                {
                    "12": 22
                }
                """;
        logger.info("Name:\n{}", name);
    }
}
