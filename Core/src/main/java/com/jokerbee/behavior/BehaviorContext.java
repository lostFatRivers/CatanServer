package com.jokerbee.behavior;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 执行上下文, 参考 behavior3js 中的 Tick;
 *
 * @author: Joker
 * @date: Created in 2021/1/26 15:44
 * @version: 1.0
 */
public class BehaviorContext {
    private final BehaviorTree tree;
    private final boolean debug;
    private final Object target;
    private final Blackboard blackboard;

    private final Queue<IBehaviorNode> openNodes;
    private int nodeCount;

    public BehaviorContext(BehaviorTree tree, boolean debug, Object target, Blackboard blackboard) {
        this.tree = tree;
        this.debug = debug;
        this.target = target;
        this.blackboard = blackboard;
        this.openNodes = new LinkedList<>();
        this.nodeCount = 0;
    }

    public void enterNode(IBehaviorNode node) {
        this.nodeCount++;
        this.openNodes.offer(node);
    }

    public void exitNode(IBehaviorNode node) {
        this.openNodes.poll();
    }

    public BehaviorTree getTree() {
        return tree;
    }

    public boolean isDebug() {
        return debug;
    }

    public Object getTarget() {
        return target;
    }

    public Blackboard getBlackboard() {
        return blackboard;
    }

    public Queue<IBehaviorNode> getOpenNodes() {
        return openNodes;
    }

    public int getNodeCount() {
        return nodeCount;
    }
}
