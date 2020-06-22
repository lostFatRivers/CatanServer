package com.jokerbee.handler;

import com.jokerbee.anno.MessageHandler;
import com.jokerbee.player.Player;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public enum HandlerManager {
    INSTANCE;
    private static Logger logger = LoggerFactory.getLogger("Handler");

    private Vertx vertx;
    private Map<Integer, Method> handlerMap = new HashMap<>();
    private Map<Integer, Object> hostMap = new HashMap<>();

    public static HandlerManager getInstance() {
        return INSTANCE;
    }

    public void init(Vertx vertx) throws Exception {
        this.vertx = vertx;
        scanHandlers();
    }

    private void scanHandlers() throws Exception {
        Reflections reflections = new Reflections("com.jokerbee.handler.impl");
        Set<Class<? extends AbstractModule>> handlerClassSet = reflections.getSubTypesOf(AbstractModule.class);
        for (Class<? extends AbstractModule> eachClass : handlerClassSet) {
            logger.info("new instance handler:{}", eachClass.getName());
            newInstance(eachClass);
        }
    }

    private void newInstance(Class<? extends AbstractModule> clazz) throws Exception {
        if (clazz == null) {
            return;
        }
        AbstractModule instance = clazz.getDeclaredConstructor().newInstance();
        instance.setVertx(vertx);

        Method[] methods = clazz.getDeclaredMethods();
        Stream.of(methods).filter(each -> {
            MessageHandler annotation = each.getAnnotation(MessageHandler.class);
            return annotation != null;
        }).forEach(each -> {
            each.setAccessible(true);
            MessageHandler annotation = each.getAnnotation(MessageHandler.class);
            handlerMap.put(annotation.code(), each);
            hostMap.put(annotation.code(), instance);
        });
    }

    public void onProtocol(Player player, JsonObject message) {
        int type = message.getInteger("type");
        Method method = handlerMap.get(type);
        if (method == null) {
            logger.error("protocol message not have handler method:{}", type);
            return;
        }
        try {
            method.invoke(hostMap.get(type), player, message);
        } catch (Exception e) {
            logger.error("protocol message handle error:{}", type, e);
        }
    }

}
