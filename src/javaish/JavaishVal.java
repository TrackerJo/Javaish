package javaish;

import java.util.List;

public sealed interface JavaishVal {
    public Object getValue();
    public JavaishType getType();
    public String typeString();
    public enum JavaishType {
        INT,
        FLOAT,
        STRING,
        BOOLEAN,
        INTLIST,
        FLOATLIST,
        STRINGLIST,
        BOOLEANLIST,
        LIST,
        INPUT
 

    }
   
}

final class JavaishInt implements JavaishVal {
    int value;
    JavaishType type = JavaishType.INT;
    public Integer getValue() {
        return value;
    }
    JavaishInt(int value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "int";
    }
    
}

final class JavaishFloat implements JavaishVal {
    float value;
    JavaishType type = JavaishType.FLOAT;
    public Float getValue() {
        return value;
    }
    JavaishFloat(float value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "float";
    }
}

final class JavaishString implements JavaishVal {
    String value;
    JavaishType type = JavaishType.STRING;
    public String getValue() {
        return value;
    }
   
    JavaishString(String value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "String";
    }
}

final class JavaishBoolean implements JavaishVal {
    Boolean value;
    JavaishType type = JavaishType.BOOLEAN;
    public Boolean getValue() {
        return value;
    }
   
    JavaishBoolean(Boolean value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "boolean";
    }
}

final class JavaishListVal implements JavaishVal {
    JavaishList list;
    JavaishType type = JavaishType.LIST;
    public JavaishList getValue() {
        return list;
    }
    JavaishListVal(JavaishList value){
        this.list = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "List";
    }
}