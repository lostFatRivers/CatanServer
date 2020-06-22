package com.jokerbee.consts;

public enum GameStatus {
    // 准备, 等待
    PREPARING("preparing", "none"),

    // 颜色选择
    COLOR_CHOOSING("colorChoosing", "begin"),

    // 前置回合1
    PRE_ROUND_1("preRound1", "toPre1"),

    // 前置回合2
    PRE_ROUND_2("preRound2", "toPre2"),

    // 扔骰子
    THROWING("throwing", "throwDice"),

    // 操作中
    OPERATING("operating", "toOperate"),

    // 回合结束
    ROUND_END("roundEnd", "toNext"),

    ;
    private final String name;
    private final String trans;

    GameStatus(String name, String trans) {
        this.name = name;
        this.trans = trans;
    }
    public String getName() {
        return this.name;
    }

    public String getTrans() {
        return trans;
    }
}
