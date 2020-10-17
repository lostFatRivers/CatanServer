package com.joker.tools.template;

import com.joker.tools.template.model.FieldModel;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 生成配置表 java 文件;
 *
 * @author: Joker
 * @date: Created in 2020/10/16 11:02
 * @version: 1.0
 */
public class TemplateGenerator {
    private static final Logger logger = LoggerFactory.getLogger("Tools");

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_30);

    static {
        CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(TemplateGenerator.class, "/template"));
        CONFIGURATION.setDefaultEncoding("UTF-8");
    }

    enum TemplateClassType {
        BEAN,
        MODEL
    }

    /** 已生成的 model 名集合 */
    private static final Set<String> MODEL_NAME_SET = new HashSet<>();

    private static String classPath;
    private static String javaPackage;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        loadToolsConfig(vertx)
            .compose(TemplateGenerator::startGenerate)
            .onSuccess(v -> {
                logger.info("template java file generate success!");
                vertx.close();
            })
            .onFailure(e -> {
                logger.error("generator failed.", e);
                vertx.close();
            });
    }

    private static Future<JsonObject> loadToolsConfig(Vertx vertx) {
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("hocon")
                .setConfig(new JsonObject().put("path", "tools.conf"));

        return Future.future(pros -> {
            ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(store));
            retriever.getConfig(pros);
        });
    }

    private static Future<Void> startGenerate(JsonObject config) {
        logger.info("template java file generate start");
        JsonObject genConfig = config.getJsonObject("template");

        String excelPath = genConfig.getString("excelPath");
        classPath = genConfig.getString("classPath");
        javaPackage = genConfig.getString("javaPackage");

        return Future.future(pro -> {
            try {
                File file = new File(excelPath);
                File[] list = file.listFiles();
                if (list == null) {
                    throw new RuntimeException("Excel Path not exist:" + excelPath);
                }
                // 遍历所有的 excel 文件
                for (File eachFile : list) {
                    if (!eachFile.getName().endsWith(".xls")) {
                        logger.warn("file not excel:{}", eachFile.getAbsolutePath());
                        continue;
                    }
                    String fileName = eachFile.getName();
                    String className = fileName.split("\\.")[0] + "Cfg";

                    final HSSFWorkbook excel = new HSSFWorkbook(new FileInputStream(eachFile));

                    singleGenerate(excel, className, "model", TemplateClassType.BEAN);
                }
            } catch (Exception e) {
                pro.fail(e);
            }
            pro.complete();
        });
    }

    private static void singleGenerate(HSSFWorkbook hssfWorkbook, String className, String sheetName, TemplateClassType type) throws Exception {
        HSSFSheet modelSheet = hssfWorkbook.getSheet(sheetName);
        List<FieldModel> fieldList;
        List<String> importList = new ArrayList<>();
        if (className.equals("GameParamsCfg")) {
            // 针对 GameParams 表的特殊处理
            fieldList = loadGameParamFieldFromRow(hssfWorkbook.getSheet("main"), hssfWorkbook, importList);
        } else {
            fieldList = loadFieldInfoFromRow(modelSheet, hssfWorkbook, importList);
        }
        String classPackage = type == TemplateClassType.BEAN ? javaPackage + ".bean" : javaPackage + ".model";

        Iterator<String> iterator = importList.iterator();
        while (iterator.hasNext()) {
            String eachImport = iterator.next();
            String lastStr = eachImport.replace(className + ".", "");
            if (lastStr.contains(".")) {
                continue;
            }
            iterator.remove();
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("classPackage", classPackage);
        dataMap.put("className", className);
        dataMap.put("fieldModelList", fieldList);
        dataMap.put("importList", importList);
        createJavaFile(classPath, className, type, dataMap);
        logger.info("generate class success! path:{}.{}", classPackage, className);
    }

    private static List<FieldModel> loadGameParamFieldFromRow(HSSFSheet modelSheet, HSSFWorkbook hssfWorkbook, List<String> importList) throws Exception {
        List<FieldModel> fieldList = new ArrayList<>();
        for (int i = 0; i < modelSheet.getLastRowNum(); i++) {
            if (i == 0) {
                continue;
            }
            Row nextRow = modelSheet.getRow(i);
            String fieldName = nextRow.getCell(0).getStringCellValue();
            if (fieldName.equals("")) {
                continue;
            }
            String fieldType = transformFieldType(nextRow.getCell(1).getStringCellValue(), hssfWorkbook, importList);
            Cell descCell = nextRow.getCell(3);
            String fieldDesc = descCell == null ? null : descCell.getStringCellValue();
            fieldList.add(new FieldModel(fieldName, fieldType, fieldDesc));
        }
        return fieldList;
    }

    private static String transformFieldType(String originType, HSSFWorkbook hssfWorkbook, List<String> importList) throws Exception {
        if (originType.equals("int") || originType.equals("long") || originType.equals("float")) {
            return originType;
        }
        if (originType.equals("string")) {
            return "String";
        }
        if (originType.startsWith("list")) {
            if (!importList.contains("java.util.List")) {
                importList.add("java.util.List");
            }
            String listType = originType.substring(5, originType.length() - 1);
            String transType = transformFieldType(listType, hssfWorkbook, importList);
            String trueType = baseTypeToObject(transType);
            return "List<" + trueType + ">";
        }
        String modelClassName = upperFirstCase(originType);
        if (!MODEL_NAME_SET.contains(modelClassName)) {
            String classImport = javaPackage + ".model." + modelClassName;
            if (!importList.contains(classImport)) {
                importList.add(classImport);
            }
            singleGenerate(hssfWorkbook, modelClassName, originType, TemplateClassType.MODEL);
            MODEL_NAME_SET.add(modelClassName);
        }
        return modelClassName;
    }

    private static String baseTypeToObject(String originType) {
        if (originType.equals("int")) {
            return "Integer";
        }
        if (originType.equals("long")) {
            return "Long";
        }
        if (originType.equals("float")) {
            return "Float";
        }
        return originType;
    }

    public static String upperFirstCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static List<FieldModel> loadFieldInfoFromRow(HSSFSheet modelSheet, HSSFWorkbook hssfWorkbook, List<String> importList) throws Exception {
        List<FieldModel> fieldList = new ArrayList<>();
        Iterator<Row> rowIterator = modelSheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row nextRow = rowIterator.next();
            String fieldName = nextRow.getCell(0).getStringCellValue();
            if (fieldName.equals("")) {
                continue;
            }
            String fieldType = transformFieldType(nextRow.getCell(1).getStringCellValue(), hssfWorkbook, importList);
            Cell descCell = nextRow.getCell(2);
            String fieldDesc = descCell == null ? null : descCell.getStringCellValue();
            fieldList.add(new FieldModel(fieldName, fieldType, fieldDesc));
        }
        return fieldList;
    }

    private static void createJavaFile(String classPath, String className, TemplateClassType type, Map<String, Object> dataMap) throws Exception {
        String outFilePath;
        if (type == TemplateClassType.BEAN) {
            outFilePath = classPath + "/bean/" + className + ".java";
        } else if (type == TemplateClassType.MODEL) {
            outFilePath = classPath + "/model/" + className + ".java";
        } else {
            throw new RuntimeException("invalid class type:" + type.name());
        }
        File file = new File(outFilePath);
        logger.debug("out file path:{}", file.getAbsolutePath());
        if (!file.exists() && !file.createNewFile()) {
            throw new RuntimeException("create out file: [" + outFilePath + "] failed.");
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        Template template = CONFIGURATION.getTemplate("templateJava.ftl");
        template.setOutputEncoding("UTF-8");
        template.process(dataMap, writer);
    }
}
