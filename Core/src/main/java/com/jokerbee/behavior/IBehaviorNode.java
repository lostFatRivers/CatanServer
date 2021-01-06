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
    RunStatus nodeState();

    /**
     * 节点行为是否有效, 可执行;
     *
     * @param input 行为参数
     * @return 是否有效
     */
    boolean evaluate(Object input);

    /**
     * 切换到另一个节点行为;
     *
     * @param input 运行参数
     */
    void transition(Object input);

    /**
     * 帧更新;
     */
    void tick();
}
