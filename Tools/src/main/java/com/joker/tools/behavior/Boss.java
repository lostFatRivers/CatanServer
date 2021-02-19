package com.joker.tools.behavior;

import com.joker.tools.behavior.action.agent.IActionHost;
import com.jokerbee.util.RandomUtil;
import com.jokerbee.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI Boss;
 *
 * @author: Joker
 * @date: Created in 2021/2/5 19:30
 * @version: 1.0
 */
public class Boss implements IActionHost {
    private static final Logger logger = LoggerFactory.getLogger("Boss");

    private int[] position = new int[3];

    /** 每秒速度 */
    private final int speed;

    private int[] targetPosition = new int[3];

    private long endTime = 0;

    private boolean finished;

    public Boss(int speed) {
        position[0] = RandomUtil.getRandom(1, 30);
        position[1] = RandomUtil.getRandom(1, 30);
        position[2] = RandomUtil.getRandom(1, 30);
        this.speed = speed;
    }

    public void randomMove() {
        // 重置结束标记
        this.finished = false;
        targetPosition[0] = RandomUtil.getRandom(1, 30);
        targetPosition[1] = RandomUtil.getRandom(1, 30);
        targetPosition[2] = RandomUtil.getRandom(1, 30);
        double dis = distance(targetPosition, position);
        double costTime = dis / speed;
        this.endTime = (long) (TimeUtil.getTime() + costTime * 1000);
        logger.info("start random move, targetPos:{}, costTime:{}", targetPosition, costTime);
    }

    private double distance(int[] p1, int[] p2) {
        int x1 = p2[0] - p1[0];
        int y1 = p2[1] - p1[1];
        int z1 = p2[2] - p1[2];
        return Math.sqrt((Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2)));
    }

    @Override
    public void tick(long time) throws Exception {
        if (this.finished) {
            return;
        }
        if (TimeUtil.getTime() > endTime) {
            this.finished = true;
            this.position = this.targetPosition;
            this.targetPosition = new int[3];
            logger.info("end random move.");
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
