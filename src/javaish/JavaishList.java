package javaish;

import java.util.List;

import javaish.JavaishVal.JavaishType;

public sealed interface JavaishList {
    public Object getValue();
    public JavaishType getType();
    public JavaishType getInnerType();
    public String typeString();
    

}

final class JavaishIntList implements JavaishList {
    List<JavaishInt> value;
    JavaishType type = JavaishType.INTLIST;
    public List<JavaishInt> getValue() {
        return value;
    }
    JavaishIntList(List<JavaishInt> value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "int[]";
    }
    public JavaishType getInnerType(){
        return JavaishType.INT;
    }

    
}

final class JavaishFloatList implements JavaishList {
    List<JavaishFloat> value;
    JavaishType type = JavaishType.FLOATLIST;
    public List<JavaishFloat> getValue() {
        return value;
    }
    JavaishFloatList(List<JavaishFloat> value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "float[]";
    }
    public JavaishType getInnerType(){
        return JavaishType.FLOAT;
    }
}

final class JavaishStringList implements JavaishList {
    List<JavaishString> value;
    JavaishType type = JavaishType.STRINGLIST;
    public List<JavaishString> getValue() {
        return value;
    }
    JavaishStringList(List<JavaishString> value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "String[]";
    }
    public JavaishType getInnerType(){
        return JavaishType.STRING;
    }
}

final class JavaishBooleanList implements JavaishList {
    List<JavaishBoolean> value;
    JavaishType type = JavaishType.BOOLEANLIST;
    public List<JavaishBoolean> getValue() {
        return value;
    }
    JavaishBooleanList(List<JavaishBoolean> value){
        this.value = value;
    }
    public JavaishType getType() {
        return type;
    }
    public String typeString(){
        return "boolean[]";
    }
    public JavaishType getInnerType(){
        return JavaishType.BOOLEAN;
    }
}