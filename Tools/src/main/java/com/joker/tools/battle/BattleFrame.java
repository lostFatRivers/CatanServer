package com.joker.tools.battle;

import io.vertx.core.json.JsonObject;

/**
 * 共斗帧信息;
 *
 * @author: Joker
 * @date: Created in 2020/12/25 10:06
 * @version: 1.0
 */
public class BattleFrame {
    /** 帧索引 */
    private long frameIndex;
    /** 玩家id */
    private long playerId;
    /** 位置同步 */
    private int x;
    private int y;
    private int z;
    private int rotation;
    /** 操作类型 */
    private int inputType;
    /** 操作参数 */
    private JsonObject inputParams;
}
