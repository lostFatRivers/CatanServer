package com.joker.tools.behavior;

/**
 * 行为树节点接口;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 14:46
 * @version: 1.0
 */
public interface ITreeNode {

    /**
     * 执行此节点;
     *
     * @return 执行后状态
     */
    TreeStatus update();

    /**
     * 获取节点状态;
     *
     * @return 节点当前状态
     */
    TreeStatus getStatus();
}
