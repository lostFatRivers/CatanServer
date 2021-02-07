package com.joker.tools.behavior.composite;

import com.joker.tools.behavior.AbstractParentTreeNode;
import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;

/**
 * 选择执行子节点;<br/>
 * 顺序执行子节点, 任意一个返回成功则直接返回成功, 所有都失败则返回失败;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 15:10
 * @version: 1.0
 */
public class Selector extends AbstractParentTreeNode {
    private int currentChildIndex = 0;

    @Override
    protected AbstractTreeNode nextExecuteChild() {
        AbstractTreeNode node = null;
        if (currentChildIndex < children.size()) {
            node = children.get(currentChildIndex);
        } else {
            this.status = TreeStatus.FAILURE;
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
            case SUCCESS -> TreeStatus.SUCCESS;
            case FAILURE -> ++currentChildIndex >= children.size() ? TreeStatus.FAILURE : TreeStatus.RUNNING;
        };
    }
}
