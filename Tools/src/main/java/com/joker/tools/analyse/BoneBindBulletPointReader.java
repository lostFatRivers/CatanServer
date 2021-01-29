package com.joker.tools.analyse;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import com.jokerbee.util.StringUtil;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 绑定骨骼类型子弹的点读取;
 *
 * @author: Joker
 * @date: Created in 2021/1/27 16:35
 * @version: 1.0
 */
public class BoneBindBulletPointReader {
    private static final Logger logger = LoggerFactory.getLogger("Bullet");

    public static void main(String[] args) {
        logger.info("Bullet read start.");

        //readBonePoints();
        testString();

        List<String> list = StringUtil.replaceHolderExtract("小明是一条狗,小红是一头猪", "{0}是一条{1},{2}是一头{3}");
        logger.info("test line:{}", list);
    }

    private static void readBonePoints() {
        FileReader reader = new FileReader("F:/serverLog/Bone010_Points.txt");
        List<String> lines = reader.readLines();

        JsonArray timePoints = new JsonArray();

        lines.forEach(el -> {
            List<String> list = StringUtil.replaceHolderExtract(el, "[{0}ms]: x = {1}， y = {2}， z = {3}");
            long time = (long) (Float.parseFloat(list.get(0)) * 1000);
            float x = Float.parseFloat(list.get(1));
            float y = Float.parseFloat(list.get(2));
            float z = Float.parseFloat(list.get(3));

            JsonObject json = new JsonObject();
            json.put("time", time);
            json.put("point", new JsonArray().add(x).add(y).add(z));
            timePoints.add(json);
        });

        FileWriter writer = new FileWriter("F:/serverLog/bullet_54211.json");
        writer.write(timePoints.encodePrettily());
    }

    private static void testString() {
        String str = "从重量为百万分之一克的样品中提取有关行星宏观组成的信息是一个极为有趣的科学尝试实例";
        logger.info("replace:{}", str.replaceAll("样品.*?星宏", ","));
    }
}
