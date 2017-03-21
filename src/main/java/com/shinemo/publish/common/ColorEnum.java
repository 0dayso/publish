package com.shinemo.publish.common;

import net.sf.json.JSONObject;

/**
 * Created by wug on 2015/10/30 0030 13:40.
 * email wug@shinemo.com
 */
public enum ColorEnum {
    LIMEGREEN(1,"酸橙绿", "#2BB300"), GOLD(2, "金", "#FFCC00");
    private final int index;
    private final String name;// 名称
    private final String color;// 16进制颜色码

    ColorEnum(int index, String name, String color) {
        this.index = index;
        this.name = name;
        this.color = color;
    }

    public int getIndex() {
        return index;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }


    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("index", index);
        jo.put("name", name);
        jo.put("color", color);
        return jo;
    }

}
