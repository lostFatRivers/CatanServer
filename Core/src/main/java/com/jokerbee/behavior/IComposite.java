package com.jokerbee.behavior;

/**
 * 组合节点接口;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 21:08
 * @version: 1.0
 */
public interface IComposite extends IBehaviorNode {

    IAction nextAction();
}
