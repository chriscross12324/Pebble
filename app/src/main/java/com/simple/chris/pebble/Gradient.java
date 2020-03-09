package com.simple.chris.pebble;

import java.util.Date;

public class Gradient {

    private String id;
    private String gradientName;
    private String startColour;
    private String endColour;
    private String description;
    private String creatorsName;
    private int likeCount;

    public Gradient(String id, String name, String start, String end, String desc, String creator, int likes) {
        this.id = id;
        this.gradientName = name;
        this.startColour = start;
        this.endColour = end;
        this.description = desc;
        this.creatorsName = creator;
        this.likeCount = likes;
    }

    public Gradient(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGradientName() {
        return gradientName;
    }

    public void setGradientName(String gradientName) {
        this.gradientName = gradientName;
    }

    public String getStartColour() {
        return startColour;
    }

    public void setStartColour(String startColour) {
        this.startColour = startColour;
    }

    public String getEndColour() {
        return endColour;
    }

    public void setEndColour(String endColour) {
        this.endColour = endColour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorsName() {
        return creatorsName;
    }

    public void setCreatorsName(String creatorsName) {
        this.creatorsName = creatorsName;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

}
