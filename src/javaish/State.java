package javaish;


import java.util.ArrayList;
import java.util.List;


public class State {
    List<Statements> statements;
    Variables globalVariables;
    Variables localVariables;
    Result pastResult;
    Return returnVal;
    int currentLine;

    public State(List<Statements> statements, Variables globalVariables, Variables localVariables, Result pastResult, Return returnVal, int currentLine) {
        this.statements = statements;
        this.globalVariables = globalVariables;
        this.localVariables = localVariables;
        this.pastResult = pastResult;
        this.returnVal = returnVal;
        this.currentLine = currentLine;
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
}
