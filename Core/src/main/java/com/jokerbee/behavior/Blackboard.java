package com.jokerbee.behavior;

import java.util.HashMap;
import java.util.Map;

/**
 * 黑板, 用于记录数据;
 *
 * @author: Joker
 * @date: Created in 2021/1/26 15:46
 * @version: 1.0
 */
public class Blackboard {
    private static final int DEFAULT_TREE_ID = -1;
    private static final int DEFAULT_NODE_ID = -1;

    private final Map<String, Map<String, Object>> scopeData = new HashMap<>();

    public void set(String key, Object value) {
        this.set(key, value, DEFAULT_TREE_ID, DEFAULT_NODE_ID);
    }

    public void set(String key, Object value, int treeId) {
        this.set(key, value, treeId, DEFAULT_NODE_ID);
    }

    public void set(String key, Object value, int treeId, int nodeId) {
        String scope = "tree_" + treeId + "_node_" + nodeId;
        Map<String, Object> scopeData = getScopeData(scope);
        scopeData.put(key, value);
    }

    public Object get(String key) {
        return this.get(key, DEFAULT_TREE_ID, DEFAULT_NODE_ID);
    }

    public Object get(String key, int treeId) {
        return this.get(key, treeId, DEFAULT_NODE_ID);
    }

    public Object get(String key, int treeId, int nodeId) {
        String scope = "tree_" + treeId + "_node_" + nodeId;
        Map<String, Object> scopeData = getScopeData(scope);
        return scopeData.get(key);
    }

    private Map<String, Object> getScopeData(String scope) {
        if (!scopeData.containsKey(scope)) {
            scopeData.put(scope, new HashMap<>());
        }
        return scopeData.get(scope);
    }
}
