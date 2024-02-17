package com.trackerjo;

import java.io.IOException;

import org.json.JSONObject;

import com.trackerjo.javaish.Runner;
import com.trackerjo.javaish.State;

public class Main {
    public static void main(String[] args) throws IOException  {
        String oldStateString = "{\"globalVariables\":{\"intList\":{\"name\":\"intList\",\"type\":\"INTLIST\",\"value\":{\"innerType\":\"INT\",\"length\":4,\"type\":\"INTLIST\",\"value\":[1,3,43,4]}},\"x\":{\"name\":\"x\",\"type\":\"INT\",\"value\":\"43\"}},\"returnVal\":{\"hasReturn\":false,\"value\":\"null\"},\"localVariables\":{\"intList\":{\"name\":\"intList\",\"type\":\"INTLIST\",\"value\":{\"innerType\":\"INT\",\"length\":4,\"type\":\"INTLIST\",\"value\":[1,3,43,4]}}},\"pastResult\":false,\"isGlobal\":true,\"body\":[],\"isComplete\":false,\"currentLine\":2,\"states\":[]}";
        // JSONObject oldStateJSON = new JSONObject(oldStateString);
        // State oldState = Runner.convertJSONToState(oldStateJSON);
        // System.out.println(oldState.isGlobal() + " - Is Global");
        // System.out.println(oldState.getCurrentLine());
        // Runner.convertFile("src/main/java/com/trackerjo/debug.javaish", "debug", "python");
        Runner.runFile("src/main/java/com/trackerjo/debug.javaish");
        // State newState = Runner.debugFile("src/main/java/com/trackerjo/debug.javaish", oldState);
    //     System.out.println(newState.getStates().size());
    //     System.out.println(newState.isComplete());
    //    System.out.println(Runner.convertStateToJSON(newState, false));
    }
}

//Declare Output State: {"globalVariables":{},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":false,"isComplete":true,"currentLine":4,"states":[]}
//Function Output State: {"globalVariables":{"bill":{"args":"STRING test","line":5,"name":"bill","type":"FUNCTION","body":[{"printStmt":[{"elementType":"STRING","element":"ran line 5"}],"printStmtColumn":6,"line":6,"printStmtReturnType":"STRING","type":"PRINT"},{"printStmt":[{"elementType":"STRING","element":"ran line 6"}],"printStmtColumn":6,"line":7,"printStmtReturnType":"STRING","type":"PRINT"}]}},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":true,"isComplete":false,"currentLine":5,"states":[]}
//Call Function Output State: {"globalVariables":{"bill":{"args":"STRING test","line":5,"name":"bill","type":"FUNCTION","body":[{"printStmt":[{"elementType":"STRING","element":"ran line 5"}],"printStmtColumn":0,"line":6,"printStmtReturnType":"STRING","type":"PRINT"},{"printStmt":[{"elementType":"STRING","element":"ran line 6"}],"printStmtColumn":0,"line":7,"printStmtReturnType":"STRING","type":"PRINT"}]}},"returnVal":{"hasReturn":false,"value":"null"},"localVariables":{"test":{"name":"test","type":"STRING","value":"test now"}},"pastResult":false,"isGlobal":true,"isComplete":true,"currentLine":6,"states":[]}