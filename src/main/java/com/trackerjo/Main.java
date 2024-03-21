package com.trackerjo;

import java.io.IOException;

import org.json.JSONObject;

import com.trackerjo.javaish.Runner;
import com.trackerjo.javaish.State;

public class Main {
    public static void main(String[] args) throws IOException  {
        String oldStateString = "{\"globalVariables\":{\"x\":{\"name\":\"x\",\"index\":0,\"type\":\"INT\",\"value\":\"5\"}},\"returnVal\":{\"hasReturn\":false,\"value\":\"null\"},\"localVariables\":{\"x\":{\"name\":\"x\",\"index\":0,\"type\":\"INT\",\"value\":\"5\"}},\"pastResult\":false,\"currentRuntimeLine\":2,\"isGlobal\":true,\"isLoop\":false,\"body\":[],\"loopStartLine\":0,\"isComplete\":false,\"currentLine\":2,\"states\":[{\"globalVariables\":{\"x\":{\"name\":\"x\",\"index\":0,\"type\":\"INT\",\"value\":\"5\"}},\"returnVal\":{\"hasReturn\":false,\"value\":\"null\"},\"localVariables\":{\"x\":{\"name\":\"x\",\"index\":0,\"type\":\"INT\",\"value\":\"4\"}},\"pastResult\":false,\"currentRuntimeLine\":4,\"isGlobal\":false,\"isLoop\":true,\"body\":[{\"printStmt\":[{\"elementType\":\"STRING\",\"element\":\"HEEE\"}],\"line\":3,\"printStmtReturnType\":\"STRING\",\"type\":\"PRINT\"},{\"mutationExpr\":[{\"elementType\":\"INTEGER\",\"element\":\"1\"}],\"line\":4,\"mutationType\":\"SUBTRACT\",\"mutationReturnType\":\"NUMBER\",\"mutationVar\":\"x\",\"type\":\"MUTATION\"},{\"printStmt\":[{\"elementType\":\"VARIABLE\",\"element\":\"x\"}],\"line\":5,\"printStmtReturnType\":\"STRING\",\"type\":\"PRINT\"}],\"loopStartLine\":2,\"isComplete\":false,\"currentLine\":2,\"states\":[]}]}";
        JSONObject oldStateJSON = new JSONObject(oldStateString);
        // State oldState = Runner.convertJSONToState(oldStateJSON);
        // System.out.println(oldState.isGlobal() + " - Is Global");
        // System.out.println(oldState.getCurrentLine());
        Runner.convertFile("src/main/java/com/trackerjo/debug.javaish", "TicTacToe", "java");
        // State newState = Runner.debugFile("src/main/java/com/trackerjo/debug.javaish", oldState);
        // JSONObject newStateJSON = Runner.convertStateToJSON(newState, false);
        // System.out.println(newStateJSON.toString());
        // State newState = Runner.debugFile("src/main/java/com/trackerjo/debug.javaish", oldState);
    //     System.out.println(newState.getStates().size());
    //     System.out.println(newState.isComplete());
    //    System.out.println(Runner.convertStateToJSON(newState, false));
       

    }
}

//Declare Output State: {"globalVariables":{},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":false,"isComplete":true,"currentLine":4,"states":[]}
//Function Output State: {"globalVariables":{"bill":{"args":"STRING test","line":5,"name":"bill","type":"FUNCTION","body":[{"printStmt":[{"elementType":"STRING","element":"ran line 5"}],"printStmtColumn":6,"line":6,"printStmtReturnType":"STRING","type":"PRINT"},{"printStmt":[{"elementType":"STRING","element":"ran line 6"}],"printStmtColumn":6,"line":7,"printStmtReturnType":"STRING","type":"PRINT"}]}},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":true,"isComplete":false,"currentLine":5,"states":[]}
//Call Function Output State: {"globalVariables":{"bill":{"args":"STRING test","line":5,"name":"bill","type":"FUNCTION","body":[{"printStmt":[{"elementType":"STRING","element":"ran line 5"}],"printStmtColumn":0,"line":6,"printStmtReturnType":"STRING","type":"PRINT"},{"printStmt":[{"elementType":"STRING","element":"ran line 6"}],"printStmtColumn":0,"line":7,"printStmtReturnType":"STRING","type":"PRINT"}]}},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":true,"isComplete":true,"currentLine":6,"states":[]}