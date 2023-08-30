
package javaish;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
                    System.out.println("ForWhen: " + forwhenStmt.getCondition() + " Increment: " + forwhenStmt.getIncrement());
                    if(forwhenStmt.getBody() != null){
                        printStmts(forwhenStmt.getBody(), indent + 1);
                    }
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
}
