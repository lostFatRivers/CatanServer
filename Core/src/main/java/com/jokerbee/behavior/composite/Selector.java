package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.*;

/**
 * 选择控制;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 22:14
 * @version: 1.0
 */
public class Selector extends AbstractComposite implements IComposite {

    @Override
    protected boolean doEvaluate(BehaviorContext input) {
        BStatus status = BStatus.FRESH;
        for (IBehaviorNode eachBh : children) {

        }
        return super.doEvaluate(input);
    }

    @Override
    public void tick() {

    }

    @Override
    public IAction nextAction() {
        return null;
    }
}

