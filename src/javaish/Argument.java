package javaish;

import javaish.JavaishVal.JavaishType;
import javaish.Variables.VarType;

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