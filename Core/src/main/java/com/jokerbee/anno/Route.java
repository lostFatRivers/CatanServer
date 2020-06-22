package com.jokerbee.anno;

import java.lang.annotation.*;

/**
 * http router mark for class & method;
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface Route {
    /**
     * request path.
     * <p>
     * example: '/player/name'
     * </p>
     * @return path;
     */
    String value();
}
