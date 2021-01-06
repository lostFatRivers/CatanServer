package com.joker.tools.battle;

import java.util.List;
import java.util.Map;

/**
 * 多人共斗房间.
 *
 * @author: Joker
 * @date: Created in 2020/12/25 9:58
 * @version: 1.0
 */
public class BattleRoom {
    /** 房间帧数 */
    private long frameIndex;
    /** 从开始到当前的所有帧信息 */
    private Map<Long, List<BattleFrame>> frameMap;

    /** 当前帧的所有操作 */
    private Map<Long, BattleFrame> currentFrames;

    public void onFrame() {

    }
}
