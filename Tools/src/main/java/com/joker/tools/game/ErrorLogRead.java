package com.joker.tools.game;

import cn.hutool.core.io.file.FileReader;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 异常日志抓取
 *
 * @author: Joker
 * @date: Created in 2020/12/30 1:39
 * @version: 1.0
 */
public class ErrorLogRead {
    public static void main(String[] args) {
        getAllTypeError();
    }

    private static void getAllTypeError() {
        FileReader reader = new FileReader("C:/Users/Administrator/Desktop/online/S4/errorlog.txt");
        List<String> lines = reader.readLines();
        Set<String> errors = new HashSet<>();
        for (String line : lines) {
            JsonObject json = new JsonObject(line);
            String exception = json.getString("exception");
            errors.add(exception);
        }
        errors.forEach(System.out::println);
    }
}
