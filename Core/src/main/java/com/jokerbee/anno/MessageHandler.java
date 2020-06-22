package com.jokerbee.anno;

import java.lang.annotation.*;

/**
 * Created by Joker on 2017/4/24.
 * message handler interface mark method；
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {
    /**
     * 消息code;
     */
    int code();
}
