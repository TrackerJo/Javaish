package com.trackerjo.javaish;


import java.util.ArrayList;
import java.util.List;


public class State {
    List<Statements> statements;
    Variables globalVariables;
    Variables localVariables;
    Result pastResult;
    Return returnVal;
    int currentLine;
    int currentRuntimeLine = 0;
    boolean isGlobal;
    boolean isComplete;
    List<State> states = new ArrayList<>();

    public State(List<Statements> statements, Variables globalVariables, Variables localVariables, Result pastResult, Return returnVal, int currentLine, boolean isGlobal, boolean isComplete, int currentRuntimeLine) {
        this.statements = statements;
        this.globalVariables = globalVariables;
        this.localVariables = localVariables;
        this.pastResult = pastResult;
        this.returnVal = returnVal;
        this.currentLine = currentLine;
        this.isGlobal = isGlobal;
        this.isComplete = isComplete;
        this.currentRuntimeLine = currentRuntimeLine;
    }

    public State(List<Statements> statements, Variables globalVariables, Variables localVariables, Result pastResult, Return returnVal, int currentLine, boolean isGlobal, boolean isComplete, List<State> states, int currentRuntimeLine) {
        this.statements = statements;
        this.globalVariables = globalVariables;
        this.localVariables = localVariables;
        this.pastResult = pastResult;
        this.returnVal = returnVal;
        this.currentLine = currentLine;
        this.isGlobal = isGlobal;
        this.isComplete = isComplete;
        this.states = states;
        this.currentRuntimeLine = currentRuntimeLine;
    }

    public int getCurrentRuntimeLine() {
        return currentRuntimeLine;
    }

    public void setCurrentRuntimeLine(int currentRuntimeLine) {
        this.currentRuntimeLine = currentRuntimeLine;
    }

    public List<Statements> getStatements() {
        return statements;
    }

    public Variables getGlobalVariables() {
        return globalVariables;
    }

    public Variables getLocalVariables() {
        return localVariables;
    }

    public Result getPastResult() {
        return pastResult;
    }

    public Return getReturnVal() {
        return returnVal;
    }

    public void setStatements(List<Statements> statements) {
        this.statements = statements;
    }

    public void setGlobalVariables(Variables globalVariables) {
        this.globalVariables = globalVariables;
    }

    public void setLocalVariables(Variables localVariables) {
        this.localVariables = localVariables;
    }

    public void setPastResult(Result pastResult) {
        this.pastResult = pastResult;
    }

    public void setReturnVal(Return returnVal) {
        this.returnVal = returnVal;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public void incrementCurrentLine() {
        currentLine++;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public List<State> getStates() {
        return states;
    }

    public void addState(State state) {
        states.add(state);
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public void removeLastState() {
        states.remove(states.size() - 1);
    }

    public State getLastState() {
        return states.get(states.size() - 1);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
        if (complete) {
            states.clear();
        }
    }

    public void printState(){
        
        // System.out.println("STATE:");
        // System.out.println("  GLOBAL VARIABLES:");
        // printVars(globalVariables);
        // System.out.println("  LOCAL VARIABLES:");
        // printVars(localVariables);
        // System.out.println("  PAST RESULT: " + pastResult.toString());
        // System.out.println("  RETURN VALUE: " + returnVal.toString());
        // System.out.println("  CURRENT LINE: " + currentLine);
        // System.out.println("  IS GLOBAL: " + isGlobal);
        // System.out.println("  IS COMPLETE: " + isComplete);
        // System.out.println("  STATES:");
        // for (State state : states) {
        //     state.printState();
        // }
    }
    
    private static void printVars(Variables variables) {
        System.out.println("Variables:");
        System.out.println("Integers:");
        for (IntVar intVar : variables.intVariables) {
            System.out.println(intVar.name + ": " + intVar.value.getValue());
        }
        System.out.println("Floats:");
        for (FloatVar floatVar : variables.floatVariables) {
            System.out.println(floatVar.name + ": " + floatVar.value.getValue());
        }
        System.out.println("Booleans:");
        for (BoolVar boolVar : variables.boolVariables) {
            System.out.println(boolVar.name + ": " + boolVar.value.getValue());
        }
        System.out.println("Strings:");
        for (StringVar stringVar : variables.stringVariables) {
            System.out.println(stringVar.name + ": " + stringVar.value.getValue());
        }
        System.out.println("Int Lists:");
        for (IntList intListVar : variables.intLists) {
            List<JavaishInt> list = intListVar.getValue().getList();
            String listString = "[";
            for (JavaishInt javaishInt : list) {
                listString += javaishInt.getValue() + ", ";
            }
            listString = listString.substring(0, listString.length() - 2);
            listString += "]";
            System.out.println(intListVar.name + ": " + listString);
            

        }
        System.out.println("Float Lists:");
        for (FloatList floatListVar : variables.floatLists) {
            List<JavaishFloat> list = floatListVar.getValue().getList();
            String listString = "[";
            for (JavaishFloat javaishFloat : list) {
                listString += javaishFloat.getValue() + ", ";
            }
            listString = listString.substring(0, listString.length() - 2);
            listString += "]";
            System.out.println(floatListVar.name + ": " + listString);
            

        }
        System.out.println("String Lists:");
        for (StringList stringListVar : variables.stringLists) {
            List<JavaishString> list = stringListVar.getValue().getList();
            String listString = "[";
            for (JavaishString javaishString : list) {
                listString += javaishString.getValue() + ", ";
            }
            listString = listString.substring(0, listString.length() - 2);
            listString += "]";
            System.out.println(stringListVar.name + ": " + listString);
            

        }
        System.out.println("Booleans Lists:");
        for (BoolList boolListVar : variables.boolLists) {
            List<JavaishBoolean> list = boolListVar.getValue().getList();
            String listString = "[";
            for (JavaishBoolean javaishBool : list) {
                listString += javaishBool.getValue() + ", ";
            }
            listString = listString.substring(0, listString.length() - 2);
            listString += "]";
            System.out.println(boolListVar.name + ": " + listString);
            

        }
        System.out.println("Functions:");
        for (FunctionVar functionVar : variables.functions.values()) {
            System.out.println(functionVar.name + "(" + getArgsString(functionVar.args) + ")");
            
        }
    }

    private static String getParamString(Expression[] params){
        String paramString = "";
        for (Expression param : params) {
            
            if(param == null){continue;}
            if(param.getElements() == null){continue;}
            String exprString = param.toString();
            
            paramString += exprString + ", ";
        }
        if(paramString.length() < 2){
            return "";
        }
        return paramString.substring(0, paramString.length() - 2);
    }

    private static String getArgsString(Argument[] args){
        if(args == null){
            return "";
        }
        String argsString = "";
        for (Argument arg : args) {
            if(arg == null){continue;}
            argsString += arg.getType() + " " + arg.getName() + ", ";
        }
        if(argsString.length() < 2){
            return "";
        }
        return argsString.substring(0, argsString.length() - 2);
    }
}
   
