package com.jokerbee.http.handler;

import com.jokerbee.http.handler.impl.AESDecodeHandlerImpl;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface AESDecodeHandler extends Handler<RoutingContext> {

    static AESDecodeHandler create(String encryptKey) {
        return new AESDecodeHandlerImpl(encryptKey);
    }
}
