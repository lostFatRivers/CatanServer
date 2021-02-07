package com.joker.tools.behavior;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点基类;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 11:27
 * @version: 1.0
 */
public abstract class AbstractTreeNode implements ITreeNode {
    /** 节点状态 */
    protected TreeStatus status = TreeStatus.READY;

    protected AbstractTreeNode parent;

    /**
     * 节点开始执行, 状态切换为 RUNNING
     */
    public void onExecuted() {
        this.status = TreeStatus.RUNNING;
    }

    public void setParent(AbstractTreeNode parent) {
        this.parent = parent;
    }

    public TreeStatus getStatus() {
        return status;
    }

    public void setStatus(TreeStatus status) {
        this.status = status;
    }

}
