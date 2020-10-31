package com.jokerbee.handler;

import com.jokerbee.anno.MessageHandler;
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
    private static final Logger logger = LoggerFactory.getLogger("Handler");

    private String scanPath;
    private final Map<Integer, Method> handlerMap = new HashMap<>();
    private final Map<Integer, Object> hostMap = new HashMap<>();

    public static HandlerManager getInstance() {
        return INSTANCE;
    }

    public void init(String scanPath) throws Exception {
        this.scanPath = scanPath;
        scanHandlers();
    }

    private void scanHandlers() throws Exception {
        Reflections reflections = new Reflections(scanPath);
        Set<Class<? extends AbstractModule>> handlerClassSet = reflections.getSubTypesOf(AbstractModule.class);
        for (Class<? extends AbstractModule> eachClass : handlerClassSet) {
            logger.info("new instance handler:{}", eachClass.getName());
            newInstance(eachClass);
        }
    }

    private void newInstance(Class<?> clazz) throws Exception {
        if (clazz == null) {
            return;
        }
        Object instance = clazz.getDeclaredConstructor().newInstance();

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

    public void onProtocol(IMessageConsumer consumer, JsonObject message) {
        int type = message.getInteger("type");
        Method method = handlerMap.get(type);
        if (method == null) {
            logger.error("protocol message not have handler method:{}", type);
            return;
        }
        consumer.getContext().runOnContext(v -> {
            try {
                method.invoke(hostMap.get(type), consumer, message);
            } catch (Exception e) {
                logger.error("protocol message handle error:{}", type, e);
            }
        });
    }

}
