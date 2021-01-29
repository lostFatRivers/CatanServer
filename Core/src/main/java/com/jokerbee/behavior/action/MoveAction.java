package com.jokerbee.behavior.action;

import com.jokerbee.behavior.BStatus;
import com.jokerbee.behavior.BehaviorContext;

/**
 * 移动行为
 *
 * @author: Joker
 * @date: Created in 2021/1/26 11:46
 * @version: 1.0
 */
public class MoveAction<T> extends AbstractAction<T> {

    public MoveAction(T host) {
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
