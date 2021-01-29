package com.jokerbee.behavior.action;

import com.jokerbee.behavior.BStatus;
import com.jokerbee.behavior.BehaviorContext;

/**
 * TODO description
 *
 * @author: Joker
 * @date: Created in 2021/1/26 11:53
 * @version: 1.0
 */
public class IdleAction<T> extends AbstractAction<T> {

    public IdleAction(T host) {
        super(host);
    }

    @Override
    public void onEnter(BehaviorContext input) {

    }

    @Override
    public BStatus execute(BehaviorContext input) {
        return null;
    }

    @Override
    public void onExit(BehaviorContext input, BStatus status) {

    }

    @Override
    public void tick() {

    }
}
