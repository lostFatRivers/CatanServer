package com.joker.tools.behavior.action;

import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;
import com.joker.tools.behavior.action.agent.IActionHost;
import com.jokerbee.util.TimeUtil;

import java.lang.reflect.Method;

/**
 * 代理对象执行节点;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 17:26
 * @version: 1.0
 */
public class AgentAction<T extends IActionHost> extends AbstractTreeNode {
    private final T host;
    private final String invokeName;

    private long startTime;

    private boolean executed = false;

    public AgentAction(T host, String invokeName) {
        this.host = host;
        this.invokeName = invokeName;
    }

    @Override
    public void onExecuted() {
        super.onExecuted();
        this.startTime = TimeUtil.getTime();
    }

    @Override
    public TreeStatus update() {
        try {
            if (executed) {
                host.tick(TimeUtil.getTime() - startTime);
                if (host.isFinished()) {
                    this.status = TreeStatus.SUCCESS;
                }
            } else {
                executed = true;
                Method method = host.getClass().getMethod(invokeName);
                method.invoke(host);
            }
        } catch (Exception e) {
            this.status = TreeStatus.FAILURE;
        }
        return this.status;
    }
}
