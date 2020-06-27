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
}
