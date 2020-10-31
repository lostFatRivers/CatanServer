package com.jokerbee.handler;

import io.vertx.core.Context;

/**
 * 消息消费者;
 *
 * @author: Joker
 * @date: Created in 2020/10/31 11:36
 * @version: 1.0
 */
public interface IMessageConsumer {

    Context getContext();

}
