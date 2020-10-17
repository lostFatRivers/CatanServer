package com.jokerbee.template;

import com.jokerbee.anno.ConfigSource;
import com.jokerbee.template.bean.GameLevelCfg;
import com.jokerbee.template.bean.GameParamsCfg;
import com.jokerbee.template.bean.MonsterModelCfg;
import com.jokerbee.template.bean.TowerLevelCfg;
import com.jokerbee.util.TimeUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 静态数据管理类;
 *
 * @author: Joker
 * @date: Created in 2020/10/16 13:55
 * @version: 1.0
 */
public class TemplateManager {
    private final Logger logger = LoggerFactory.getLogger("Config");
    private static volatile TemplateManager INSTANCE = new TemplateManager();

    @ConfigSource(path = "/json/GameParams.json")
    private GameParamsCfg gameParamsCfg;

    @ConfigSource(path = "/json/GameLevel.json")
    private Map<Integer, GameLevelCfg> gameLevelCfgMap;

    @ConfigSource(path = "/json/MonsterModel.json")
    private List<MonsterModelCfg> monsterModels;

    @ConfigSource(path = "/json/TowerLevel.json")
    private Map<Integer, TowerLevelCfg> towerLevelCfgMap;

    private TemplateManager() {}

    public static TemplateManager getInstance() {
        return INSTANCE;
    }

    public synchronized void init() throws Exception {
        long startTime = TimeUtil.getTime();
        logger.info("start");
        loadConfig();
        afterLoad();
        logger.info("end, costTime:{}", TimeUtil.getTime() - startTime);
    }

    public static void main(String[] args) throws Exception {
        TemplateManager.getInstance().init();
        TimeUnit.SECONDS.sleep(2);

        for (int i = 0; i < 10; i++) {
            TemplateManager.getInstance().reload();
            TimeUnit.SECONDS.sleep(2);
        }
    }

    private void afterLoad() {
        // 加载完后的处理
    }

    /**
     * 数据过滤器;
     *
     * @param config Cfg对象
     * @return 是否丢弃 true:丢弃, false:保留
     */
    private boolean assembleFilter(Object config) {
        // 示例: 塔等级大于100则表示数据不合法
        if (config instanceof TowerLevelCfg) {
            TowerLevelCfg cfg = (TowerLevelCfg) config;
            return cfg.getLevel() > 100;
        }
        return false;
    }


    ////////////////////////////// 数据装配 //////////////////////////////

    private void loadConfig() throws Exception {
        Field[] fields = TemplateManager.class.getDeclaredFields();
        for (Field eachField : fields) {
            ConfigSource annotation = eachField.getAnnotation(ConfigSource.class);
            if (annotation == null) {
                continue;
            }
            assembleConfigField(eachField, annotation.path());
        }
    }

    /**
     * TemplateManager 的配置字段组装;
     */
    private void assembleConfigField(Field configField, String configSourcePath) throws Exception {
        int startIndex = configSourcePath.lastIndexOf("/") + 1;
        int endIndex = configSourcePath.lastIndexOf(".");
        String className = configSourcePath.substring(startIndex, endIndex);
        String fullClassName = "com.jokerbee.template.bean." + className + "Cfg";
        logger.info("start assemble {}", fullClassName);
        // Config class
        Class<?> configClass = TemplateManager.class.getClassLoader().loadClass(fullClassName);
        // Config instance list.
        List<?> configObjectList = parseJsonFile(configField, configClass, configSourcePath);

        Class<?> declaringClass = configField.getType();
        if (declaringClass == Map.class) {
            assembleMapField(configField, configClass, configObjectList);
        } else if (declaringClass == List.class) {
            assembleListField(configField, configObjectList);
        } else {
            assembleSingleField(configField, configObjectList);
        }
    }

    /**
     * 加载 json 文件并装入 list;
     */
    private <T> List<T> parseJsonFile(Field field, Class<T> configClass, String configSourcePath) throws Exception {
        InputStream stream = TemplateManager.class.getResourceAsStream(configSourcePath);
        List<T> list = new ArrayList<>();
        if (field.getType() == List.class || field.getType() == Map.class) {
            JsonArray jsonArray = new JsonArray(Buffer.buffer(stream.readAllBytes()));
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject json = jsonArray.getJsonObject(i);
                T cfg = json.mapTo(configClass);
                list.add(cfg);
            }
        } else {
            JsonObject json = new JsonObject(Buffer.buffer(stream.readAllBytes()));
            T cfg = json.mapTo(configClass);
            list.add(cfg);
        }
        stream.close();
        return list;
    }

    /**
     * 组装 Map 类型字段;
     */
    private void assembleMapField(Field field, Class<?> configClass, List<?> dataList) throws Exception {
        Map<Integer, Object> map = new ConcurrentHashMap<>();
        Field configIdField = null;
        Field[] declaredFields = configClass.getDeclaredFields();
        for (Field eachConfigField : declaredFields) {
            eachConfigField.setAccessible(true);
            String fieldName = eachConfigField.getName();
            if (fieldName.equals("id")) {
                configIdField = eachConfigField;
                break;
            }
        }
        if (configIdField == null) {
            throw new RuntimeException("Not found Id field. class:" + configClass.getName());
        }
        for (Object object : dataList) {
            if (assembleFilter(object)) {
                continue;
            }
            Integer id = (Integer) configIdField.get(object);
            map.put(id, object);
        }
        field.set(this, map);
    }

    /**
     * 组装 List 类型字段;
     */
    private void assembleListField(Field field, List<?> dataList) throws Exception {
        List<Object> list = new CopyOnWriteArrayList<>();
        for (Object object : dataList) {
            if (assembleFilter(object)) {
                continue;
            }
            list.add(object);
        }
        field.set(this, list);
    }

    /**
     * 组装单个类型字段;
     */
    private void assembleSingleField(Field field, List<?> dataList) throws Exception {
        if (dataList.isEmpty()) {
            throw new RuntimeException("Field config is null");
        }
        if (dataList.size() > 1) {
            logger.warn("json array size error. field:{}", field.getName());
        }
        field.set(this, dataList.get(0));
    }

    /**
     * 重新加载配置;
     */
    private void reload() {
        TemplateManager newManager = new TemplateManager();
        try {
            newManager.init();
            INSTANCE = newManager;
        } catch (Exception e) {
            logger.error("reload config error.", e);
        }
    }

}
