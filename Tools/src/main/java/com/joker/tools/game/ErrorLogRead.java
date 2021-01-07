package com.joker.tools.game;

import cn.hutool.core.io.file.FileReader;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger("LogRead");

    public static void main(String[] args) {
        getRepeatSceneId();
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

    private static void getRepeatSceneId() {
        FileReader reader = new FileReader("C:/Users/Administrator/Desktop/online/s1/battle1_2/create_scene.txt");
        List<String> lines = reader.readLines();
        Set<Long> ids = new HashSet<>();
        for (String line : lines) {
            JsonObject json = new JsonObject(line);
            String log = json.getString("log");
            String[] split = log.split(", ");
            String sceneIdStr = split[1];
            long sceneId = Long.parseLong(sceneIdStr.split(":")[1]);
            if (ids.contains(sceneId)) {
                logger.info("repeated scene id:{}", sceneId);
            } else {
                ids.add(sceneId);
            }
        }
        logger.info("line size:{}, sceneId set size:{}", lines.size(), ids.size());
    }
}
