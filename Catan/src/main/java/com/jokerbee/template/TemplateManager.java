package com.jokerbee.template;

import com.jokerbee.template.bean.GameLevelCfg;
import com.jokerbee.template.bean.GameParamsCfg;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * 静态数据管理类;
 *
 * @author: Joker
 * @date: Created in 2020/10/16 13:55
 * @version: 1.0
 */
public enum TemplateManager {
    INSTANCE;
    private final Logger logger = LoggerFactory.getLogger("Config");

//    @ConfigSource("GameParams.json")
//    private GameParamsCfg gameParamsCfg;

    public static TemplateManager getInstance() {
        return INSTANCE;
    }

    public void init() throws Exception {
        InputStream stream = TemplateManager.class.getResourceAsStream("/json/GameParams.json");
        JsonObject json = new JsonObject(Buffer.buffer(stream.readAllBytes()));
        GameParamsCfg cfg = json.mapTo(GameParamsCfg.class);
        logger.info("game param load ok: {}", cfg.getSlowRatio());
        stream.close();

        InputStream stream2 = TemplateManager.class.getResourceAsStream("/json/GameLevel.json");
        JsonArray json2 = new JsonArray(Buffer.buffer(stream2.readAllBytes()));
        for (int i = 0; i < json2.size(); i++) {
            JsonObject jsonObject = json2.getJsonObject(i);
            GameLevelCfg levelCfg = jsonObject.mapTo(GameLevelCfg.class);
            logger.info("game level load ok: {}", levelCfg.getId());
        }
        stream2.close();
    }

    public static void main(String[] args) throws Exception {
        TemplateManager.getInstance().init();
    }
}
