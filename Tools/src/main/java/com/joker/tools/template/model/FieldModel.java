package com.joker.tools.template.model;

/**
 * 字段封装;
 *
 * @author: Joker
 * @date: Created in 2020/10/16 16:03
 * @version: 1.0
 */
public class FieldModel {
    /** 字段名 */
    private String fieldName;
    /** 字段类型 */
    private String fieldType;
    /** 字段注释 */
    private String fieldDesc;

    public FieldModel(String fieldName, String fieldType, String fieldDesc) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.fieldDesc = fieldDesc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }
}
