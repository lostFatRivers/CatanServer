package com.jokerbee.state.anno;

import java.lang.annotation.*;

/**
 * after [transition] 注解, 标注进入该状态时的回调方法.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transition {
    /**
     * 转换名;
     */
    String value();
}
