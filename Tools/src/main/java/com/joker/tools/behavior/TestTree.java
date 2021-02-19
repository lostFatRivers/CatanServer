package com.joker.tools.behavior;

import com.joker.tools.behavior.action.AgentAction;
import com.joker.tools.behavior.action.Log;
import com.joker.tools.behavior.action.Wait;
import com.joker.tools.behavior.composite.Parallel;
import com.joker.tools.behavior.composite.Selector;
import com.joker.tools.behavior.composite.Sequence;
import com.joker.tools.behavior.decorator.Loop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTree {
    private static final Logger logger = LoggerFactory.getLogger("Test");

    public static void main(String[] args) {
        Selector root = new Selector();
//        Sequence root = new Sequence();

        Parallel parallel1 = new Parallel(Parallel.ParallelStrategy.FAIL_IN_ONE);
        Sequence sequence2 = new Sequence();
        root.addChild(parallel1);
        root.addChild(sequence2);

        Boss boss = new Boss(2);
        AgentAction<Boss> bossAgent = new AgentAction<>(boss, "randomMove");
        parallel1.addChild(bossAgent);

        Log log3 = new Log("parallel 1 log 1");
        parallel1.addChild(log3);

        Log log1 = new Log("sequence 2 log 1");
        Wait wait1 = new Wait(1000);
        Log log2 = new Log("sequence 2 log 2 ⎞⎜⎛");

        sequence2.addChild(log1);
        sequence2.addChild(wait1);
        sequence2.addChild(log2);

        Loop loop = new Loop(root, -1);

        BehaviorTree tree = new BehaviorTree(loop);

        BehaviorManager.getInstance().pushTree(tree);

        logger.info("startup behavior tree manager.");
        BehaviorManager.getInstance().startup();
    }

}
