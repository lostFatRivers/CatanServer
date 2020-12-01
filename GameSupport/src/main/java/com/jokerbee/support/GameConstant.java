package com.jokerbee.support;

/**
 * 游戏常量
 *
 * @author: Joker
 * @date: Created in 2020/10/31 15:33
 * @version: 1.0
 */
public interface GameConstant {

    String API_ACCOUNT_BIND = "account_bind";
    String API_ACCOUNT_UNBIND = "account_unbind";
    String API_CREATE_PLAYER = "create_player";
    String API_SOCKET_CLOSE = "connectClose";

    String API_SERVER_TITLE = "request_server_";

    String API_TAIL_MESSAGE_DISPATCH = "_message_consume";
    String API_TAIL_SOCKET_SWAP = "_socket_swap";

    String RESULT_SUCCESS = "success";

    String REDIS_SERVER_ID = "server_auto_id";

    String REDIS_ACCOUNT_SERVER = "account_server_id";

    String DB_QUERY = "query_entity";
}
