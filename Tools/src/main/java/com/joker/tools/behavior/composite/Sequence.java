package com.joker.tools.behavior.composite;

import com.joker.tools.behavior.AbstractParentTreeNode;
import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;

/**
 * 顺序执行子节点;<br/>
 * 当子节点全部执行成功, 则返回成功, 任何一个子节点返回失败则会立即返回失败;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 11:50
 * @version: 1.0
 */
public class Sequence extends AbstractParentTreeNode {
    private int currentChildIndex = 0;

    @Override
    protected AbstractTreeNode nextExecuteChild() {
        AbstractTreeNode node = null;
        if (children.size() <= 0) {
            this.status = TreeStatus.FAILURE;
            return null;
        }
        if (currentChildIndex < children.size()) {
            node = children.get(currentChildIndex);
        } else {
            this.status = TreeStatus.SUCCESS;
        }
        return node;
    }

    @Override
    protected void onChildExecuted(TreeStatus childStatus) {
        this.status = checkStatus(childStatus);
    }

    private TreeStatus checkStatus(TreeStatus childStatus) {
        return switch (childStatus) {
            case READY, RUNNING -> TreeStatus.RUNNING;
            // 当执行完所有的子节点时, 则执行成功, 还有剩余子节点时则递增索引
            case SUCCESS -> ++currentChildIndex >= children.size() ? TreeStatus.SUCCESS : TreeStatus.RUNNING;
            case FAILURE -> TreeStatus.FAILURE;
        };
    }
}
