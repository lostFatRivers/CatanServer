package com.jokerbee.http.handler.impl;

import com.jokerbee.http.handler.AESDecodeHandler;
import com.jokerbee.util.AESUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESDecodeHandlerImpl implements AESDecodeHandler {
    private static Logger logger = LoggerFactory.getLogger("AESDecoder");
    private String encryptKey;

    public AESDecodeHandlerImpl(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    @Override
    public void handle(RoutingContext event) {
        String bodyStr = event.getBodyAsString();
        logger.info("AESDecodeHandler handle body: {}", bodyStr);
        try {
            String decodeBody = AESUtil.decrypt(bodyStr, this.encryptKey);
            logger.info("AESDecodeHandler decode body: {}", decodeBody);
            event.setBody(Buffer.buffer(decodeBody));
        } catch (Exception e) {
            e.printStackTrace();
        }
        event.next();
    }
}
