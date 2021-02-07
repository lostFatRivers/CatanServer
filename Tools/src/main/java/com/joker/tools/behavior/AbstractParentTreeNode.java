package com.joker.tools.behavior;

import java.util.ArrayList;
import java.util.List;

/**
 * 树父节点基类;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 11:43
 * @version: 1.0
 */
public abstract class AbstractParentTreeNode extends AbstractTreeNode {
    /** 子节点 */
    protected List<AbstractTreeNode> children = new ArrayList<>();

    public void addChild(AbstractTreeNode node) {
        node.setParent(this);
        children.add(node);
    }

    @Override
    public TreeStatus update() {
        AbstractTreeNode child = nextExecuteChild();
        if (child == null) {
            return this.status;
        }
        if (child.getStatus() == TreeStatus.READY) {
            child.onExecuted();
        }
        TreeStatus childStatus = child.update();
        onChildExecuted(childStatus);
        return this.status;
    }

    /**
     * 选择下一个要执行的子节点;
     *
     * @return 子节点
     */
    protected abstract AbstractTreeNode nextExecuteChild();

    /**
     * 子节点执行完毕后的回调;
     *
     * @param childStatus 子节点的执行状态
     */
    protected abstract void onChildExecuted(TreeStatus childStatus);
}
