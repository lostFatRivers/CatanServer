package com.joker.tools.behavior.decorator;

import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;

/**
 * 循环执行子节点;
 *
 * @author: Joker
 * @date: Created in 2021/2/19 16:41
 * @version: 1.0
 */
public class Loop extends AbstractTreeNode {
    private final AbstractTreeNode child;
    private int times;

    public Loop(AbstractTreeNode child, int times) {
        this.child = child;
        this.times = times;
    }

    @Override
    public TreeStatus update() {
        TreeStatus childStatus = child.update();
        if (childStatus == TreeStatus.SUCCESS || childStatus == TreeStatus.FAILURE) {
            if (times > 0) {
                times--;
                if (times == 0) {
                    setStatus(TreeStatus.SUCCESS);
                }
            }
            if (times > 0 || times == -1) {
                // 重置子节点状态
                child.setStatus(TreeStatus.READY);
            }
        }
        return this.status;
    }
}
