package com.jokerbee.gateway;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理器;
 *
 * @author: Joker
 * @date: Created in 2020/11/3 16:14
 * @version: 1.0
 */
public enum ConnectorManager {
    INSTANCE;

    public static ConnectorManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, GatewayConnector> connectorMap = new ConcurrentHashMap<>();
    private final Map<String, String> accountToHandlerIdMap = new ConcurrentHashMap<>();

    public void addConnector(GatewayConnector connector) {
        connectorMap.put(connector.getHandlerId(), connector);
    }

    public void removeConnector(GatewayConnector connector) {
        if (connector == null) {
            return;
        }
        if (StringUtils.isNotEmpty(connector.getBindAccount())) {
            accountToHandlerIdMap.remove(connector.getBindAccount());
        }
        connectorMap.remove(connector.getHandlerId());
    }

    public Map<String, String> getAccountToHandlerIdMap() {
        return accountToHandlerIdMap;
    }

    public GatewayConnector getConnector(String handlerId) {
        return connectorMap.get(handlerId);
    }
}
