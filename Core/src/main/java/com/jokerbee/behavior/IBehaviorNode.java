package com.jokerbee.behavior;

/**
 * 行为节点接口;
 *
 * @author: Joker
 * @date: Created in 2020/12/1 21:01
 * @version: 1.0
 */
public interface IBehaviorNode {

    /**
     * 节点执行状态;
     *
     * @return 状态枚举
     */
    BStatus nodeState();

    /**
     * 节点行为是否有效, 可执行;
     *
     * @param input 行为参数
     * @return 是否有效
     */
    boolean evaluate(BehaviorContext input);

    /**
     * 帧更新;
     */
    void tick();

    /**
     * 添加子节点;
     *
     * @param child 子节点, 控制节点/行为节点
     */
    void addChild(IBehaviorNode child);
}
