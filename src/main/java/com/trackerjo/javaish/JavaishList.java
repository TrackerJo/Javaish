package com.trackerjo.javaish;

import java.util.List;

import com.trackerjo.javaish.JavaishVal.JavaishType;

public interface JavaishList {
    public Object getList();
    public JavaishType getType();
    public JavaishType getInnerType();
    public String typeString();
    public String listString();
    public JavaishVal getValue(int index);
    public int getLength();
    

}

final class JavaishIntList implements JavaishList {
    List<JavaishInt> value;
    JavaishType type = JavaishType.INTLIST;
    public List<JavaishInt> getList() {
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
    public String listString(){
        String listString = "[";
        for (JavaishInt i : value){
            listString += i.getType() + ",";
        }
        listString = listString.substring(0, listString.length() - 1);
        listString += "]";
        return listString;
    }

    public JavaishInt getValue(int index) {
        return value.get(index);
    }

    public int getLength() {
        return value.size();
    }

    
}

final class JavaishFloatList implements JavaishList {
    List<JavaishFloat> value;
    JavaishType type = JavaishType.FLOATLIST;
    public List<JavaishFloat> getList() {
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
    public String listString(){
        String listString = "[";
        for (JavaishFloat i : value){
            listString += i.getType() + ",";
        }
        listString = listString.substring(0, listString.length() - 1);
        listString += "]";
        return listString;
    }

    public JavaishFloat getValue(int index) {
        return value.get(index);
    }

    public int getLength() {
        return value.size();
    }
}

final class JavaishStringList implements JavaishList {
    List<JavaishString> value;
    JavaishType type = JavaishType.STRINGLIST;
    public List<JavaishString> getList() {
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
    public String listString(){
        String listString = "[";
        for (JavaishString i : value){
            listString += i.getType() + ",";
        }
        listString = listString.substring(0, listString.length() - 1);
        listString += "]";
        return listString;
    }

    public JavaishString getValue(int index) {
        return value.get(index);
    }

    public int getLength() {
        return value.size();
    }
}

final class JavaishBooleanList implements JavaishList {
    List<JavaishBoolean> value;
    JavaishType type = JavaishType.BOOLEANLIST;
    public List<JavaishBoolean> getList() {
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
    public String listString(){
        String listString = "[";
        for (JavaishBoolean i : value){
            listString += i.getType() + ",";
        }
        listString = listString.substring(0, listString.length() - 1);
        listString += "]";
        return listString;
    }

    public JavaishBoolean getValue(int index) {
        return value.get(index);
    }

    public int getLength() {
        return value.size();
    }
}