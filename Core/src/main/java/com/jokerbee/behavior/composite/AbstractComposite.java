package com.jokerbee.behavior.composite;

import com.jokerbee.behavior.IBehaviorNode;
import com.jokerbee.behavior.IComposite;
import com.jokerbee.behavior.node.AbstractBehaviorNode;

import java.util.List;

/**
 * 基础控制节点;
 *
 * @author: Joker
 * @date: Created in 2021/1/26 14:36
 * @version: 1.0
 */
public abstract class AbstractComposite extends AbstractBehaviorNode implements IComposite {

    /** 子节点集合 */
    protected List<IBehaviorNode> children;

    @Override
    public void addChild(IBehaviorNode child) {
        children.add(child);
    }
}
