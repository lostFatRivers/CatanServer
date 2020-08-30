package com.jokerbee.consts;

/**
 * 静态常量;
 */
public interface Constants {
    /** websocket 路径 */
    String WEB_SOCKET_PATH = "/websocket";

    String API_SYNC_ROOM = "syncRooms";
    String API_CREATE_ROOM = "createRoom";

    String API_DELETE_ROOM_PRE = "deleteRoom_";
    String API_JOIN_ROOM_PRE = "joinRoom_";
    String API_EXIT_ROOM_PRE = "exitRoom_";
    String API_START_GAME_PRE = "startGame_";
    String API_SELECT_COLOR_PRE = "selectColor_";
    String API_THROW_DICE_PRE = "throwDice_";
    String API_SYNC_ROLE_PRE = "syncRole_";
    String API_BUILD_ROAD_PRE = "buildRoad_";
    String API_BUILD_CITY_PRE = "buildCity_";
    String API_TURN_NEXT_PRE = "turnNext_";
    String API_START_EXCHANGE_PRE = "startExchange_";
    String API_CLOSE_EXCHANGE_PRE = "closeExchange_";
    String API_ACCEPT_EXCHANGE_PRE = "acceptExchange_";
    String API_RESUME_EXCHANGE_PRE = "resumeExchange_";
    String API_CONFIRM_EXCHANGE_PRE = "confirmExchange_";
    String API_SEND_CHAT_PRE = "sendChat_";
    String API_SYS_ROB_OUT_PRE = "systemRobOut_";

    // 最长路的最短标准
    int MAX_ROAD_LENGTH_LIMIT = 4;
    // 最大士兵次数标准
    int MAX_ROB_TIMES_LIMIT = 2;

    // 被抢数量
    int ROB_DICE_NUMBER = 7;
}
