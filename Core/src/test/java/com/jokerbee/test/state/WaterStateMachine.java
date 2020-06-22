package com.jokerbee.test.state;

import com.jokerbee.state.AbstractStateMachine;
import com.jokerbee.state.anno.State;
import com.jokerbee.state.anno.Transition;

/**
 * 状态机使用示例<br/>
 * 水的 '液态', '固态', '气态' 三种状态转换;
 */
public class WaterStateMachine extends AbstractStateMachine<WaterState, WaterTransition> {

    public WaterStateMachine() {
        // 初始状态 - 固态
        super(WaterState.solid);
        // 融化, 固态 -> 液态
        registerTransition(WaterTransition.melt, WaterState.solid, WaterState.liquid);
        // 凝固, 液态 -> 固态
        registerTransition(WaterTransition.freeze, WaterState.liquid, WaterState.solid);
        // 蒸发, 液态 -> 气态
        registerTransition(WaterTransition.vaporize, WaterState.liquid, WaterState.gas);
        // 凝结, 气态 -> 液态
        registerTransition(WaterTransition.condense, WaterState.gas, WaterState.liquid);
    }

    /**
     * 水变为液态时的回调方法;
     */
    @State("liquid")
    private void onLiquid() {
        logger.info("water is liquid now.");
    }

    /**
     * 当凝结过程完成后回调方法;
     */
    @Transition("condense")
    private void afterCondense() {
        logger.info("water was Condensed.");
    }

    /**
     * 水变为气态时的回调方法, 可带参数;
     */
    @State("gas")
    private void onGas() {
        logger.info("water is gas now.");
    }

}
