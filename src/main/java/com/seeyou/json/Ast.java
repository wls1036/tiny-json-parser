package com.seeyou.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2022/12/16 17:57
 * @history: 1.2022/12/16 created by jianfeng.zheng
 */
public class Ast {
    //节点类型
    private String type;

    //如果是object则是字段列表
    //如果是array则是值列表
    private List<Ast> items;

    //字段名（可为空）
    private String name;

    //字段值
    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * object和array的items如果为空默认是空对象或者空数组
     *
     * @return
     */
    public List<Ast> getItems() {
        return (items == null && ("object".equals(type) || "array".equals(type))) ? items = new ArrayList<>() : items;
    }

    public void setItems(List<Ast> items) {
        this.items = items;
    }


}
