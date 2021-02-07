package com.joker.tools.behavior.composite;

import com.joker.tools.behavior.AbstractParentTreeNode;
import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;

/**
 * 并行执行行为节点;
 *
 * @author: Joker
 * @date: Created in 2021/2/7 11:21
 * @version: 1.0
 */
public class Parallel extends AbstractParentTreeNode {
    private ParallelStrategy strategy;

    public Parallel(ParallelStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public TreeStatus update() {
        for (AbstractTreeNode eachChild : children) {
            if (eachChild.getStatus() == TreeStatus.READY) {
                eachChild.onExecuted();
            }
            if (eachChild.getStatus() == TreeStatus.SUCCESS || eachChild.getStatus() == TreeStatus.FAILURE) {
                continue;
            }
            TreeStatus childStatus = eachChild.update();
            onChildExecuted(childStatus);
        }
        // 判断全部成功
        if (this.strategy == ParallelStrategy.FAIL_IN_ONE) {
            boolean isAllSuccess = true;
            for (AbstractTreeNode child : children) {
                if (child.getStatus() != TreeStatus.SUCCESS) {
                    isAllSuccess = false;
                    break;
                }
            }
            if (isAllSuccess) {
                this.setStatus(TreeStatus.SUCCESS);
            }
        }
        // 判断全部失败
        if (this.strategy == ParallelStrategy.SUCCESS_IN_ONE) {
            boolean isAllFailed = true;
            for (AbstractTreeNode child : children) {
                if (child.getStatus() != TreeStatus.FAILURE) {
                    isAllFailed = false;
                    break;
                }
            }
            if (isAllFailed) {
                this.setStatus(TreeStatus.FAILURE);
            }
        }
        return this.status;
    }

    @Override
    protected AbstractTreeNode nextExecuteChild() {
        throw new IllegalStateException("parallel composite cannot do single child.");
    }

    @Override
    protected void onChildExecuted(TreeStatus childStatus) {
        strategy.onChildExecuted(this, childStatus);
    }

    public ParallelStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ParallelStrategy strategy) {
        this.strategy = strategy;
    }

    public enum ParallelStrategy {
        FAIL_IN_ONE {
            @Override
            public void onChildExecuted(AbstractParentTreeNode parent, TreeStatus childStatus) {
                if (parent.getStatus() == TreeStatus.FAILURE) {
                    return;
                }
                if (childStatus == TreeStatus.FAILURE) {
                    parent.setStatus(TreeStatus.FAILURE);
                }
            }
        },
        SUCCESS_IN_ONE {
            @Override
            public void onChildExecuted(AbstractParentTreeNode parent, TreeStatus childStatus) {
                if (parent.getStatus() == TreeStatus.SUCCESS) {
                    return;
                }
                if (childStatus == TreeStatus.SUCCESS) {
                    parent.setStatus(TreeStatus.SUCCESS);
                }
            }
        };

        public abstract void onChildExecuted(AbstractParentTreeNode parent, TreeStatus childStatus);
    }
}
