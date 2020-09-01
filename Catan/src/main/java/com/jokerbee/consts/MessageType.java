package com.jokerbee.consts;

public interface MessageType {
    int SC_ERROR_CODE = 110;

    // 玩家进入
    int CS_PLAYER_ENTER = 1001;
    int SC_PLAYER_ENTER = 1002;

    // 房间同步
    int CS_SYNC_ROOM = 1003;
    int SC_SYNC_ROOM = 1004;

    // 房间创建
    int CS_CREATE_ROOM = 1005;

    // 退出房间
    int CS_EXIT_ROOM = 1007;
    int SC_EXIT_ROOM = 1008;

    // 删除房间
    int CS_DELETE_ROOM = 1009;
    int SC_DELETE_ROOM = 1010;

    // 删除房间
    int CS_JOIN_ROOM = 1011;
    int SC_JOIN_ROOM = 1012;

    // 游戏开始
    int CS_START_GAME = 1013;
    int SC_START_GAME = 1014;

    // 颜色选择
    int CS_COLOR_SELECT = 1015;
    int SC_COLOR_SELECT = 1016;

    // 建城
    int CS_BUILD_CITY = 1017;
    int SC_BUILD_CITY = 1018;

    // 修路
    int CS_BUILD_ROAD = 1019;
    int SC_BUILD_ROAD = 1020;

    // 扔骰子
    int CS_THROW_DICE = 1021;
    int SC_THROW_DICE = 1022;

    // 结束操作
    int CS_TURN_NEXT_ONE = 1023;
    int SC_TURN_NEXT_ONE = 1024;

    // 房间状态改变
    int CS_SYNC_ROOM_STATUS = 1025;
    int SC_SYNC_ROOM_STATUS = 1026;

    // 改名
    int CS_CHANGE_NAME = 1027;
    int SC_CHANGE_NAME = 1028;

    // 同步玩家显示
    int CS_SYNC_ROLE_VIEW = 1029;
    int SC_SYNC_ROLE_VIEW = 1030;

    // 发起资源交换
    int CS_START_EXCHANGE = 1031;
    int SC_START_EXCHANGE = 1032;

    // 取消资源交换
    int CS_CLOSE_EXCHANGE = 1033;
    int SC_CLOSE_EXCHANGE = 1034;

    // 接受资源交换
    int CS_ACCEPT_EXCHANGE = 1035;
    int SC_ACCEPT_EXCHANGE = 1036;

    // 拒绝资源交换
    int CS_RESUME_EXCHANGE = 1037;
    int SC_RESUME_EXCHANGE = 1038;

    // 确认资源交换(完成)
    int CS_CONFIRM_EXCHANGE = 1038;
    int SC_CONFIRM_EXCHANGE = 1039;

    // 聊天内容
    int CS_SEND_CHAT = 1040;
    int SC_SEND_CHAT = 1041;

    // 最长路通知
    int SC_MAX_ROAD_LENGTH_NOTICE = 1043;

    // 最大士兵数通知
    int SC_MAX_ROB_TIMES_NOTICE = 1045;

    // 开始被抢
    int SC_SYSTEM_ROB = 1047;

    // 被抢
    int CS_ROB_OUT_SOURCE = 1048;
    int SC_ROB_OUT_SOURCE = 1049;

    // 被抢结束
    int SC_SYSTEM_ROB_FINISHED = 1051;

    // 海盗放在地块
    int CS_ROBBER_PUT_MAP = 1052;
    int SC_ROBBER_PUT_MAP = 1053;

    // 玩家选择了抢劫目标
    int CS_PLAYER_SELECTED_ROB_TARGET = 1054;
    int SC_PLAYER_SELECTED_ROB_TARGET = 1055;

    // 被抢劫玩家返回资源类型
    int CS_PLAYER_ROB_TARGET_BACK = 1056;
    int SC_PLAYER_ROB_TARGET_BACK = 1057;

    // 使用技能卡
    int CS_USE_SKILL_CARD = 1058;
    int SC_USE_SKILL_CARD = 1059;
}
