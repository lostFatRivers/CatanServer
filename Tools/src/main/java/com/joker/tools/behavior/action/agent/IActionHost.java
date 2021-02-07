package com.joker.tools.behavior.action.agent;

/**
 * 行为节点代理对象接口;<br/>
 * 被行为树Action代理的对象在执行业务时, 只有在出错时才会抛出异常;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 17:40
 * @version: 1.0
 */
public interface IActionHost {

    /**
     * 任务心跳;
     *
     * @param time tick time
     */
    void tick(long time) throws Exception;

    /**
     * 是否结束;
     *
     * @return
     */
    boolean isFinished();
}
