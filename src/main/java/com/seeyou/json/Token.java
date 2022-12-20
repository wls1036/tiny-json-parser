package com.seeyou.json;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2022/12/16 16:04
 * @history: 1.2022/12/16 created by jianfeng.zheng
 */
public class Token {
    public String type;
    public String valueType;
    public Object value;

    public Token(String type, String valueType, Object value) {
        this.type = type;
        this.valueType = valueType;
        this.value = value;
    }

    public Token(String type, String value) {
        this.type = type;
        this.valueType = type;
        this.value = value;
    }

    public Token(String type, char value) {
        this.type = type;
        this.valueType = type;
        this.value = String.valueOf(value);
    }

    public Token(String type, String valueType, char value) {
        this.type = type;
        this.valueType = valueType;
        this.value = String.valueOf(value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}
