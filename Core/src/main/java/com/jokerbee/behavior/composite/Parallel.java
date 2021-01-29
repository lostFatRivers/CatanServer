package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.BehaviorContext;
import com.jokerbee.behavior.IAction;
import com.jokerbee.behavior.IComposite;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

/**
 * 并行控制;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 22:14
 * @version: 1.0
 */
public class Parallel extends AbstractComposite {

    @Override
    protected boolean doEvaluate(BehaviorContext input) {

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
