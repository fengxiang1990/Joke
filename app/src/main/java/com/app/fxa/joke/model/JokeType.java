package com.app.fxa.joke.model;

/**
 * Created by admin on 2014-12-23.
 */
public enum JokeType {

    ALL(10, "全部"),
    TUIJIAN(20, "今日推荐"),
    COLD(30, "冷笑话"),
    HUMOR(40, "幽默"),
    LOVE(50, "爱情"),
    HIGH(60, "爆笑"),
    SCHOOL(70, "校园"),
    CHILDREN(80, "儿童"),
    AUDIT(90, "成人"),
    NEW(100, "最新");
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    JokeType(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
