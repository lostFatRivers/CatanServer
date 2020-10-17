package com.jokerbee.anno;

import java.lang.annotation.*;

/**
 * 配置表文件数据源;
 *
 * @author: Joker
 * @date: Created in 2020/10/17 16:03
 * @version: 1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSource {
    String path();
}