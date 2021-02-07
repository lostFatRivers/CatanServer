package com.joker.tools.behavior.action;

import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;
import com.jokerbee.util.TimeUtil;

/**
 * 等待节点;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 15:41
 * @version: 1.0
 */
public class Wait extends AbstractTreeNode {
    private long startTime;
    /** 等待时间 单位(ms) */
    private final long idleTime;

    public Wait(long idleTime) {
        this.idleTime = idleTime;
    }

    @Override
    public void onExecuted() {
        super.onExecuted();
        this.startTime = TimeUtil.getTime();
    }

    @Override
    public TreeStatus update() {
        if (TimeUtil.getTime() - this.startTime >= idleTime) {
            this.status = TreeStatus.SUCCESS;
        }
        return this.status;
    }
}
