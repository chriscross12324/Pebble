package com.simple.chris.pebble;

import android.app.Person;

public class Gradient {
    private String name;
    private int hexL;
    private int hexR;
    private String description;

    public Gradient(String name, int hexL, int hexR, String description){
        this.name = name;
        this.hexL = hexL;
        this.hexR = hexR;
        this.description = description;
    }

    public String getName(){ return name; }

    public void setName(String name) { this.name = name; }

    public int getHexL() {
        return hexL;
    }

    public void setHexL(int hexL) {
        this.hexL = hexL;
    }

    public int getHexR() {
        return hexR;
    }

    public void setHexR(int hexR) {
        this.hexR = hexR;
    }

    public String getDescription(){ return description; }

    public void setDescription(String description) { this.description = description; }
}
