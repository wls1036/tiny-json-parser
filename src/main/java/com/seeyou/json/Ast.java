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
    private String type;
    private List<Ast> items;
    private String name;
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

    public List<Ast> getItems() {
        return (items == null && ("object".equals(type) || "array".equals(type))) ? items=new ArrayList<>() : items;
    }

    public void setItems(List<Ast> items) {
        this.items = items;
    }


}
