package com.joker.tools.behavior;

/**
 * 行为树;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 14:24
 * @version: 1.0
 */
public class BehaviorTree implements ITreeNode {
    private final AbstractTreeNode root;

    public BehaviorTree(AbstractTreeNode node) {
        this.root = node;
    }

    @Override
    public TreeStatus update() {
        if (this.root.getStatus() == TreeStatus.READY) {
            this.root.onExecuted();
        }
        return root.update();
    }

    @Override
    public TreeStatus getStatus() {
        return root.getStatus();
    }
}
