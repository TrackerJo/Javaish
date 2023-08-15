package javish;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javish.Elements.ElementTypes;
import javish.Statements.StmtType;
import javish.Variables.VarType;

public class javish {

    
     public static void main(String[] args) throws IOException {
        String path = "javish/code.javish";
        runFile(path);
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        Variables variables = new Variables();
        Parser parser = new Parser(file, variables);
        Statements statements = parser.parse();
        System.out.println(statements.getBody());
        printStmts(statements.getBody(),0);
        // Organizer organizer = new Organizer(statements);
        // List<Elements> elements = organizer.organize();
        //System.out.println(elements);
        //printElmts(elements);
       
        
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
                    VarType varType = declaration.getVarType();
                    String value = declaration.getValue();

                    
                  
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
                    System.out.println("Return: " + returnStmt.getValue());
                    
                    break;
                case MUTATION:
                    MutationStmt mutationStmt = (MutationStmt) statement;
                    System.out.println("Mutation: " + mutationStmt.getVarName() + " " + mutationStmt.getMutationType() + " " + mutationStmt.getValue());
                    
                    break;
                case FUNCTION:
                    FunctionStmt functionStmt = (FunctionStmt) statement;
                    System.out.println("Function: " + functionStmt.getName() + " " + getArgsString(functionStmt.getArgs()));
                    
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
                    System.out.println("Assignment: " + assignmentStmt.getName() + " " + assignmentStmt.getValue());
                    
                    break;
                default:
                    break;
            }
        }
    }

    private static String getParamString(String[] params){
        String paramString = "";
        for (String param : params) {
            paramString += param + ", ";
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
