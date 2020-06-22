package com.jokerbee.state.anno;

import java.lang.annotation.*;

/**
 * on [state] 注解, 标注进入该状态时的回调方法.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface State {
    /**
     * 状态名;
     */
    String value();
}
