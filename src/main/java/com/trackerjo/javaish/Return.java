package com.trackerjo.javaish;

public class Return {
    boolean hasReturn;
    JavaishVal value;

    public Return(boolean hasReturn, JavaishVal value2) {
        this.hasReturn = hasReturn;
        this.value = value2;
    }

    public boolean hasReturn(){
        return hasReturn;
    }

    public JavaishVal getValue(){
        return value;
    }

    public void setHasReturn(boolean hasReturn){
        this.hasReturn = hasReturn;
    }

    public void setValue(JavaishVal value){
        this.value = value;
    }
}