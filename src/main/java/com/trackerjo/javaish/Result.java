package com.trackerjo.javaish;


public class Result {
    boolean pastResult;

    public Result(boolean pastResult) {
        this.pastResult = pastResult;
    }
    
    public boolean getResult(){
        return pastResult;
    }

    public void setResult(boolean pastResult){
        this.pastResult = pastResult;
    }
}

