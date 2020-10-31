package com.jokerbee.support;

/**
 * 游戏常量
 *
 * @author: Joker
 * @date: Created in 2020/10/31 15:33
 * @version: 1.0
 */
public interface GameConstant {

    String API_CONNECT_ACTIVE = "connectActive";
    String API_CREATE_PLAYER = "createPlayer";
    String API_DESTROY_PLAYER = "destroyPlayer_";

    String API_TAIL_SOCKET_CLOSE = "_close";
    String API_TAIL_MESSAGE_DISPATCH = "_message_consume";

    String RESULT_SUCCESS = "success";

    String REDIS_ACCOUNT_TO_SOCKET = "account_to_socket_id";
}
