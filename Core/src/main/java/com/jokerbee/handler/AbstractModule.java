package com.jokerbee.handler;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModule {
    protected static Logger logger = LoggerFactory.getLogger("Handler");

    protected Vertx vertx;

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
}
