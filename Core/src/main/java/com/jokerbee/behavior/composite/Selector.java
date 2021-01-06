package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.IBehaviorNode;
import com.jokerbee.behavior.IComposite;
import com.jokerbee.behavior.RunStatus;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

/**
 * 选择控制;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 22:14
 * @version: 1.0
 */
public class Selector extends AbstractBehaviorNode implements IComposite {

    @Override
    protected boolean doEvaluate(Object input) {
        RunStatus status = RunStatus.FRESH;
        for (IBehaviorNode eachBh : children) {

        }
        return super.doEvaluate(input);
    }

    @Override
    public void tick() {

    }
}
