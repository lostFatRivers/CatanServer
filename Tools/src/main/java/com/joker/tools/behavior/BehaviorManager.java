package com.joker.tools.behavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 行为树管理器;
 *
 * @author: Joker
 * @date: Created in 2021/2/4 19:55
 * @version: 1.0
 */
public enum BehaviorManager {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger("BehaviorManager");

    private BehaviorTree tree;

    public static BehaviorManager getInstance() {
        return INSTANCE;
    }

    public void pushTree(BehaviorTree tree) {
        this.tree = tree;
    }

    public void startup() {
        while (!Thread.interrupted()) {
            try {
                TreeStatus status = this.tree.update();
                if (status == TreeStatus.SUCCESS || status == TreeStatus.FAILURE) {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(30);
            } catch (Exception ignore) {
            }
        }
    }
}
