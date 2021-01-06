package com.jokerbee.behavior;

/**
 * 节点条件接口;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 11:31
 * @version: 1.0
 */
public interface ICondition extends IBehaviorNode {

    /**
     * 是否满足条件;
     *
     * @param input 行为参数
     * @return 是否满足
     */
    boolean evaluate(Object input);
}
