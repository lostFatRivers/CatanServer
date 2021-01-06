package com.jokerbee.behavior.node;

import com.jokerbee.behavior.IBehaviorNode;
import com.jokerbee.behavior.ICondition;
import com.jokerbee.behavior.RunStatus;

import java.util.List;

/**
 * 基础行为树节点;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 11:03
 * @version: 1.0
 */
public abstract class AbstractBehaviorNode implements IBehaviorNode {

    /** 节点名称 */
    private String name;

    protected RunStatus status = RunStatus.FRESH;

    /** 父亲节点 */
    protected IBehaviorNode parent;

    /** 子节点集合 */
    protected List<IBehaviorNode> children;

    /** 外部前置条件 */
    protected ICondition precondition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 内部前置条件;
     *
     * @param input
     * @return
     */
    protected boolean doEvaluate(Object input) {
        return true;
    }

    @Override
    public boolean evaluate(Object input) {
        return doEvaluate(input) && (precondition == null || precondition.evaluate(input));
    }

    @Override
    public RunStatus nodeState() {
        return status;
    }

    @Override
    public void transition(Object input) {

    }
}
