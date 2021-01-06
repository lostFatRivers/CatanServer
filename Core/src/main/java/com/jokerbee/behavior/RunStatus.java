package com.jokerbee.behavior;

/**
 * 执行状态;
 *
 * @author: Joker
 * @date: Created in 2020/12/2 10:42
 * @version: 1.0
 */
public enum RunStatus {
    /** 初始 */
    FRESH,
    /** 执行成功 */
    SUCCESS,
    /** 执行失败 */
    FAILURE,
    /** 正在执行 */
    RUNNING,
}
