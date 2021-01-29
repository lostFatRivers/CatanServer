package com.jokerbee.behavior.action;

import com.jokerbee.behavior.IAction;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

/**
 * 基础行为动作;
 *
 * @author: Joker
 * @date: Created in 2021/1/26 11:29
 * @version: 1.0
 */
public abstract class AbstractAction<T> extends AbstractBehaviorNode implements IAction {
    /** 行为对象 */
    private final T host;

    public AbstractAction(T host) {
        this.host = host;
    }

    public T getHost() {
        return host;
    }
}
