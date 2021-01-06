package com.jokerbee.behavior;


/**
 * 行为动作接口;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 17:43
 * @version: 1.0
 */
public interface IAction extends IBehaviorNode {

    /**
     * 当节点状态不是 RUNNING 时, 会调用 onEnter;
     *
     * @param input 行为参数
     */
    void onEnter(Object input);

    /**
     * 当节点状态时 RUNNING 时, 会直接调用 execute, 并且判断是否满足条件;
     *
     * @param input 行为参数
     * @param output 返回参数
     * @return
     */
    RunStatus execute(Object input, Object output);

    /**
     * 当执行成功或者失败时, 从 RUNNING 状态退出;
     *
     * @param input 行为参数
     * @param status 结束状态
     */
    void onExit(Object input, RunStatus status);
}
