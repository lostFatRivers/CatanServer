package com.joker.tools.match;

import com.jokerbee.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 匹配小队;
 *
 * @author: Joker
 * @date: Created in 2021/1/21 11:51
 * @version: 1.0
 */
public class MatchTeam {
    private static final Logger logger = LoggerFactory.getLogger("MatchTeam");

    public static final int TEAM_SIZE = 4;
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(87540);

    private final int teamId;
    private final LimitType limitType;
    private final List<PlayerInfo> members = new ArrayList<>();

    public MatchTeam(LimitType limitType) {
        this.teamId = ID_GENERATOR.incrementAndGet();
        this.limitType = limitType;
    }

    /**
     * 检查是否可以添加到队伍并加入;
     */
    public boolean checkAndAddPlayer(PlayerInfo playerInfo) {
        if (members.size() >= TEAM_SIZE || playerInfo.getLimitType() != limitType) {
            return false;
        }
        if (members.size() <= 0 || limitType == LimitType.ANY) {
            addPlayer(playerInfo);
            return true;
        }
        if (limitType == LimitType.LEVEL_ONLY) {
            PlayerInfo info = members.get(0);
            if (info.getLevel() == playerInfo.getLevel()) {
                addPlayer(playerInfo);
                return true;
            } else {
                return false;
            }
        }
        if (limitType == LimitType.WEAPON_AND_LEVEL) {
            for (PlayerInfo eachMember : members) {
                if (eachMember.getLevel() != playerInfo.getLevel() || eachMember.getType() == playerInfo.getType()) {
                    return false;
                }
            }
            addPlayer(playerInfo);
            return true;
        }
        return false;
    }

    public int getTeamId() {
        return teamId;
    }

    public List<PlayerInfo> getMembers() {
        return members;
    }

    private void addPlayer(PlayerInfo playerInfo) {
        playerInfo.setTeamId(teamId);
        members.add(playerInfo);
        if (members.size() >= TEAM_SIZE) {
            List<Integer> list = getMemberIds();
            logger.info("team member full, teamId:{}, limitType:{}, members:{}", teamId, limitType, list);
        }
    }

    public List<Integer> getMemberIds() {
        return members.stream().map(PlayerInfo::getId).collect(Collectors.toList());
    }

    public List<String> getMemberSimples() {
        return members.stream().map(PlayerInfo::simpleInfo).collect(Collectors.toList());
    }

    public void removePlayer(int playerId) {
        Iterator<PlayerInfo> iterator = members.iterator();
        while (iterator.hasNext()) {
            PlayerInfo next = iterator.next();
            if (next.getId() == playerId) {
                next.setTeamId(0);
                iterator.remove();
            }
        }
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public boolean isNotFull() {
        return members.size() < TEAM_SIZE;
    }
}
