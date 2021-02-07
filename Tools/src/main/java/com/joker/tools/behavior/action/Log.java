package com.joker.tools.behavior.action;

import com.joker.tools.behavior.AbstractTreeNode;
import com.joker.tools.behavior.TreeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志节点;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 15:45
 * @version: 1.0
 */
public class Log extends AbstractTreeNode {
    private static final Logger logger = LoggerFactory.getLogger(Log.class);
    private final String content;

    public Log(String content) {
        this.content = content;
    }

    @Override
    public TreeStatus update() {
        logger.info(content);
        this.setStatus(TreeStatus.SUCCESS);
        return TreeStatus.SUCCESS;
    }
}
