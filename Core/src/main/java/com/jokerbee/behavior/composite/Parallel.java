package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.IComposite;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

/**
 * 并行控制;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 22:14
 * @version: 1.0
 */
public class Parallel extends AbstractBehaviorNode implements IComposite {

    @Override
    protected boolean doEvaluate(Object input) {

        return super.doEvaluate(input);
    }

    @Override
    public void tick() {

    }
}
