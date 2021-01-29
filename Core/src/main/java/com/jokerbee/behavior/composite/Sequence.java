package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.IAction;
import com.jokerbee.behavior.IComposite;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

/**
 * 顺序执行控制;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 22:14
 * @version: 1.0
 */
public class Sequence extends AbstractComposite {

    @Override
    public void tick() {

    }

    @Override
    public IAction nextAction() {
        return null;
    }
}
