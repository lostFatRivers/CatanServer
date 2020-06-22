package com.jokerbee.test.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachineTest {
    protected static Logger logger = LoggerFactory.getLogger("TEST");

    public static void main(String[] args) {
        waterStateTest();
    }

    public static void waterStateTest() {
        WaterStateMachine wsm = new WaterStateMachine();

        logger.info("water state:{}", wsm.toString());

        wsm.toState(WaterState.liquid);
        wsm.toState(WaterState.gas);
        wsm.toState(WaterState.solid);
        logger.info("water now is {}", wsm.getCurrentState().name());

        wsm.toState(WaterState.solid);
        if (wsm.isState(WaterState.solid)) {
            logger.info("water now is {}", wsm.getCurrentState().name());
        }
    }
}
