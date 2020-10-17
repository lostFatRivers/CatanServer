package com.jokerbee.template.bean;

import com.jokerbee.template.model.ColorModel;
import java.util.List;
import com.jokerbee.template.model.Point;
import com.jokerbee.template.model.WaveModel;
import com.jokerbee.template.model.RewardModel;

/**
 * ==================================
 * = auto generated, do not modify! =
 * ==================================
 */
public class GameLevelCfg {
    private int id;
    
    private int map;
    
    private int way;
    
    private ColorModel color;
    
    private List<Point> wayPoints;
    
    private List<WaveModel> waves;
    
    private List<Point> cursorPoints;
    
    private List<RewardModel> reward;
    
    private List<Point> firePillarPoints;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public ColorModel getColor() {
        return color;
    }

    public void setColor(ColorModel color) {
        this.color = color;
    }

    public List<Point> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<Point> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public List<WaveModel> getWaves() {
        return waves;
    }

    public void setWaves(List<WaveModel> waves) {
        this.waves = waves;
    }

    public List<Point> getCursorPoints() {
        return cursorPoints;
    }

    public void setCursorPoints(List<Point> cursorPoints) {
        this.cursorPoints = cursorPoints;
    }

    public List<RewardModel> getReward() {
        return reward;
    }

    public void setReward(List<RewardModel> reward) {
        this.reward = reward;
    }

    public List<Point> getFirePillarPoints() {
        return firePillarPoints;
    }

    public void setFirePillarPoints(List<Point> firePillarPoints) {
        this.firePillarPoints = firePillarPoints;
    }

}