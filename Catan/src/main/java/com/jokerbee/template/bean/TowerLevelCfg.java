package com.jokerbee.template.bean;

import java.util.List;
import com.jokerbee.template.model.CostResource;

/**
 * ==================================
 * = auto generated, do not modify! =
 * ==================================
 */
public class TowerLevelCfg {
    private int id;
    
    private int type;
    
    private int level;
    
    private int hit;
    
    private int interval;
    
    private int range;
    
    private int bulletSpeed;
    
    private int damageType;
    
    private int debuffType;
    
    private List<CostResource> buildSource;
    
    private List<CostResource> upgradeSource;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public int getDamageType() {
        return damageType;
    }

    public void setDamageType(int damageType) {
        this.damageType = damageType;
    }

    public int getDebuffType() {
        return debuffType;
    }

    public void setDebuffType(int debuffType) {
        this.debuffType = debuffType;
    }

    public List<CostResource> getBuildSource() {
        return buildSource;
    }

    public void setBuildSource(List<CostResource> buildSource) {
        this.buildSource = buildSource;
    }

    public List<CostResource> getUpgradeSource() {
        return upgradeSource;
    }

    public void setUpgradeSource(List<CostResource> upgradeSource) {
        this.upgradeSource = upgradeSource;
    }

}