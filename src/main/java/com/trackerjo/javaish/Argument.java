package com.trackerjo.javaish;

import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Variables.VarType;

public class Argument {
    JavaishType type;
    String name;

    public Argument(JavaishType type, String name) {
        this.type = type;
        this.name = name;
    }

    public JavaishType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    
}