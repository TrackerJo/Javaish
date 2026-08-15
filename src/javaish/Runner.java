
package javaish;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javaish.Expression.ExpressionReturnType;
import javaish.JavaishVal.JavaishType;
import javaish.Statements.StmtType;

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

    public static void convertFile(String path, String projName) throws IOException {
          byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
       // printStmts(statements.getBody(),0);
        Translator translator = new Translator(variables, projName);
        translator.interpretFunction(statements.getBody(), null, null, "$main", true, true);
        List<String> lines = translator.getJavaLines();
        printJavaLines(lines);
        //Create java file
        String javaFile = "";
        for (String line : lines) {
            javaFile += line + "\n";
        }
        Files.write(Paths.get("src/" + projName + ".java"), javaFile.getBytes());
    }

    public static State debugFile(String path, State oldState) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
        printStmts(statements.getBody(),0);
        Result pastResult = new Result(false);  
       
        Return returnVal = new Return(false, null);
        if(oldState == null){
            oldState = new State(statements.getBody(), variables, variables, pastResult, returnVal, 0, true, false);
        } else {
            oldState.setStatements(statements.getBody());
        }
        Debugger debugger = new Debugger();
        System.out.println(oldState.getCurrentLine() + " - CURRENT LINE");
        oldState.setCurrentLine(oldState.getCurrentLine() - 1);
        State newState = debugger.debugLine(oldState.getCurrentLine(), oldState);
        // State newState = debugLines(debugger, oldState, statements.getBody().size());
       // printVars(state.getGlobalVariables());
        return newState;
    }

    public static State debugString(String string){
        Variables variables = new Variables();
        Parser parser = new Parser(string, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
        printStmts(statements.getBody(),0);
        Result pastResult = new Result(false);  
       
        Return returnVal = new Return(false, null);
        State state = new State(statements.getBody(), variables, variables, pastResult, returnVal, 0, true, false);
        Debugger debugger = new Debugger();
        state = debugLines(debugger, state, statements.getBody().size());
       // printVars(state.getGlobalVariables());
        return state;
    }

    private static State debugLines(Debugger debugger, State state, int maxLines){
        int lineNumber = 0;
        while(!state.isComplete){
            state = debugger.debugLine(lineNumber, state);
            lineNumber = state.getCurrentLine();
            System.out.println(lineNumber + " - Line Number");
            System.out.println(convertStateToString(state));
            System.out.println(convertStateToString(convertStringToState(convertStateToString(state))));
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

    public static String convertStateToString(State state){
        String stateString = "";
        stateString += state.isComplete() + "***";

        stateString += state.isGlobal() + "***";

        stateString += state.getCurrentLine() + "***";

        //Convert ReturnVal to String
        if(state.getReturnVal().getValue() == null){
            stateString += "null***";
        } else {
            stateString += state.getReturnVal().getValue().getValue().toString() + "," + state.getReturnVal().getValue().typeString() + "," + state.getReturnVal().hasReturn() +  "***";

        }

        //Convert PastResult to String
        stateString += state.getPastResult().getResult() + "***";
        // List<Statements> statements = state.getStatements();
        // for (Statements statement : statements) {
        //     stateString += convertStatementToString(statement) + "---";
        // }
        //Convert GlobalVariables to String
        stateString += convertVariablesToString(state.getGlobalVariables());
        stateString += convertVariablesToString(state.getLocalVariables());
        List<State> states = state.getStates();
        System.out.println(states.size());
        for (State state2 : states) {
            stateString += convertStateToString(state2) + "---";
        }


        return stateString;
        
    }

    public static String convertStatementToString(Statements statements){
        String statementString = "";
        statementString += statements.getType() + ":::";
        statementString += statements.getLine() + ":::";
        switch (statements.getType()) {
            case DECLARATION:
                DeclarationStmt declaration = (DeclarationStmt) statements;
                String name = declaration.getName();
                JavaishType varType = declaration.getVarType();
                String value = declaration.getValue().toString();
                int column = declaration.getValue().getColumn();
                ExpressionReturnType returnType = declaration.getValue().getReturnType();
                statementString += name + "," + varType + "," + value + "," + column + "," + returnType;
                break;
            case IF:
                IfStmt ifStmt = (IfStmt) statements;
                Expression condition = ifStmt.getCondition();
                int ifcolumn = condition.getColumn();
                ExpressionReturnType ifreturnType = condition.getReturnType();
                List<Statements> body = ifStmt.getBody();
                String bString = "|||";
                for (Statements statements2 : body) {
                    bString += convertStatementToString(statements2) + "|||";
                }
                statementString += condition.toString() + "," + ifcolumn + "," + ifreturnType + "," + body.size() + ", (" +  bString + ") ";
                break;
            case ELSE:
                ElseStmt elseStmt = (ElseStmt) statements;
                List<Statements> ebody = elseStmt.getBody();
                String ebString = "|||";
                for (Statements statements2 : ebody) {
                    ebString += convertStatementToString(statements2) + "|||";
                }
                statementString += ebody.size() + ", (" +  ebString + ") ";
                break;
            case ELSEIF:
                ElseIfStmt elseIfStmt = (ElseIfStmt) statements;
                Expression efcondition = elseIfStmt.getCondition();
                int efcolumn = efcondition.getColumn();
                ExpressionReturnType efreturnType = efcondition.getReturnType();
                List<Statements> efbody = elseIfStmt.getBody();
                String efbString = "|||";
                for (Statements statements2 : efbody) {
                    efbString += convertStatementToString(statements2) + "|||";
                }
                statementString += efcondition.toString() + "," + efcolumn + "," + efreturnType.toString() + "," + efbody.size() + ", (" +  efbString + ") ";
                break;
            case END:
                statementString += "END";
                break;
            case RETURN:
                ReturnStmt returnStmt = (ReturnStmt) statements;
                Expression returnExpr = returnStmt.getValue();
                int returnColumn = returnExpr.getColumn();
                ExpressionReturnType returnReturnType = returnExpr.getReturnType();
                statementString += returnStmt.getValue().toString() + "," + returnColumn + "," + returnReturnType.toString();
                break;
            case MUTATION:
                MutationStmt mutationStmt = (MutationStmt) statements;
                Expression mutationExpr = mutationStmt.getValue();
                int mutationColumn = mutationExpr.getColumn();
                ExpressionReturnType mutationReturnType = mutationExpr.getReturnType();
                statementString += mutationStmt.getVarName() + "," + mutationStmt.getMutationType() + "," + mutationStmt.getValue().toString() + "," + mutationColumn + "," + mutationReturnType.toString();
                break;
            case FUNCTION:
                FunctionStmt functionStmt = (FunctionStmt) statements;
                List<Statements> fbody = functionStmt.getBody();
                String fbString = "|||";
                for (Statements statements2 : fbody) {
                    fbString += convertStatementToString(statements2) + "|||";
                }
                statementString += functionStmt.getName() + "," + getArgsString(functionStmt.getArgs()) + ","  + fbody.size() + ", (" +  fbString + ") ";
                break;
            case CALL:
                CallStmt callStmt = (CallStmt) statements;
                statementString += callStmt.getName() + "," + getParamString(callStmt.getParams());
                break;
            case ASSIGNMENT:
                AssignmentStmt assignmentStmt = (AssignmentStmt) statements;
                Expression assignmentExpr = assignmentStmt.getValue();
                int assignmentColumn = assignmentExpr.getColumn();
                ExpressionReturnType assignmentReturnType = assignmentExpr.getReturnType();
                statementString += assignmentStmt.getName() + "," + assignmentStmt.getValue().toString() + "," + assignmentColumn + "," + assignmentReturnType.toString();
                break;
            case FOREACH:
                ForEachStmt foreachStmt = (ForEachStmt) statements;
                List<Statements> febody = foreachStmt.getBody();
                String febString = "|||";
                for (Statements statements2 : febody) {
                    febString += convertStatementToString(statements2) + "|||";
                }
                statementString += foreachStmt.getTempVar() + "," + foreachStmt.getListVar() + "," + febody.size()+ ", (" +  febString + ") " ;
                break;
            case FORWHEN:
                ForWhenStmt forwhenStmt = (ForWhenStmt) statements;
                int forwhenColumn = forwhenStmt.getCondition().getColumn();
                ExpressionReturnType forwhenReturnType = forwhenStmt.getCondition().getReturnType();
                List<Statements> fwbody = forwhenStmt.getBody();
                String fwbString = "|||";
                for (Statements statements2 : fwbody) {
                    fwbString += convertStatementToString(statements2) + "|||";
                }
                statementString += forwhenStmt.getCondition() + "," + forwhenStmt.getIncVar() + "," + forwhenStmt.getIncrement() + "," + forwhenColumn + "," + forwhenReturnType.toString() + "," + fwbody.size() + ", (" +  fwbString + ") ";
                break;
            case SHOWMSGBOX:
                ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) statements;
                int showMsgBoxColumn = showMsgBoxStmt.getValue().getColumn();
                ExpressionReturnType showMsgBoxReturnType = showMsgBoxStmt.getValue().getReturnType();
                statementString += showMsgBoxStmt.getValue().toString() + "," + showMsgBoxColumn + "," + showMsgBoxReturnType.toString();
                break;
            case PRINT:
                PrintStmt printStmt = (PrintStmt) statements;
                int printColumn = printStmt.getValue().getColumn();
                ExpressionReturnType printReturnType = printStmt.getValue().getReturnType();
                statementString += printStmt.getValue().toString() + "," + printColumn + "," + printReturnType.toString();
                break;
            case WHILE:
                WhileStmt whileStmt = (WhileStmt) statements;
                int whileColumn = whileStmt.getCondition().getColumn();
                ExpressionReturnType whileReturnType = whileStmt.getCondition().getReturnType();
                List<Statements> wbody = whileStmt.getBody();
                String wbString = "|||";
                for (Statements statements2 :wbody) {
                    wbString += convertStatementToString(statements2) + "|||";
                }
                statementString += whileStmt.getCondition() + "," + whileColumn + "," + whileReturnType.toString() + wbody.size() + ", (" +  wbString + ") " ;
                break;
            case REMOVEALLFROM:
                RemoveAllFromStmt removeAllFromStmt = (RemoveAllFromStmt) statements;
                int removeAllFromColumn = removeAllFromStmt.getValue().getColumn();
                ExpressionReturnType removeAllFromReturnType = removeAllFromStmt.getValue().getReturnType();
                statementString += removeAllFromStmt.getValue() + "," + removeAllFromStmt.getListName() + "," + removeAllFromColumn + "," + removeAllFromReturnType.toString();
                break;
            case REMOVEFROM:
                RemoveFromStmt removeFromStmt = (RemoveFromStmt) statements;
                int removeFromColumn = removeFromStmt.getValue().getColumn();
                ExpressionReturnType removeFromReturnType = removeFromStmt.getValue().getReturnType();
                statementString += removeFromStmt.getValue() + "," + removeFromStmt.getListName() + "," + removeFromColumn + "," + removeFromReturnType.toString();
                break;
            case REMOVEAT:
                RemoveAtStmt removeAtStmt = (RemoveAtStmt) statements;
                int removeAtColumn = removeAtStmt.getLocation().getColumn();
                ExpressionReturnType removeAtReturnType = removeAtStmt.getLocation().getReturnType();

                statementString += removeAtStmt.getLocation() + "," + removeAtStmt.getListName() + "," + removeAtColumn + "," + removeAtReturnType.toString();
                break;
            default:
                break;
        }
        return statementString;
    }

    public static String convertVariablesToString(Variables variables){
        String variablesString = "";
        Map<String, JavaishType> variablesMap = variables.getAllVariables();

        
        for (Entry<String, JavaishType> entry : variablesMap.entrySet()) {
            //Get Value
            JavaishVal value = variables.getVariableValue(entry.getKey());
            variablesString += entry.getKey() + "," + ((JavaishType) entry.getValue()) + "," + value.getValue().toString() + "___";
        }
        variablesString += "===";
        Map<String, FunctionVar> functionsMap = variables.getFunctions();
        for (Entry<String, FunctionVar> entry : functionsMap.entrySet()) {
            //Get Value
            FunctionVar value = entry.getValue();
            List<Statements> body = value.getBody();
            String bString = "";
            for (Statements statements2 : body) {
                bString += convertStatementToString(statements2) + "|||";
            }
            String argsString = "";
            for (Argument arg : value.getArgs()) {
                argsString += arg.getType() + " " + arg.getName() + "?";
            }
            variablesString += entry.getKey() + "$$$" + argsString + "$$$" + body.size() + "$$$" + bString + "___";
        }

        variablesString += "***";
        return variablesString;
    }

    public static State convertStringToState(String stringState){
        String[] stateArray = stringState.split("\\*\\*\\*");
        for (String string : stateArray) {
            System.out.println(string);
        }
        boolean isComplete = Boolean.parseBoolean(stateArray[0]);
        boolean isGlobal = Boolean.parseBoolean(stateArray[1]);
        int currentLine = Integer.parseInt(stateArray[2]);
        Return returnVal = convertStringToReturn(stateArray[3]);
        Result pastResult = new Result(stateArray[4] == "true");
        if(stateArray.length == 5){
            Variables variables = new Variables();
            List<Statements> statements = new ArrayList<>();
            return new State(statements, variables, variables, pastResult, returnVal, currentLine, isGlobal, isComplete);
        }
        Variables globalVariables = new Variables();
        if(!stateArray[5].equals("")){
            globalVariables = convertStringToVariables(stateArray[5]);
        }
        Variables localVariables = new Variables();
        if(!stateArray[6].equals("")){
            localVariables = convertStringToVariables(stateArray[6]);
        }
        List<Statements> statements = new ArrayList<>();


        if(stateArray.length > 7){
            List<State> states = new ArrayList<>();
            String newStateString = "";
            int index = 7;
            while( stateArray[index].equals("---") == false && index < stateArray.length) {
                newStateString += stateArray[index] + "***";
                index++;
            }
            
            
            System.out.println(newStateString + " - State String");
            states.add(convertStringToState(newStateString));
            
            return new State(statements, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete, states);
        }
        
        return new State(statements, globalVariables, localVariables, pastResult, returnVal, currentLine, isGlobal, isComplete);
    }

    public static Variables convertStringToVariables(String stringVariables){
        Variables variables = new Variables();
        String[] splitArray = stringVariables.split("===");
        System.out.println(splitArray.length);
        if(!splitArray[0].equals("")){
            
        
        String[] variablesArray = splitArray[0].split("___");
            for (String string : variablesArray) {
                if(string == "***"){
                    continue;
                }
                String[] variableArray = string.split(",");
                JavaishType type = JavaishType.valueOf(variableArray[1]);
                JavaishVal value = null;
                if(type == JavaishType.INT){
                    value = new JavaishInt(Integer.parseInt(variableArray[2]));
                } else if(type == JavaishType.FLOAT){
                    value = new JavaishFloat(Float.parseFloat(variableArray[2]));
                } else if(type == JavaishType.STRING){
                    value = new JavaishString(variableArray[2]);
                } else if(type == JavaishType.BOOLEAN){
                    value = new JavaishBoolean(Boolean.parseBoolean(variableArray[2]));
                }
                variables.addVariable(variableArray[0], JavaishType.valueOf(variableArray[1]), value, 0);
            }
        }
        if(!splitArray[1].equals("")){
           
        
            String[] functionsArray = splitArray[1].split("___");
            for (String string : functionsArray) {
                System.out.println(string);
                if(string == "***"){
                    continue;
                }
                String[] functionArray = string.split("$$$");
                String name = functionArray[0];
                String[] argsArray = functionArray[1].split("\\?");
                Argument[] args = new Argument[argsArray.length];
                for (int i = 0; i < argsArray.length; i++) {
                    String[] argArray = argsArray[i].split(" ");
                    args[i] = new Argument(JavaishType.valueOf(argArray[0]), argArray[1]);
                }
                List<Statements> body = new ArrayList<>();
                if(!functionArray[3].equals("")){
                    String[] bodyArray = functionArray[3].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        body.add(convertStringToStatement(string2));
                    }
                }
                FunctionVar functionVar = new FunctionVar(name, , args);
                variables.addFunction(name, functionVar);
            }
        }
        return variables;
    }

    public static Statements convertStringToStatement(String stringStatement){
        String[] statementArray = stringStatement.split(":::");
        StmtType type = StmtType.valueOf(statementArray[0]);
        int line = Integer.parseInt(statementArray[1]);
        switch (type) {
            case DECLARATION:
                String[] declarationArray = statementArray[2].split(",");
                String name = declarationArray[0];
                JavaishType varType = JavaishType.valueOf(declarationArray[1]);
                String value = declarationArray[2];
                int column = Integer.parseInt(declarationArray[3]);
                ExpressionReturnType returnType = ExpressionReturnType.valueOf(declarationArray[4]);
                return new DeclarationStmt(line, name, varType, new Expression(value, returnType, line, column ));
            case IF:
                String[] ifArray = statementArray[2].split(",");
                Expression condition = new Expression(ifArray[0], ExpressionReturnType.valueOf(ifArray[2]), line ,Integer.parseInt(ifArray[1]));

                List<Statements> body = new ArrayList<>();
                if(!ifArray[5].equals("")){
                    String[] bodyArray = ifArray[5].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        body.add(convertStringToStatement(string2));
                    }
                }
                return new IfStmt(condition, body, line);
            case ELSE:
                String[] elseArray = statementArray[2].split(",");
                List<Statements> ebody = new ArrayList<>();
                if(!elseArray[0].equals("")){
                    String[] bodyArray = elseArray[0].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        ebody.add(convertStringToStatement(string2));
                    }
                }
                return new ElseStmt(ebody, line);
            case ELSEIF:
                String[] elseIfArray = statementArray[2].split(",");
                Expression efcondition = new Expression(elseIfArray[0], Integer.parseInt(elseIfArray[1]), ExpressionReturnType.valueOf(elseIfArray[2]));
                int efcolumn = Integer.parseInt(elseIfArray[3]);
                ExpressionReturnType efreturnType = ExpressionReturnType.valueOf(elseIfArray[4]);
                List<Statements> efbody = new ArrayList<>();
                if(!elseIfArray[5].equals("")){
                    String[] bodyArray = elseIfArray[5].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        efbody.add(convertStringToStatement(string2));
                    }
                }
                return new ElseIfStmt(efcondition, efbody, line);
            case END:
                return new EndBlockStmt(line);
            case RETURN:
                String[] returnArray = statementArray[2].split(",");
                Expression returnExpr = new Expression(returnArray[0], Integer.parseInt(returnArray[1]), ExpressionReturnType.valueOf(returnArray[2]));
                return new ReturnStmt(returnExpr, line);
            case MUTATION:
                String[] mutationArray = statementArray[2].split(",");
                Expression mutationExpr = new Expression(mutationArray[2], Integer.parseInt(mutationArray[3]), ExpressionReturnType.valueOf(mutationArray[4]));
                return new MutationStmt(mutationArray[0], mutationExpr, MutationType.valueOf(mutationArray[1]), line);
            case FUNCTION:
                String[] functionArray = statementArray[2].split(",");
                String name = functionArray[0];
                String[] argsArray = functionArray[1].split("\\?");
                Argument[] args = new Argument[argsArray.length];
                for (int i = 0; i < argsArray.length; i++) {
                    String[] argArray = argsArray[i].split(" ");
                    args[i] = new Argument(JavaishType.valueOf(argArray[0]), argArray[1]);
                }
                List<Statements> fbody = new ArrayList<>();
                if(!functionArray[3].equals("")){
                    String[] bodyArray = functionArray[3].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        fbody.add(convertStringToStatement(string2));
                    }
                }
                return new FunctionStmt(name, args, fbody, line);
            case CALL:
                String[] callArray = statementArray[2].split(",");
                String name = callArray[0];
                String[] paramsArray = callArray[1].split(",");
                Expression[] params = new Expression[paramsArray.length];
                for (int i = 0; i < paramsArray.length; i++) {
                    params[i] = new Expression(paramsArray[i], 0, ExpressionReturnType.NULL);
                }
                return new CallStmt(name, params, line);
            case ASSIGNMENT:
                String[] assignmentArray = statementArray[2].split(",");
                Expression assignmentExpr = new Expression(assignmentArray[1], Integer.parseInt(assignmentArray[2]), ExpressionReturnType.valueOf(assignmentArray[3]));
                return new AssignmentStmt(assignmentArray[0], assignmentExpr, line);
            case FOREACH:
                String[] foreachArray = statementArray[2].split(",");
                List<Statements> febody = new ArrayList<>();
                if(!foreachArray[2].equals("")){
                    String[] bodyArray = foreachArray[2].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        febody.add(convertStringToStatement(string2));
                    }
                }
                return new ForEachStmt(foreachArray[0], foreachArray[1], febody, line);
            case FORWHEN:
                String[] forwhenArray = statementArray[2].split(",");
                Expression forwhenExpr = new Expression(forwhenArray[0], Integer.parseInt(forwhenArray[3]), ExpressionReturnType.valueOf(forwhenArray[4]));
                List<Statements> fwbody = new ArrayList<>();
                if(!forwhenArray[5].equals("")){
                    String[] bodyArray = forwhenArray[5].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        fwbody.add(convertStringToStatement(string2));
                    }
                }
                return new ForWhenStmt(forwhenExpr, forwhenArray[1], Integer.parseInt(forwhenArray[2]), fwbody, line);
            case SHOWMSGBOX:
                String[] showMsgBoxArray = statementArray[2].split(",");
                Expression showMsgBoxExpr = new Expression(showMsgBoxArray[0], Integer.parseInt(showMsgBoxArray[1]), ExpressionReturnType.valueOf(showMsgBoxArray[2]));
                return new ShowMsgBoxStmt(showMsgBoxExpr, line);
            case PRINT:
                String[] printArray = statementArray[2].split(",");
                Expression printExpr = new Expression(printArray[0], Integer.parseInt(printArray[1]), ExpressionReturnType.valueOf(printArray[2]));
                return new PrintStmt(printExpr, line);
            case WHILE:
                String[] whileArray = statementArray[2].split(",");
                Expression whileExpr = new Expression(whileArray[0], Integer.parseInt(whileArray[1]), ExpressionReturnType.valueOf(whileArray[2]));
                List<Statements> wbody = new ArrayList<>();
                if(!whileArray[3].equals("")){
                    String[] bodyArray = whileArray[3].split("\\|\\|\\|");
                    for (String string2 : bodyArray) {
                        if(string2 == "***"){
                            continue;
                        }
                        wbody.add(convertStringToStatement(string2));
                    }
                }
                return new WhileStmt(whileExpr, wbody, line);
            case REMOVEALLFROM:
                String[] removeAllFromArray = statementArray[2].split(",");
                Expression removeAllFromExpr = new Expression(removeAllFromArray[0], Integer.parseInt(removeAllFromArray[1]), ExpressionReturnType.valueOf(removeAllFromArray[2]));
                return new RemoveAllFromStmt(removeAllFromExpr, removeAllFromArray[3], line);
            case REMOVEFROM:
                String[] removeFromArray = statementArray[2].split(",");
                Expression removeFromExpr = new Expression(removeFromArray[0], Integer.parseInt(removeFromArray[1]), ExpressionReturnType.valueOf(removeFromArray[2]));
                return new RemoveFromStmt(removeFromExpr, removeFromArray[3], line);
            case REMOVEAT:
                String[] removeAtArray = statementArray[2].split(",");
                Expression removeAtExpr = new Expression(removeAtArray[0], Integer.parseInt(removeAtArray[1]), ExpressionReturnType.valueOf(removeAtArray[2]));
                return new RemoveAtStmt(removeAtExpr, removeAtArray[3], line);
            default:
                break;
        }


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

