
package com.trackerjo.javaish;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.trackerjo.javaish.Element.ElementType;
import com.trackerjo.javaish.Expression.ExpressionReturnType;
import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.MutationType;
import com.trackerjo.javaish.Statements.StmtType;

public class Runner {

    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
        printStmts(statements.getBody(),0);
      
        Interpreter interpreter = new Interpreter(variables);
        interpreter.interpretFunction(statements.getBody(), null, null, "", true);
        printVars(variables);
        
        // Organizer organizer = new Organizer(statements);
        // List<Elements> elements = organizer.organize();
        //System.out.println(elements);
        //printElmts(elements);
       
        
    }

    public static void convertFile(String path, String projName, String translateTo) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
       // printStmts(statements.getBody(),0);
       List<String> lines = new ArrayList<>();
        if(translateTo.equals("java")){
           
       
            JavaTranslator translator = new JavaTranslator(variables, projName);
            translator.interpretFunction(statements.getBody(), null, null, "$main", true, true);

            lines = translator.getJavaLines();
        } else if(translateTo.equals("python")){
            PythonTranslator translator = new PythonTranslator(variables, projName);
            translator.interpretFunction(statements.getBody(), null, null, "$main", true, true);
            lines = translator.getPythonLines();
        }
        printJavaLines(lines);
        //Create java file
        String javaFile = "";
        for (String line : lines) {
            javaFile += line + "\n";
        }
        System.out.println(javaFile);
        
        Files.write(Paths.get("src/" + projName + (translateTo.equals("java") ? ".java" : ".py")), javaFile.getBytes());
    }

    public static void runRobotFile(String path, String projName, String robotIP) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
     
        PythonTranslator translator = new PythonTranslator(variables, projName, robotIP);
        translator.interpretFunction(statements.getBody(), null, null, "$main", true, true);
        List<String> lines = translator.getPythonLines();

        printJavaLines(lines);
        //Create java file
        String pythonFile = "";
        for (String line : lines) {
            pythonFile += line + "\n";
        }
        System.out.println(pythonFile);
        String encodedFile = URLEncoder.encode(pythonFile, "UTF-8");
        //Make a request to the HTTP server
        String urlS = "http://192.168.1.16:8080/run_string/" + encodedFile;
        System.out.println(urlS);
        URL url = new URL(urlS);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println(status);


        
    }

    public static State debugFile(String path, State oldState) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Variables variables1 = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        // System.out.println(statements.getBody());
        // printStmts(statements.getBody(),0);
        Result pastResult = new Result(false);  
       
        Return returnVal = new Return(false, null);
        if(oldState == null){
            oldState = new State(statements.getBody(), variables, variables1, pastResult, returnVal, 0, true, false, 0, false, 0, 0, false);
        } else {
            oldState.setStatements(statements.getBody());
        }
        Debugger debugger = new Debugger();
        // System.out.println(oldState.getCurrentLine() + " - CURRENT LINE");
        State newState = debugger.debugLine(oldState.getCurrentLine(), oldState);
        // State newState = debugLines(debugger, oldState, statements.getBody().size());
       // printVars(state.getGlobalVariables());
        return newState;
    }

    public static void parseString(String string){
        Variables variables = new Variables();
        Parser parser = new Parser(string, variables);
        Statements statements = parser.parse();
        // System.out.println(statements.getBody());
        // printStmts(statements.getBody(),0);
        // printVars(variables);
    }

    public static State debugString(String string){
        Variables variables = new Variables();
        Variables variables1 = new Variables();
        Parser parser = new Parser(string, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
        printStmts(statements.getBody(),0);
        Result pastResult = new Result(false);  
       
        Return returnVal = new Return(false, null);
        State state = new State(statements.getBody(), variables, variables1, pastResult, returnVal, 0, true, false, 0, false, 0, 0, false);
        Debugger debugger = new Debugger();
        state = debugLines(debugger, state, statements.getBody().size());
       // printVars(state.getGlobalVariables());
        return state;
    }

    private static State debugLines(Debugger debugger, State state, int maxLines){
        int lineNumber = 0;
        while(!state.isComplete){
            Debugger testDebugger = new Debugger();
            state = testDebugger.debugLine(lineNumber, state);
            System.out.println(state.getStates().size() + " - States Size");
            lineNumber = state.getCurrentLine();
            System.out.println(lineNumber + " - Line Number");
            System.out.println(convertStateToJSON(state, false));
            System.out.println(convertStateToJSON(convertJSONToState(convertStateToJSON(state, false)), false));
            //printVars(state.getGlobalVariables());
            if(state.isComplete){
                JOptionPane.showMessageDialog(null, "You've reached the end of the file");
                return state;
            }
            String input = JOptionPane.showInputDialog(null, "Do you want to continue?");
            if(!input.equals("y")){
                return state;
            }
        }
        JOptionPane.showMessageDialog(null, "You've reached the end of the file");
        return state;
    }

    private static void printJavaLines(List<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
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
    
    private static void printStmts(List<Statements> statements, int indent) {
        for (Statements statement : statements) {
            StmtType type = statement.getType();
            int lineNumber = statement.getLine();
            System.out.print(lineNumber + ":");
            //Print indent
            for (int i = 0; i < indent; i++) {
                System.out.print("    ");
            }
            switch (type) {
                case DECLARATION:
                    DeclarationStmt declaration = (DeclarationStmt) statement;
                    String name = declaration.getName();
                    JavaishType varType = declaration.getVarType();
                    String value = declaration.getValue().toString();

                    
                  
                    System.out.println("Declaration: " + name + " " + varType + " " + value);

                    
                    break;
                case IF:
                    IfStmt ifStmt = (IfStmt) statement;
                    System.out.println("If: " + ifStmt.getCondition());
                    if(ifStmt.getBody() != null){
                        printStmts(ifStmt.getBody(), indent + 1);
                    }

                   
                    break;
                case ELSE:
                    ElseStmt elseStmt = (ElseStmt) statement;
                    System.out.println("Else:");
                    if(elseStmt.getBody() != null){
                        printStmts(elseStmt.getBody(), indent + 1);
                    }
                    
                    break;
                case ELSEIF:
                    ElseIfStmt elseIfStmt = (ElseIfStmt) statement;
                    System.out.println("ElseIf: " + elseIfStmt.getCondition());
                    if(elseIfStmt.getBody() != null){
                        printStmts(elseIfStmt.getBody(), indent + 1);
                    }
                    
                    break;
                case END:
                   
                    System.out.println("End");
                    
                    break;
                
                case RETURN:
                    ReturnStmt returnStmt = (ReturnStmt) statement;
                    System.out.println("Return: " + returnStmt.getValue().toString());
                    
                    break;
                case MUTATION:
                    MutationStmt mutationStmt = (MutationStmt) statement;
                    System.out.println("Mutation: " + mutationStmt.getVarName() + " " + mutationStmt.getMutationType() + " " + mutationStmt.getValue().toString());
                    
                    break;
                case FUNCTION:
                    FunctionStmt functionStmt = (FunctionStmt) statement;
                    System.out.println("Function: " + functionStmt.getName() + " " + getArgsString(functionStmt.getArgs()) + "ARG Length: " + functionStmt.getArgs().length);
                    
                    if(functionStmt.getBody() != null){
                        printStmts(functionStmt.getBody(), indent + 1);
                    }
                    break;
                case CALL:
                    CallStmt callStmt = (CallStmt) statement;
                    System.out.println("Call: " + callStmt.getName() + " " + getParamString(callStmt.getParams()));
                    

                    break;
                case ASSIGNMENT:
                    AssignmentStmt assignmentStmt = (AssignmentStmt) statement;
                    System.out.println("Assignment: " + assignmentStmt.getName() + " " + assignmentStmt.getValue().toString());
                    
                    break;
                case FOREACH:
                    ForEachStmt foreachStmt = (ForEachStmt) statement;
                    System.out.println("Foreach: " + foreachStmt.getTempVar() + " in " + foreachStmt.getListVar());
                    if(foreachStmt.getBody() != null){
                        printStmts(foreachStmt.getBody(), indent + 1);
                    }
                    
                    break;
                case FORWHEN:
                    ForWhenStmt forwhenStmt = (ForWhenStmt) statement;
                    System.out.println("ForWhen: " + forwhenStmt.getCondition() + " Increment " + forwhenStmt.getIncVar() + " by " + forwhenStmt.getIncrement());
                    if(forwhenStmt.getBody() != null){
                        printStmts(forwhenStmt.getBody(), indent + 1);
                    }
                    break;
                case SHOWMSGBOX:
                    ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) statement;
                    System.out.println("ShowMsgBox: " + showMsgBoxStmt.getValue().toString());
                    break;
                case PRINT:
                    PrintStmt printStmt = (PrintStmt) statement;
                    System.out.println("Print: " + printStmt.getValue().toString());
                    break;
                case WHILE:
                    WhileStmt whileStmt = (WhileStmt) statement;
                    System.out.println("While: " + whileStmt.getCondition());
                    if(whileStmt.getBody() != null){
                        printStmts(whileStmt.getBody(), indent + 1);
                    }
                    break;
                case REMOVEALLFROM:
                    RemoveAllFromStmt removeAllFromStmt = (RemoveAllFromStmt) statement;
                    System.out.println("RemoveAll: " + removeAllFromStmt.getValue() + " From:" + removeAllFromStmt.getListName());
                    break;
                case REMOVEFROM:
                    RemoveFromStmt removeFromStmt = (RemoveFromStmt) statement;
                    System.out.println("Remove: " + removeFromStmt.getValue() + " From:" + removeFromStmt.getListName());
                    break;
                case REMOVEAT:
                    RemoveAtStmt removeAtStmt = (RemoveAtStmt) statement;
                    System.out.println("RemoveAt: " + removeAtStmt.getLocation() + " From:" + removeAtStmt.getListName());
                    break;
                default:
                    break;
            }
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

    public static void error(String message, int line, int column) {
        System.err.println("Error at line " + line + ", column " + column + ": " + message);
        System.exit(1);
    }

    public static JSONObject convertStateToJSON(State state, boolean nestedState){
        JSONObject stateJSON = new JSONObject();
        if(!nestedState){
            stateJSON.put("body", new JSONArray());
        } else {
            List<Statements> body = state.getStatements();
            List<JSONObject> bodyJSON = new ArrayList<>();
            for (Statements statements2 : body) {
                bodyJSON.add(convertStatementToJSON(statements2));
            }
            stateJSON.put("body", bodyJSON);
        }
        stateJSON.put("isComplete", state.isComplete());
        stateJSON.put("currentRuntimeLine", state.getCurrentRuntimeLine());
        stateJSON.put("isGlobal", state.isGlobal());
        stateJSON.put("isLoop", state.isLoop());
        stateJSON.put("currentLine", state.getCurrentLine());
        stateJSON.put("returnVal", convertReturnToJSON(state.getReturnVal()));
        stateJSON.put("pastResult", state.getPastResult().getResult());
        stateJSON.put("loopStartLine", state.getLoopStartLine());
        stateJSON.put("inForWhenLoop", state.isInForWhenLoop());
        stateJSON.put("forIndex", state.getForIndex());
        stateJSON.put("globalVariables", convertVariablesToJSON(state.getGlobalVariables()));
        stateJSON.put("localVariables", convertVariablesToJSON(state.getLocalVariables()));


        List<State> states = state.getStates();
        List<JSONObject> statesJSON = new ArrayList<>();
        for (State state2 : states) {
            statesJSON.add(convertStateToJSON(state2, true));
        }
        stateJSON.put("states", statesJSON);
        return stateJSON;
    }

    public static JSONObject convertReturnToJSON(Return returnVal){
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("hasReturn", returnVal.hasReturn());
        if(returnVal.getValue() == null){
            returnJSON.put("value", "null");
        } else {
            returnJSON.put("value", returnVal.getValue().getValue().toString());
            returnJSON.put("type", returnVal.getValue().typeString());
        }
        return returnJSON;
    }

    public static JSONObject convertVariablesToJSON(Variables variables){
        JSONObject variablesJSON = new JSONObject();
        Map<String, JavaishType> variablesMap = variables.getAllVariables();
        int numVariables = 0;
        for (Entry<String, JavaishType> entry : variablesMap.entrySet()) {
            //Get Value
            JavaishVal value = variables.getVariableValue(entry.getKey());
            JSONObject variableJSON = new JSONObject();
            variableJSON.put("name", entry.getKey());
            variableJSON.put("type", entry.getValue().toString());
            System.out.println(entry.getValue().toString());
            if(entry.getValue().toString().equals("INTLIST") || entry.getValue().toString().equals("FLOATLIST") || entry.getValue().toString().equals("STRINGLIST") || entry.getValue().toString().equals("BOOLEANLIST")){
                variableJSON.put("value", convertListToJSON((JavaishListVal) value));
            } else {
                variableJSON.put("value", value.getValue().toString());
            }
            variableJSON.put("index", numVariables);
            numVariables++;
            

            variablesJSON.put(entry.getKey(), variableJSON);
        }
        Map<String, FunctionVar> functionsMap = variables.getFunctions();
        for (Entry<String, FunctionVar> entry : functionsMap.entrySet()) {
            //Get Value
            FunctionVar value = entry.getValue();
            JSONObject functionJSON = new JSONObject();
            functionJSON.put("name", entry.getKey());
            functionJSON.put("type", "FUNCTION");
            functionJSON.put("args", getArgsString(value.getArgs()));
            //line number
            functionJSON.put("line", value.getLineNumber());
            List<Statements> body = value.getBody();
            List<JSONObject> bodyJSON = new ArrayList<>();
            for (Statements statements2 : body) {
                bodyJSON.add(convertStatementToJSON(statements2));
            }
            functionJSON.put("body", bodyJSON);
            variablesJSON.put(entry.getKey(), functionJSON);
        }
        return variablesJSON;
    }

    public static JSONObject convertListToJSON(JavaishListVal listVal){
        JSONObject listJSON = new JSONObject();
        JavaishList list = listVal.getValue();
        listJSON.put("type", list.getType().toString());
        listJSON.put("innerType", list.getInnerType().toString());
        listJSON.put("length", list.getLength());
        JSONArray listJSONArray = new JSONArray();
        for (int i = 0; i < list.getLength(); i++) {
            listJSONArray.put(list.getValue(i).getValue());
        }
        listJSON.put("value", listJSONArray);
        return listJSON;
    }

    public static JSONObject convertStatementToJSON(Statements statements){
        JSONObject statementJSON = new JSONObject();
        statementJSON.put("type", statements.getType().toString());
        statementJSON.put("line", statements.getLine());
        switch (statements.getType()) {
            case DECLARATION:
                DeclarationStmt declaration = (DeclarationStmt) statements;
                String name = declaration.getName();
                JavaishType varType = declaration.getVarType();
                String value = declaration.getValue().toString();
                Element[] elements = declaration.getValue().getElements();
                JSONArray elementsJSONArray = new JSONArray();
                for (Element element : elements) {
                    if(element == null){continue;}
                    System.out.print("ELEMENT: ");
                    JSONObject elementJSON = convertElementToJSON(element);
                    elementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType returnType = declaration.getValue().getReturnType();
                statementJSON.put("name", name);
                statementJSON.put("varType", varType.toString());
                statementJSON.put("value", elementsJSONArray);

                statementJSON.put("returnType", returnType.toString());
                break;
            case IF:
                IfStmt ifStmt = (IfStmt) statements;
                Expression condition = ifStmt.getCondition();
                Element[] conditionElements = condition.getElements();
                JSONArray conditionElementsJSONArray = new JSONArray();
                for (Element element : conditionElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    conditionElementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType ifreturnType = condition.getReturnType();
                List<Statements> body = ifStmt.getBody();
                List<JSONObject> bodyJSON = new ArrayList<>();
                for (Statements statements2 : body) {
                    bodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("condition", conditionElementsJSONArray);

                statementJSON.put("ifreturnType", ifreturnType.toString());
                statementJSON.put("body", bodyJSON);
                break;
            case ELSE:
                ElseStmt elseStmt = (ElseStmt) statements;
                List<Statements> ebody = elseStmt.getBody();
                List<JSONObject> ebodyJSON = new ArrayList<>();
                for (Statements statements2 : ebody) {
                    ebodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("ebody", ebodyJSON);
                break;
            case ELSEIF:
                ElseIfStmt elseIfStmt = (ElseIfStmt) statements;
                Expression efcondition = elseIfStmt.getCondition();
                Element[] efconditionElements = efcondition.getElements();
                JSONArray efconditionElementsJSONArray = new JSONArray();
                for (Element element : efconditionElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    // elementJSON.put("elementReturnType", element.getReturnType().toString());
                    efconditionElementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType efreturnType = efcondition.getReturnType();
                List<Statements> efbody = elseIfStmt.getBody();
                List<JSONObject> efbodyJSON = new ArrayList<>();
                for (Statements statements2 : efbody) {
                    efbodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("efcondition", efconditionElementsJSONArray);

                statementJSON.put("efreturnType", efreturnType.toString());
                statementJSON.put("efbody", efbodyJSON);
                break;
            case END:
                break;

            case RETURN:
                ReturnStmt returnStmt = (ReturnStmt) statements;
                if(returnStmt.getValue() == null){
                    statementJSON.put("returnExpr", "null");
                    break;
                }
                Expression returnExpr = returnStmt.getValue();
                Element[] returnElements = returnExpr.getElements();
                JSONArray returnElementsJSONArray = new JSONArray();
                for (Element element : returnElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    returnElementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType returnReturnType = returnExpr.getReturnType();
                statementJSON.put("returnExpr", returnElementsJSONArray);

                statementJSON.put("returnReturnType", returnReturnType.toString());
                break;
            case MUTATION:
                MutationStmt mutationStmt = (MutationStmt) statements;
                Expression mutationExpr = mutationStmt.getValue();
                Element[] mutationElements = mutationExpr.getElements();
                JSONArray mutationElementsJSONArray = new JSONArray();
                for (Element element : mutationElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    mutationElementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType mutationReturnType = mutationExpr.getReturnType();
                statementJSON.put("mutationExpr", mutationElementsJSONArray);
                statementJSON.put("mutationType", mutationStmt.getMutationType().toString());
                statementJSON.put("mutationVar", mutationStmt.getVarName());

                statementJSON.put("mutationReturnType", mutationReturnType.toString());
                break;
            case FUNCTION:
                FunctionStmt functionStmt = (FunctionStmt) statements;
                List<Statements> fbody = functionStmt.getBody();
                List<JSONObject> fbodyJSON = new ArrayList<>();
                for (Statements statements2 : fbody) {
                    fbodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("name", functionStmt.getName());
                statementJSON.put("args", getArgsString(functionStmt.getArgs()));
                statementJSON.put("fbody", fbodyJSON);
                break;
            case CALL:
                CallStmt callStmt = (CallStmt) statements;
                statementJSON.put("name", callStmt.getName());
                Expression[] params = callStmt.getParams();
                
                JSONArray paramsJSONArray = new JSONArray();
                for (Expression expression : params) {
                    if(expression == null){continue;}
                    JSONObject paramJSON = new JSONObject();
                    Element[] elementsCall = expression.getElements();
                    JSONArray elementsCallJSONArray = new JSONArray();
                    for (Element element : elementsCall) {
                        if(element == null){continue;}
                        JSONObject elementJSON = convertElementToJSON(element);
                        elementsCallJSONArray.put(elementJSON);
                    }
                    paramJSON.put("param", elementsCallJSONArray);

                    paramJSON.put("paramReturnType", expression.getReturnType().toString());
                    paramsJSONArray.put(paramJSON);
                }

                statementJSON.put("params", paramsJSONArray);
                break;
            case ASSIGNMENT:
                AssignmentStmt assignmentStmt = (AssignmentStmt) statements;
                Expression assignmentExpr = assignmentStmt.getValue();
                Element[]   assignmentElements = assignmentExpr.getElements();
                JSONArray assignmentElementsJSONArray = new JSONArray();
                for (Element element : assignmentElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    assignmentElementsJSONArray.put(elementJSON);
                }

                ExpressionReturnType assignmentReturnType = assignmentExpr.getReturnType();
                statementJSON.put("name", assignmentStmt.getName());
                statementJSON.put("assignmentExpr", assignmentElementsJSONArray);

                statementJSON.put("assignmentReturnType", assignmentReturnType.toString());
                break;
            case FOREACH:
                ForEachStmt foreachStmt = (ForEachStmt) statements;
                List<Statements> febody = foreachStmt.getBody();
                List<JSONObject> febodyJSON = new ArrayList<>();
                for (Statements statements2 : febody) {
                    febodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("tempVar", foreachStmt.getTempVar());
                statementJSON.put("listVar", foreachStmt.getListVar());
                statementJSON.put("febody", febodyJSON);
                break;
            case FORWHEN:
                ForWhenStmt forwhenStmt = (ForWhenStmt) statements;

                ExpressionReturnType forwhenReturnType = forwhenStmt.getCondition().getReturnType();
                List<Statements> fwbody = forwhenStmt.getBody();
                List<JSONObject> fwbodyJSON = new ArrayList<>();
                for (Statements statements2 : fwbody) {
                    fwbodyJSON.add(convertStatementToJSON(statements2));
                }
                Element[] forwhenConditionElements = forwhenStmt.getCondition().getElements();
                JSONArray forwhenConditionElementsJSONArray = new JSONArray();
                for (Element element : forwhenConditionElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    forwhenConditionElementsJSONArray.put(elementJSON);
                }
                Element[] forwhenIncElements = forwhenStmt.getIncrement().getElements();
                JSONArray forwhenIncElementsJSONArray = new JSONArray();
                for (Element element : forwhenIncElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    forwhenIncElementsJSONArray.put(elementJSON);
                }
                statementJSON.put("condition", forwhenConditionElementsJSONArray);
                statementJSON.put("incVar", forwhenStmt.getIncVar());
                statementJSON.put("increment", forwhenIncElementsJSONArray);

                statementJSON.put("forwhenIncReturnType", forwhenStmt.getIncrement().getReturnType().toString());

                statementJSON.put("forwhenReturnType", forwhenReturnType.toString());
                statementJSON.put("fwbody", fwbodyJSON);
                break;
            case SHOWMSGBOX:
                ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) statements;
                Element[] showMsgBoxStmtElements = showMsgBoxStmt.getValue().getElements();
                JSONArray showMsgBoxStmtElementsJSONArray = new JSONArray();
                for (Element element : showMsgBoxStmtElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    showMsgBoxStmtElementsJSONArray.put(elementJSON);
                }
                statementJSON.put("showMsgBoxStmt", showMsgBoxStmtElementsJSONArray);

                statementJSON.put("showMsgBoxStmtReturnType", showMsgBoxStmt.getValue().getReturnType().toString());
                break;
            case PRINT:
                PrintStmt printStmt = (PrintStmt) statements;
                Element[] printStmtElements = printStmt.getValue().getElements();
                JSONArray printStmtElementsJSONArray = new JSONArray();
                for (Element element : printStmtElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    printStmtElementsJSONArray.put(elementJSON);
                }
                statementJSON.put("printStmt", printStmtElementsJSONArray);

                statementJSON.put("printStmtReturnType", printStmt.getValue().getReturnType().toString());
                break;
            case WHILE:
                WhileStmt whileStmt = (WhileStmt) statements;
                Element[] whileConditionElements = whileStmt.getCondition().getElements();
                JSONArray whileConditionElementsJSONArray = new JSONArray();
                for (Element element : whileConditionElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    whileConditionElementsJSONArray.put(elementJSON);
                }
                Expression whilecondition = whileStmt.getCondition();

                ExpressionReturnType whilereturnType = whilecondition.getReturnType();
                List<Statements> whilebody = whileStmt.getBody();
                List<JSONObject> whilebodyJSON = new ArrayList<>();
                for (Statements statements2 : whilebody) {
                    whilebodyJSON.add(convertStatementToJSON(statements2));
                }
                statementJSON.put("whilecondition", whileConditionElementsJSONArray);

                statementJSON.put("whilereturnType", whilereturnType.toString());
                statementJSON.put("whilebody", whilebodyJSON);
                break;
            case REMOVEALLFROM:
                RemoveAllFromStmt removeAllFromStmt = (RemoveAllFromStmt) statements;
                Element[] removeAllFromStmtElements = removeAllFromStmt.getValue().getElements();
                JSONArray removeAllFromStmtElementsJSONArray = new JSONArray();
                for (Element element : removeAllFromStmtElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    removeAllFromStmtElementsJSONArray.put(elementJSON);
                }
                statementJSON.put("removeAllFromStmt", removeAllFromStmtElementsJSONArray);
                statementJSON.put("removeAllFromStmtListName", removeAllFromStmt.getListName());

                statementJSON.put("removeAllFromStmtReturnType", removeAllFromStmt.getValue().getReturnType().toString());
                break;
            case REMOVEFROM:
                RemoveFromStmt removeFromStmt = (RemoveFromStmt) statements;
                Element[] removeFromStmtElements = removeFromStmt.getValue().getElements();
                JSONArray removeFromStmtElementsJSONArray = new JSONArray();
                for (Element element : removeFromStmtElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    removeFromStmtElementsJSONArray.put(elementJSON);
                }
                statementJSON.put("removeFromStmt", removeFromStmtElementsJSONArray);
                statementJSON.put("removeFromStmtListName", removeFromStmt.getListName());

                statementJSON.put("removeFromStmtReturnType", removeFromStmt.getValue().getReturnType().toString());
                break;
            case REMOVEAT:
                RemoveAtStmt removeAtStmt = (RemoveAtStmt) statements;
                Element[] removeAtStmtElements = removeAtStmt.getLocation().getElements();
                JSONArray removeAtStmtElementsJSONArray = new JSONArray();
                for (Element element : removeAtStmtElements) {
                    if(element == null){continue;}
                    JSONObject elementJSON = convertElementToJSON(element);
                    
                    removeAtStmtElementsJSONArray.put(elementJSON);
                }

                statementJSON.put("removeAtStmt", removeAtStmtElementsJSONArray);
                statementJSON.put("removeAtStmtListName", removeAtStmt.getListName());

                statementJSON.put("removeAtStmtReturnType", removeAtStmt.getLocation().getReturnType().toString());
                break;
            default:
                break;
        }
        return statementJSON;
    }

    public static JSONObject convertElementToJSON(Element element){
        JSONObject elementJSON = new JSONObject();
        ElementType type = element.getType();
        System.out.println(type + " - TYPE");

        elementJSON.put("elementType", type.toString());
        switch (type) {
            case EXPRESSION:
                ExpressionElmt expressionElmt = (ExpressionElmt) element;
                elementJSON.put("elementReturnType", expressionElmt.getExpression().getReturnType().toString());

                Element[] expressionElements = expressionElmt.getExpression().getElements();
                JSONArray expressionStmtElementsJSONArray = new JSONArray();
                for (Element expressionElement : expressionElements) {
                    if(expressionElement == null){continue;}
                    JSONObject expressionElementJSON = convertElementToJSON(expressionElement);
                    expressionStmtElementsJSONArray.put(expressionElementJSON);
                }
                elementJSON.put("element", expressionStmtElementsJSONArray);
                break;
            case NOT:
                NotElmt notElmt = (NotElmt) element;
                elementJSON.put("elementReturnType", notElmt.getExpression().getReturnType().toString());
                Element[] notElements = notElmt.getExpression().getElements();
                JSONArray notStmtElementsJSONArray = new JSONArray();
                for (Element expressionElement : notElements) {
                    if(expressionElement == null){continue;}
                    JSONObject expressionElementJSON = convertElementToJSON(expressionElement);
                    notStmtElementsJSONArray.put(expressionElementJSON);
                }
                elementJSON.put("element", notStmtElementsJSONArray);
                break;
            case CAST:
                CastElmt castElmt = (CastElmt) element;
                elementJSON.put("elementReturnType", castElmt.getExpression().getReturnType().toString());

                elementJSON.put("castType", castElmt.getCastType().toString());
                Element[] castElements = castElmt.getExpression().getElements();
                JSONArray castStmtElementsJSONArray = new JSONArray();
                for (Element expressionElement : castElements) {
                    if(expressionElement == null){continue;}
                    JSONObject expressionElementJSON = convertElementToJSON(expressionElement);
                    castStmtElementsJSONArray.put(expressionElementJSON);
                }
                elementJSON.put("element", castStmtElementsJSONArray);

                break;
            case SHOWINPUTBOX:
                ShowInputBoxElmt showInputBoxElmt = (ShowInputBoxElmt) element;
                elementJSON.put("elementReturnType", showInputBoxElmt.getValue().getReturnType().toString());
                Element[] showInputBoxElements = showInputBoxElmt.getValue().getElements();
                JSONArray showInputBoxStmtElementsJSONArray = new JSONArray();
                for (Element expressionElement : showInputBoxElements) {
                    if(expressionElement == null){continue;}
                    JSONObject expressionElementJSON = convertElementToJSON(expressionElement);
                    showInputBoxStmtElementsJSONArray.put(expressionElementJSON);
                }
                elementJSON.put("element", showInputBoxStmtElementsJSONArray);
                break;
            case FUNCTION:
                FunctionElmt functionElmt = (FunctionElmt) element;
                
                elementJSON.put("name", functionElmt.getName());
                Expression[] functionParams = functionElmt.getParams();
                JSONArray functionParamsJSONArray = new JSONArray();
                for (Expression expression : functionParams) {
                    if(expression == null){continue;}
                    JSONObject paramJSON = new JSONObject();
                    Element[] elementsCall = expression.getElements();
                    JSONArray elementsCallJSONArray = new JSONArray();
                    for (Element element2 : elementsCall) {
                        if(element2 == null){continue;}
                        JSONObject elementJSON2 = convertElementToJSON(element2);
                        elementsCallJSONArray.put(elementJSON2);
                    }
                    paramJSON.put("param", elementsCallJSONArray);

                    paramJSON.put("paramReturnType", expression.getReturnType().toString());
                    functionParamsJSONArray.put(paramJSON);
                }
                elementJSON.put("params", functionParamsJSONArray);
                break;
            case LIST:
                ListElmt listElmt = (ListElmt) element;
                elementJSON.put("elementListType", listElmt.getListType().toString());
                List<Expression> list = listElmt.getList();
                JSONArray listJSONArray = new JSONArray();
                for (Expression javaishVal : list) {
                    Element[] elementsCall = javaishVal.getElements();
                    JSONArray elementsCallJSONArray = new JSONArray();
                    for (Element element2 : elementsCall) {
                        if(element2 == null){continue;}
                        JSONObject elementJSON2 = convertElementToJSON(element2);
                        elementsCallJSONArray.put(elementJSON2);
                    }
                    listJSONArray.put(elementsCallJSONArray);
                }
                elementJSON.put("element", listJSONArray);
                break;
            case ARRAYLENGTH:
                ArrayLengthElmt arrayLengthElmt = (ArrayLengthElmt) element;

               
                elementJSON.put("element", arrayLengthElmt.getArrayName());
                break;
            case VARIABLE:
                VariableElmt variableElmt = (VariableElmt) element;
                elementJSON.put("element", variableElmt.getName());
                break;
            case LISTVAL:
                ListValElmt listValElmt = (ListValElmt) element;
                elementJSON.put("elementListName", listValElmt.getListName());
                Expression listVal = listValElmt.getIndex();
                JSONArray listValJSONArray = new JSONArray();
                Element[] elementsCall = listVal.getElements();

                for (Element element2 : elementsCall) {
                    if(element2 == null){continue;}
                    JSONObject elementJSON2 = convertElementToJSON(element2);
                    listValJSONArray.put(elementJSON2);
                }
                elementJSON.put("element", listValJSONArray);
                break;
                
        
            default:
                elementJSON.put("element", element.toString());

                break;
        }
        
        return elementJSON;
    }

    public static State convertJSONToState(JSONObject jsonObject){
        boolean isComplete = jsonObject.getBoolean("isComplete");
        boolean isGlobal = jsonObject.getBoolean("isGlobal");
        System.out.println(jsonObject.getBoolean("isGlobal") + " - isGlobal - " + isGlobal);
        int currentLine = jsonObject.getInt("currentLine");
        int currentRuntimeLine = jsonObject.getInt("currentRuntimeLine");
        int loopStartLine = jsonObject.getInt("loopStartLine");
        boolean isLoop = jsonObject.getBoolean("isLoop");
        boolean inForWhenLoop = jsonObject.getBoolean("inForWhenLoop");
        int forIndex = jsonObject.getInt("forIndex");
        Result pastResult = new Result(jsonObject.getBoolean("pastResult"));
        Return returnVal = convertJSONToReturn(jsonObject.getJSONObject("returnVal"));
        Variables globalVariables = convertJSONToVariables(jsonObject.getJSONObject("globalVariables"));
        Variables localVariables = convertJSONToVariables(jsonObject.getJSONObject("localVariables"));
        List<State> states = new ArrayList<>();
        JSONArray statesJSONArray = jsonObject.getJSONArray("states");
    
       
        List<JSONObject> statesJSON = new ArrayList<>();
        for (int i = 0; i < statesJSONArray.length(); i++) {
            statesJSON.add(statesJSONArray.getJSONObject(i));
        }
        for (JSONObject jsonObject2 : statesJSON) {
            states.add(convertJSONToState(jsonObject2));
        }
        if(jsonObject.getJSONArray("body").length() != 0){
            List<Statements> body = new ArrayList<>();
            JSONArray bodyJSOJsonArray = jsonObject.getJSONArray("body");
    
            List<JSONObject> bodyJSON = new ArrayList<>();
            for (int i = 0; i < bodyJSOJsonArray.length(); i++) {
                bodyJSON.add(bodyJSOJsonArray.getJSONObject(i));
            }
            for (JSONObject jsonObject2 : bodyJSON) {
                body.add(convertJSONToStatement(jsonObject2));
            }
            State state = new State(body, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete, currentRuntimeLine, isLoop, loopStartLine,forIndex, inForWhenLoop);
            state.setStates(states);
            return state;
        }
        State state = new State(null, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete, currentRuntimeLine, isLoop, loopStartLine,forIndex, inForWhenLoop);
        state.setStates(states);
        return state;
    }

    public static Return convertJSONToReturn(JSONObject jsonObject){
        boolean hasReturn = jsonObject.getBoolean("hasReturn");
        if(jsonObject.getString("value").equals("null")){
            return new Return(hasReturn, null);
        }
        JavaishVal value = convertJSONToJavaishVal(jsonObject.getJSONObject("value"));
        return new Return(hasReturn, value);
    }

    public static JavaishVal convertJSONToJavaishVal(JSONObject jsonObject){
        JavaishType type = JavaishType.valueOf(jsonObject.getString("type"));
        switch (type) {
            case FLOAT:
                return new JavaishFloat(Float.parseFloat(jsonObject.getString("value")));
            case INT:
                return new JavaishInt(Integer.parseInt(jsonObject.getString("value")));
            case STRING:
                return new JavaishString(jsonObject.getString("value"));
            case BOOLEAN:
                return new JavaishBoolean(Boolean.parseBoolean(jsonObject.getString("value")));
            default:
                break;
        }
        return null;
    }

    public static Variables convertJSONToVariables(JSONObject jsonObject){
        Variables variables = new Variables();
        for (String key : jsonObject.keySet()) {
            JSONObject variableJSON = jsonObject.getJSONObject(key);
            //Check if not function
            if(variableJSON.getString("type").equals("FUNCTION")){
                String name = variableJSON.getString("name");
                String argsString = variableJSON.getString("args");
                String[] args = argsString.split(", ");
                Argument[] arguments = new Argument[args.length];
                for (int i = 0; i < args.length; i++) {
                    System.out.println(args[i]);
                    if(args[i].equals("")){continue;}
                    String[] arg = args[i].split(" ");

                    JavaishType type = JavaishType.valueOf(arg[0]);
                    String argName = arg[1];
                    arguments[i] = new Argument(type, argName);
                }
                int lineNumber = variableJSON.getInt("line");
                List<Statements> body = new ArrayList<>();
                JSONArray bodyJSOJsonArray = variableJSON.getJSONArray("body");
       
                List<JSONObject> bodyJSON = new ArrayList<>();
                for (int i = 0; i < bodyJSOJsonArray.length(); i++) {
                    bodyJSON.add(bodyJSOJsonArray.getJSONObject(i));
                }

                for (JSONObject jsonObject2 : bodyJSON) {
                    body.add(convertJSONToStatement(jsonObject2));
                }
                variables.addFunction(name, body, arguments, lineNumber);
                continue;
            }
            JavaishType type = JavaishType.valueOf(variableJSON.getString("type"));
            String name = variableJSON.getString("name");
            JSONObject valueJSON = new JSONObject();
            valueJSON.put("type", type.toString());
            if(type.toString().equals("INTLIST") || type.toString().equals("FLOATLIST") || type.toString().equals("STRINGLIST") || type.toString().equals("BOOLEANLIST")){
                valueJSON.put("value", convertJSONToList(variableJSON.getJSONObject("value")));
                JavaishListVal value =  convertJSONToList(variableJSON.getJSONObject("value"));
                variables.addList(name, type, value.getValue(), 0);
                continue;
            }
            valueJSON.put("value", variableJSON.getString("value"));
            JavaishVal value =  convertJSONToJavaishVal(valueJSON);
            variables.addVariable(name, type, value, 0);
        }
        return variables;
    }

    public static JavaishListVal convertJSONToList(JSONObject jsonObject){
        JavaishType type = JavaishType.valueOf(jsonObject.getString("type"));
        JavaishType innerType = JavaishType.valueOf(jsonObject.getString("innerType"));
        int length = jsonObject.getInt("length");
        JSONArray valueJSONArray = jsonObject.getJSONArray("value");
        List<JavaishVal> list = new ArrayList<>();
        for (int i = 0; i < valueJSONArray.length(); i++) {
            switch (innerType) {
                case FLOAT:
                    list.add(new JavaishFloat(valueJSONArray.getFloat(i)));
                    break;
                case INT:
                    list.add(new JavaishInt(valueJSONArray.getInt(i)));
                    break;
                case STRING:
                    list.add(new JavaishString(valueJSONArray.getString(i)));
                    break;
                case BOOLEAN:
                    list.add(new JavaishBoolean(valueJSONArray.getBoolean(i)));
                    break;
                default:
                    break;
            }
        }
        JavaishListVal listVal = null;
        System.out.println(type + " - TYPE");
        switch (type) {
            case FLOATLIST:
                List<JavaishFloat> floatList = new ArrayList<>();
                for (JavaishVal javaishVal : list) {
                    floatList.add((JavaishFloat) javaishVal);
                }
                listVal = new JavaishListVal(new JavaishFloatList(floatList));
                break;
            case INTLIST:
                List<JavaishInt> intList = new ArrayList<>();
                for (JavaishVal javaishVal : list) {
                    intList.add((JavaishInt) javaishVal);
                }
                listVal = new JavaishListVal(new JavaishIntList(intList));
                break;
            case STRINGLIST:
                List<JavaishString> stringList = new ArrayList<>();
                for (JavaishVal javaishVal : list) {
                    stringList.add((JavaishString) javaishVal);
                }
                listVal = new JavaishListVal(new JavaishStringList(stringList));
                break;
            case BOOLEANLIST:
                List<JavaishBoolean> booleanList = new ArrayList<>();
                for (JavaishVal javaishVal : list) {
                    booleanList.add((JavaishBoolean) javaishVal);
                }
                listVal = new JavaishListVal(new JavaishBooleanList(booleanList));
                break;
            default:
                break;
        }
        System.out.println(listVal.getValue().toString() + " - LISTVAL");
        return listVal;
    }

    public static Statements convertJSONToStatement(JSONObject jsonObject){
        StmtType type = StmtType.valueOf(jsonObject.getString("type"));
        int line = jsonObject.getInt("line");
        switch (type) {
            case DECLARATION:
                String name = jsonObject.getString("name");
                JavaishType varType = JavaishType.valueOf(jsonObject.getString("varType"));
                JSONArray elementsJSONArray = jsonObject.getJSONArray("value");
                Element[] elements = new Element[elementsJSONArray.length()];
                for (int i = 0; i < elementsJSONArray.length(); i++) {
                    JSONObject element = elementsJSONArray.getJSONObject(i);
                    elements[i] = convertJSONToElement(element, line);
                }
                // String value = jsonObject.getString("value");
                // int column = jsonObject.getInt("column");
                ExpressionReturnType returnType = ExpressionReturnType.valueOf(jsonObject.getString("returnType"));
                return new DeclarationStmt(line,name, varType, new Expression(elements, returnType, line));
            case IF:
                JSONArray conditionElementsJSONArray = jsonObject.getJSONArray("condition");
                Element[] conditionElements = new Element[conditionElementsJSONArray.length()];
                for (int i = 0; i < conditionElementsJSONArray.length(); i++) {
                    JSONObject element = conditionElementsJSONArray.getJSONObject(i);
                    conditionElements[i] = convertJSONToElement(element, line);
                }
                Expression condition = new Expression(conditionElements, ExpressionReturnType.valueOf(jsonObject.getString("ifreturnType")),line );
                List<Statements> body = new ArrayList<>();
                JSONArray bodyJSOJsonArray = jsonObject.getJSONArray("states");
       
                List<JSONObject> bodyJSON = new ArrayList<>();
                for (int i = 0; i < bodyJSOJsonArray.length(); i++) {
                    bodyJSON.add(bodyJSOJsonArray.getJSONObject(i));
                }
                
                IfStmt newIfStmt = new IfStmt(line,condition);
                for (JSONObject jsonObject2 : bodyJSON) {
                    newIfStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                

                return newIfStmt;
            case ELSE:
                List<Statements> ebody = new ArrayList<>();
                JSONArray ebodyJSOJsonArray = jsonObject.getJSONArray("ebody");
       
                List<JSONObject> ebodyJSON = new ArrayList<>();
                for (int i = 0; i < ebodyJSOJsonArray.length(); i++) {
                    ebodyJSON.add(ebodyJSOJsonArray.getJSONObject(i));
                }
                ElseStmt newElseStmt = new ElseStmt(line);
                for (JSONObject jsonObject2 : ebodyJSON) {
                    newElseStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newElseStmt;
            case ELSEIF:
                JSONArray efconditionElementsJSONArray = jsonObject.getJSONArray("efcondition");
                Element[] efconditionElements = new Element[efconditionElementsJSONArray.length()];
                for (int i = 0; i < efconditionElementsJSONArray.length(); i++) {
                    JSONObject element = efconditionElementsJSONArray.getJSONObject(i);
                    efconditionElements[i] = convertJSONToElement(element, line);
                }
                Expression efcondition = new Expression(efconditionElements, ExpressionReturnType.valueOf(jsonObject.getString("efreturnType")),line);
                List<Statements> efbody = new ArrayList<>();
                JSONArray efbodyJSOJsonArray = jsonObject.getJSONArray("efbody");
       
                List<JSONObject> efbodyJSON = new ArrayList<>();
                for (int i = 0; i < efbodyJSOJsonArray.length(); i++) {
                    efbodyJSON.add(efbodyJSOJsonArray.getJSONObject(i));
                }
                ElseIfStmt newElseIfStmt = new ElseIfStmt(line,efcondition);
                for (JSONObject jsonObject2 : efbodyJSON) {
                    newElseIfStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newElseIfStmt;
            case END:
                return null;
            case RETURN:
                if(jsonObject.getString("returnExpr").equals("null")){
                    return new ReturnStmt(line, null,false);
                }
                JSONArray returnElementsJSONArray = jsonObject.getJSONArray("returnExpr");
                Element[] returnElements = new Element[returnElementsJSONArray.length()];
                for (int i = 0; i < returnElementsJSONArray.length(); i++) {
                    JSONObject element = returnElementsJSONArray.getJSONObject(i);
                    returnElements[i] = convertJSONToElement(element, line);
                }
                Expression returnExpr = new Expression(returnElements, ExpressionReturnType.valueOf(jsonObject.getString("returnReturnType")), line);
                return new ReturnStmt(line,returnExpr, true);
            case MUTATION:
                JSONArray mutationElementsJSONArray = jsonObject.getJSONArray("mutationExpr");
                Element[] mutationElements = new Element[mutationElementsJSONArray.length()];
                for (int i = 0; i < mutationElementsJSONArray.length(); i++) {
                    JSONObject element = mutationElementsJSONArray.getJSONObject(i);
                    mutationElements[i] = convertJSONToElement(element, line);
                }
                Expression mutationExpr = new Expression(mutationElements, ExpressionReturnType.valueOf(jsonObject.getString("mutationReturnType")), line);
                String mutationType = jsonObject.getString("mutationType");
                String mutationName = jsonObject.getString("mutationVar");
                return new MutationStmt(line,mutationName, mutationExpr, MutationType.valueOf(mutationType));
            case FUNCTION:
                String fname = jsonObject.getString("name");
                String argsString = jsonObject.getString("args");
                String[] args = argsString.split(", ");
                Argument[] arguments = new Argument[args.length];
                for (int i = 0; i < args.length; i++) {
                    String[] arg = args[i].split(" ");
                    JavaishType type2 = JavaishType.valueOf(arg[0]);
                    String argName = arg[1];
                    arguments[i] = new Argument(type2, argName);
                }
                List<Statements> fbody = new ArrayList<>();
                JSONArray fbodyJSOJsonArray = jsonObject.getJSONArray("fbody");
       
                List<JSONObject> fbodyJSON = new ArrayList<>();
                for (int i = 0; i < fbodyJSOJsonArray.length(); i++) {
                    fbodyJSON.add(fbodyJSOJsonArray.getJSONObject(i));
                }
                FunctionStmt newFunctionStmt = new FunctionStmt(line,fname, arguments);
                for (JSONObject jsonObject2 : fbodyJSON) {
                    newFunctionStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newFunctionStmt;
            case CALL:
                String cname = jsonObject.getString("name");
                JSONArray paramsString = jsonObject.getJSONArray("params");
                Expression[] expressions = new Expression[paramsString.length()];
                for (int i = 0; i < paramsString.length(); i++) {
                    JSONObject param = paramsString.getJSONObject(i);
                    JSONArray paramElementsJSONArray = param.getJSONArray("param");
                    Element[] paramElements = new Element[paramElementsJSONArray.length()];
                    for (int j = 0; j < paramElementsJSONArray.length(); j++) {
                        JSONObject element = paramElementsJSONArray.getJSONObject(j);
                        paramElements[j] = convertJSONToElement(element, line);
                    }
                    expressions[i] = new Expression(paramElements, ExpressionReturnType.valueOf(param.getString("paramReturnType")), line);
                }
                return new CallStmt(line, cname, expressions);
            case ASSIGNMENT:
                String aname = jsonObject.getString("name");
                JSONArray assignmentElementsJSONArray = jsonObject.getJSONArray("assignmentExpr");
                Element[] assignmentElements = new Element[assignmentElementsJSONArray.length()];
                for (int i = 0; i < assignmentElementsJSONArray.length(); i++) {
                    JSONObject element = assignmentElementsJSONArray.getJSONObject(i);
                    assignmentElements[i] = convertJSONToElement(element, line);
                }
                Expression assignmentExpr = new Expression(assignmentElements, ExpressionReturnType.valueOf(jsonObject.getString("assignmentReturnType")), line);
                return new AssignmentStmt(line,aname, assignmentExpr);
            case FOREACH:
                String tempVar = jsonObject.getString("tempVar");
                String listVar = jsonObject.getString("listVar");
                List<Statements> febody = new ArrayList<>();
                JSONArray febodyJSOJsonArray = jsonObject.getJSONArray("febody");
       
                List<JSONObject> febodyJSON = new ArrayList<>();
                for (int i = 0; i < febodyJSOJsonArray.length(); i++) {
                    febodyJSON.add(febodyJSOJsonArray.getJSONObject(i));
                }
                ForEachStmt newForEachStmt = new ForEachStmt(line,tempVar, listVar);
                for (JSONObject jsonObject2 : febodyJSON) {
                    newForEachStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newForEachStmt;
            case FORWHEN:
                JSONArray forwhenConditionElementsJSONArray = jsonObject.getJSONArray("condition");
                Element[] forwhenConditionElements = new Element[forwhenConditionElementsJSONArray.length()];
                for (int i = 0; i < forwhenConditionElementsJSONArray.length(); i++) {
                    JSONObject element = forwhenConditionElementsJSONArray.getJSONObject(i);
                    forwhenConditionElements[i] = convertJSONToElement(element, line);
                }
                Expression forwhenCondition = new Expression(forwhenConditionElements, ExpressionReturnType.valueOf(jsonObject.getString("forwhenReturnType")), line);
                String incVar = jsonObject.getString("incVar");
                JSONArray forwhenIncElementsJSONArray = jsonObject.getJSONArray("increment");
                Element[] forwhenIncElements = new Element[forwhenIncElementsJSONArray.length()];
                for (int i = 0; i < forwhenIncElementsJSONArray.length(); i++) {
                    JSONObject element = forwhenIncElementsJSONArray.getJSONObject(i);
                    forwhenIncElements[i] = convertJSONToElement(element, line);
                }
                Expression increment = new Expression(forwhenIncElements, ExpressionReturnType.valueOf(jsonObject.getString("forwhenIncReturnType")), line);
                List<Statements> fwbody = new ArrayList<>();
                JSONArray fwbodyJSOJsonArray = jsonObject.getJSONArray("fwbody");
       
                List<JSONObject> fwbodyJSON = new ArrayList<>();
                for (int i = 0; i < fwbodyJSOJsonArray.length(); i++) {
                    fwbodyJSON.add(fwbodyJSOJsonArray.getJSONObject(i));
                }
                ForWhenStmt newForWhenStmt = new ForWhenStmt(line,forwhenCondition, increment, incVar);
                for (JSONObject jsonObject2 : fwbodyJSON) {
                    newForWhenStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newForWhenStmt;
            case SHOWMSGBOX:
                JSONArray showMsgBoxStmtElementsJSONArray = jsonObject.getJSONArray("showMsgBoxStmt");
                Element[] showMsgBoxStmtElements = new Element[showMsgBoxStmtElementsJSONArray.length()];
                for (int i = 0; i < showMsgBoxStmtElementsJSONArray.length(); i++) {
                    JSONObject element = showMsgBoxStmtElementsJSONArray.getJSONObject(i);
                    showMsgBoxStmtElements[i] = convertJSONToElement(element, line);
                }
                Expression showMsgBoxExpr = new Expression(showMsgBoxStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("showMsgBoxReturnType")), line);
                return new ShowMsgBoxStmt(line, showMsgBoxExpr);
            case PRINT:
                JSONArray printStmtElementsJSONArray = jsonObject.getJSONArray("printStmt");
                Element[] printStmtElements = new Element[printStmtElementsJSONArray.length()];
                for (int i = 0; i < printStmtElementsJSONArray.length(); i++) {
                    JSONObject element = printStmtElementsJSONArray.getJSONObject(i);
                    // System.out.println(element.toString() + " PRINT ELEMENT");
                    printStmtElements[i] = convertJSONToElement(element, line);
                    
                }
                Expression printExpr = new Expression(printStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("printStmtReturnType")), line);
                return new PrintStmt(line, printExpr);
            case WHILE:
                JSONArray whileConditionElementsJSONArray = jsonObject.getJSONArray("whilecondition");
                Element[] whileConditionElements = new Element[whileConditionElementsJSONArray.length()];
                for (int i = 0; i < whileConditionElementsJSONArray.length(); i++) {
                    JSONObject element = whileConditionElementsJSONArray.getJSONObject(i);
                    whileConditionElements[i] = convertJSONToElement(element, line);
                }
                Expression whileCondition = new Expression(whileConditionElements, ExpressionReturnType.valueOf(jsonObject.getString("whilereturnType")), line);
                List<Statements> whilebody = new ArrayList<>();
                JSONArray whilebodyJSOJsonArray = jsonObject.getJSONArray("whilebody");
       
                List<JSONObject> whilebodyJSON = new ArrayList<>();
                for (int i = 0; i < whilebodyJSOJsonArray.length(); i++) {
                    whilebodyJSON.add(whilebodyJSOJsonArray.getJSONObject(i));
                }
                WhileStmt newWhileStmt = new WhileStmt(line,whileCondition);
                for (JSONObject jsonObject2 : whilebodyJSON) {
                    newWhileStmt.addStatement(convertJSONToStatement(jsonObject2));
                }
                return newWhileStmt;
            case REMOVEALLFROM:
                JSONArray removeAllFromStmtElementsJSONArray = jsonObject.getJSONArray("removeAllFromStmt");
                Element[] removeAllFromStmtElements = new Element[removeAllFromStmtElementsJSONArray.length()];
                for (int i = 0; i < removeAllFromStmtElementsJSONArray.length(); i++) {
                    JSONObject element = removeAllFromStmtElementsJSONArray.getJSONObject(i);
                    removeAllFromStmtElements[i] = convertJSONToElement(element, line);
                }
                Expression removeAllFromExpr = new Expression(removeAllFromStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("removeAllFromStmtReturnType")), line);
                String removeAllFromListName = jsonObject.getString("removeAllFromStmtListName");
                return new RemoveAllFromStmt(line, removeAllFromListName, removeAllFromExpr);
            case REMOVEFROM:
                JSONArray removeFromStmtElementsJSONArray = jsonObject.getJSONArray("removeFromStmt");
                Element[] removeFromStmtElements = new Element[removeFromStmtElementsJSONArray.length()];
                for (int i = 0; i < removeFromStmtElementsJSONArray.length(); i++) {
                    JSONObject element = removeFromStmtElementsJSONArray.getJSONObject(i);
                    removeFromStmtElements[i] = convertJSONToElement(element, line);
                }
                Expression removeFromExpr = new Expression(removeFromStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("removeFromStmtReturnType")), line);
                String removeFromListName = jsonObject.getString("removeFromStmtListName");
                return new RemoveFromStmt(line, removeFromExpr, removeFromListName);
            case REMOVEAT:
                JSONArray removeAtStmtElementsJSONArray = jsonObject.getJSONArray("removeAtStmt");
                Element[] removeAtStmtElements = new Element[removeAtStmtElementsJSONArray.length()];
                for (int i = 0; i < removeAtStmtElementsJSONArray.length(); i++) {
                    JSONObject element = removeAtStmtElementsJSONArray.getJSONObject(i);
                    removeAtStmtElements[i] = convertJSONToElement(element, line);
                }
                Expression removeAtExpr = new Expression(removeAtStmtElements ,ExpressionReturnType.valueOf(jsonObject.getString("removeAtStmtReturnType")), line);
                String removeAtListName = jsonObject.getString("removeAtStmtListName");
                return new RemoveAtStmt(line, removeAtExpr, removeAtListName);
            default:
                break;
        }
        return null;
    }

    public static Element convertJSONToElement(JSONObject jsonObject, int line){
        Element element = new Element();
        ElementType elementType = ElementType.valueOf(jsonObject.getString("elementType"));
            switch (elementType) {
                case EXPRESSION:
                    JSONArray expressionStmtElementsJSONArray = jsonObject.getJSONArray("element");
                    Element[] expressionFromStmtElements = new Element[expressionStmtElementsJSONArray.length()];
                    for (int i = 0; i < expressionStmtElementsJSONArray.length(); i++) {
                        JSONObject expElmt = expressionStmtElementsJSONArray.getJSONObject(i);
                        expressionFromStmtElements[i] = convertJSONToElement(expElmt, line);
                    }
                    element = new ExpressionElmt(new Expression(expressionFromStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("elementReturnType")), line));
                    break;
                case STRING:
                    element = new StringElmt(jsonObject.getString("element"));
                    break;
                case VARIABLE:
                    element = new VariableElmt(jsonObject.getString("element"));
                    break;
                case LIST:
                    JSONArray listJSONArray = jsonObject.getJSONArray("element");
                    List<Expression> list = new ArrayList<>();
                    for (int i = 0; i < listJSONArray.length(); i++) {
                        JSONArray listElementJSONArray = listJSONArray.getJSONArray(i);
                        Element[] listElement = new Element[listElementJSONArray.length()];
                        for (int j = 0; j < listElementJSONArray.length(); j++) {
                            JSONObject element2 = listElementJSONArray.getJSONObject(j);
                            listElement[j] = convertJSONToElement(element2, line);
                        }
                        list.add(new Expression(listElement, ExpressionReturnType.valueOf(jsonObject.getString("elementListType")), line));
                    }
                    element = new ListElmt(list, JavaishType.valueOf(jsonObject.getString("elementListType")));
                    break;
                case FUNCTION:
                    JSONArray paramsString = jsonObject.getJSONArray("params");
                    Expression[] expressions = new Expression[paramsString.length()];
                    for (int i = 0; i < paramsString.length(); i++) {
                        JSONObject param = paramsString.getJSONObject(i);
                        Element[] elementsCall = new Element[param.length()];
                        String paramReturnType = param.getString("paramReturnType");
                        JSONArray paramElementsJSONArray = param.getJSONArray("param");

                        for (int j = 0; j < paramElementsJSONArray.length(); j++) {
                            JSONObject element2 = paramElementsJSONArray.getJSONObject(j);
                            elementsCall[j] = convertJSONToElement(element2, line);
                           
                        }
                        expressions[i] = new Expression(elementsCall, ExpressionReturnType.valueOf(paramReturnType), line);
                    }
                    element = new FunctionElmt(jsonObject.getString("name"), expressions);
                    break;
                case AND:
                    element = new AndElmt();
                    break;
                case OR:
                    element = new OrElmt();
                    break;
                case NOT:
                    JSONArray notStmtElementsJSONArray = jsonObject.getJSONArray("element");
                    Element[] notElements = new Element[notStmtElementsJSONArray.length()];
                    for (int i = 0; i < notStmtElementsJSONArray.length(); i++) {
                        JSONObject notElmt = notStmtElementsJSONArray.getJSONObject(i);
                        notElements[i] = convertJSONToElement(notElmt, line);
                    }
                    Expression notExpr = new Expression(notElements, ExpressionReturnType.valueOf(jsonObject.getString("elementReturnType")), line);
                    element = new NotElmt(notExpr);
                    break;
                case PLUS:
                    element = new PlusElmt();
                    break;
                case MINUS:
                    element = new MinusElmt();
                    break;
                case MULTIPLY:
                    element = new MultiplyElmt();
                    break;
                case DIVIDE:
                    element = new DivideElmt();
                    break;
                case FLOAT:
                    element = new FloatElmt(Float.parseFloat(jsonObject.getString("element")));
                    break;
                case INTEGER:
                    element = new IntElmt(Integer.parseInt(jsonObject.getString("element")));
                    break;
                case BOOL:
                    element = new BoolElmt(Boolean.parseBoolean(jsonObject.getString("element")));
                    break;
                case ARRAYLENGTH:
                    element = new ArrayLengthElmt(jsonObject.getString("element"));
                    break;
                case SHOWINPUTBOX:
                    JSONArray showInputBoxStmtElementsJSONArray = jsonObject.getJSONArray("element");
                    Element[] showInputBoxStmtElements = new Element[showInputBoxStmtElementsJSONArray.length()];
                    for (int i = 0; i < showInputBoxStmtElementsJSONArray.length(); i++) {
                        JSONObject showInputBoxElmt = showInputBoxStmtElementsJSONArray.getJSONObject(i);
                        showInputBoxStmtElements[i] = convertJSONToElement(showInputBoxElmt, line);
                    }
                    Expression showInputBoxExpr = new Expression(showInputBoxStmtElements ,ExpressionReturnType.valueOf(jsonObject.getString("elementReturnType")), line);
                    element = new ShowInputBoxElmt(showInputBoxExpr);
                    break;
                case EQUAL:
                    element = new EqualElmt();
                    break;
                case NOT_EQUAL:
                    element = new NotEqualElmt();
                    break;
                case GREATER_THAN:
                    element = new GreaterThanElmt();
                    break;
                case LESS_THAN:
                    element = new LessThanElmt();
                    break;
                case GREATER_THAN_EQUAL:
                    element = new GreaterThanEqualElmt();
                    break;
                case LESS_THAN_EQUAL:
                    element = new LessThanEqualElmt();
                    break;
                case CAST:
                    JSONArray castStmtElementsJSONArray = jsonObject.getJSONArray("element");
                    Element[] castElements = new Element[castStmtElementsJSONArray.length()];
                    for (int i = 0; i < castStmtElementsJSONArray.length(); i++) {
                        JSONObject castElmt = castStmtElementsJSONArray.getJSONObject(i);
                        castElements[i] = convertJSONToElement(castElmt, line);
                    }
                    Expression castExpr = new Expression(castElements, ExpressionReturnType.valueOf(jsonObject.getString("elementReturnType")), line);
                    JavaishType castType = JavaishType.valueOf(jsonObject.getString("castType"));
                    element = new CastElmt( castType,castExpr);
                    break;
                case LISTVAL:
                    JSONArray listValStmtElementsJSONArray = jsonObject.getJSONArray("element");
                    Element[] listValStmtElements = new Element[listValStmtElementsJSONArray.length()];
                    for (int i = 0; i < listValStmtElementsJSONArray.length(); i++) {
                        JSONObject listValElmt = listValStmtElementsJSONArray.getJSONObject(i);
                        listValStmtElements[i] = convertJSONToElement(listValElmt, line);
                    }
                    Expression listValExpr = new Expression(listValStmtElements, ExpressionReturnType.valueOf(jsonObject.getString("elementReturnType")), line);
                    String listValName = jsonObject.getString("elementListName");
                    element = new ListValElmt(listValName, listValExpr);
                    break;
                default:
                    break;
        }
        return element;
    }

    // public static State convertStringToState(String stringState){
    //     String[] stateArray = stringState.split("\\*\\*\\*");
    //     for (String string : stateArray) {
    //         System.out.println(string);
    //     }
    //     boolean isComplete = Boolean.parseBoolean(stateArray[0]);
    //     boolean isGlobal = Boolean.parseBoolean(stateArray[1]);
    //     int currentLine = Integer.parseInt(stateArray[2]);
    //     Return returnVal = convertStringToReturn(stateArray[3]);
    //     Result pastResult = new Result(stateArray[4] == "true");
    //     if(stateArray.length == 5){
    //         Variables variables = new Variables();
    //         List<Statements> statements = new ArrayList<>();
    //         return new State(statements, variables, variables, pastResult, returnVal, currentLine, isGlobal, isComplete);
    //     }
    //     Variables globalVariables = new Variables();
    //     if(!stateArray[5].equals("")){
    //         globalVariables = convertStringToVariables(stateArray[5]);
    //     }
    //     Variables localVariables = new Variables();
    //     if(!stateArray[6].equals("")){
    //         localVariables = convertStringToVariables(stateArray[6]);
    //     }
    //     List<Statements> statements = new ArrayList<>();


    //     if(stateArray.length > 7){
    //         List<State> states = new ArrayList<>();
    //         String newStateString = "";
    //         int index = 7;
    //         while( stateArray[index].equals("---") == false && index < stateArray.length) {
    //             newStateString += stateArray[index] + "***";
    //             index++;
    //         }
            
            
    //         System.out.println(newStateString + " - State String");
    //         states.add(convertStringToState(newStateString));
            
    //         return new State(statements, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete, states);
    //     }
        
    //     return new State(statements, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete);
    // }



    public static Return convertStringToReturn(String stringReturn){
        System.out.println(stringReturn);
        if(stringReturn.equals("null")){
            return new Return(false, null);
        }
        String[] returnArray = stringReturn.split(",");
        System.out.println(returnArray);
        JavaishVal value = null;
        if(returnArray[1] == "int"){
            value = new JavaishInt(Integer.parseInt(returnArray[0]));
        } else if(returnArray[1] == "float"){
            value = new JavaishFloat(Float.parseFloat(returnArray[0]));
        } else if(returnArray[1] == "String"){
            value = new JavaishString(returnArray[0]);
        } else if(returnArray[1] == "boolean"){
            value = new JavaishBoolean(Boolean.parseBoolean(returnArray[0]));
        }
        return new Return(returnArray[0] == "true", value);
        
    }

}

