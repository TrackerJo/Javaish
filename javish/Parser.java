package javish;

import java.util.ArrayList;
import java.util.List;

import javish.Elements.MutationType;

import javish.Variables.VarType;

public class Parser {
    String source;
    Variables variables;
    int lineNumber = 0;
    ClassStmt classStmt = new ClassStmt(-1);

    List<String> variableNames = new ArrayList<>();
    public Parser(String source, Variables variables) {
        this.source = source;
        this.variables = variables;
    }

    private String removeIndents(String line){
        int i = 0;
        String rString = "";
        boolean readingIndents = true;
        while(i < line.length()){
            char c = line.charAt(i);
            
            if(c != ' ' && readingIndents){
                readingIndents = false;
                rString += c;
            } else if(!readingIndents){
                rString += c;
            }
            i++;
        }
        return rString;
    }

    public Statements parse(){
        List<Statements> parents = new ArrayList<>();
        parents.add(classStmt);
       
        String[] lines = source.split("\n");
        for (String line : lines) {
            line = removeIndents(line);
            lineNumber++;
            if (line.trim().isEmpty()) {
                continue;
            }
            //Check if line is a comment
            if(line.startsWith("//")){
                continue;
            }
            String[] words = line.split(" ");
            switch (words[0]) {
                case "let":
                   
                    String[] declaration = parseDeclaration(line);
                    String varName = declaration[0];
                    VarType varType = getType(declaration[1]);
                    String varValue = declaration[2];
                    DeclarationStmt dec = new DeclarationStmt(lineNumber,varName, varType, varValue);
                    parents.get(parents.size() - 1).addStatement(dec);
                    break;
                case "}":
                    Statements parent = parents.get(parents.size() - 1);


                    
                    if(words.length > 2  && words[1].equals("else") && words[2].equals("if")){
                        
                        parents.remove(parents.size() - 1);
                        parents.get(parents.size() - 1).addStatement(parent);
                        String condition = parseElseIf(line, "if");
                        ElseIfStmt elseIfStmt = new ElseIfStmt(lineNumber, condition);
                        parents.add(elseIfStmt);
                    } else if(words.length > 1 && words[1].equals("else")){
                        
                        parents.remove(parents.size() - 1);
                        parents.get(parents.size() - 1).addStatement(parent);
                        parents.add(new ElseStmt(lineNumber));

                       
                    } else {
                        parents.remove(parents.size() - 1);
                        parents.get(parents.size() - 1).addStatement(parent);
                    }
                
                    break;
                case "if":
                    String condition = parseLoop(line, "if");
                    IfStmt ifStmt = new IfStmt(lineNumber, condition);
                    parents.add(ifStmt);
                    break;
                case "}else":
                    Statements parentE = parents.get(parents.size() - 1);
                    parents.remove(parents.size() - 1);
                    parents.get(parents.size() - 1).addStatement(parentE);
                    if(words.length > 1  && words[1].equals("if")){
                        
                       
                        String conditionE = parseElseIf(line, "if");
                        ElseIfStmt elseIfStmt = new ElseIfStmt(lineNumber, conditionE);
                        parents.add(elseIfStmt);
                    } else {
                     parents.add(new ElseStmt(lineNumber));
                    }
                    break;
                case "for":
                    String forCondition = parseLoop(line, "for");
                    ForStmt forStmt = new ForStmt(lineNumber, forCondition);
                    parents.add(forStmt);   
                    break;
                case "while":
                    String whileCondition = parseLoop(line, "while");
                    WhileStmt whileStmt = new WhileStmt(lineNumber, whileCondition);
                    parents.add(whileStmt);
                    break;
                case "return":
                    String returnVal = parseReturn(line);
                    ReturnStmt returnStmt = new ReturnStmt(lineNumber, returnVal);
                    parents.get(parents.size() - 1).addStatement(returnStmt);
                    break;
                case "add":
                    String[] addMutation = parseMutation(line, "add");
                    String addVarName = addMutation[0];
                    String addChange = addMutation[1];
                    MutationStmt addStmt = new MutationStmt(lineNumber, addVarName, addChange,MutationType.ADD);
                    parents.get(parents.size() - 1).addStatement(addStmt);
                    break;
                case "subtract":
                    String[] subtractMutation = parseMutation(line, "subtract");
                    String subtractVarName = subtractMutation[0];
                    String subtractChange = subtractMutation[1];
                    MutationStmt subtractStmt = new MutationStmt(lineNumber, subtractVarName, subtractChange,MutationType.SUBTRACT);
                    parents.get(parents.size() - 1).addStatement(subtractStmt);
                    break;
                case "multiply":
                    String[] multiplyMutation = parseMutation(line, "multiply");
                    String multiplyVarName = multiplyMutation[0];
                    String multiplyChange = multiplyMutation[1];
                    MutationStmt multiplyStmt = new MutationStmt(lineNumber, multiplyVarName, multiplyChange,MutationType.MULTIPLY);
                    parents.get(parents.size() - 1).addStatement(multiplyStmt);
                    break;
                case "divide":
                    String[] divideMutation = parseMutation(line, "divide");
                    String divideVarName = divideMutation[0];
                    String divideChange = divideMutation[1];
                    MutationStmt divideStmt = new MutationStmt(lineNumber, divideVarName, divideChange,MutationType.DIVIDE);
                    parents.get(parents.size() - 1).addStatement(divideStmt);
                    break;
                case "function":
                    String[] functionDeclaration = parseFunction(line);
                    String functionName = functionDeclaration[0];
                    String[] functionArgs = functionDeclaration[1].split(",");
                    Argument[] arguments = new Argument[functionArgs.length];
                    
                    for(int i = 0; i < functionArgs.length; i++){
                        if(functionArgs[i].isEmpty()){
                            continue;
                        }
                        String[] arg = functionArgs[i].split(":");
                        if(arg.length != 2){
                            throw new RuntimeException("Invalid argument declaration");
                        }
                        String argName = arg[1];
                        VarType argType = getType(arg[0]);
                        arguments[i] = new Argument(argType, argName);
                    }
                    
                    FunctionStmt functionStmt = new FunctionStmt(lineNumber, functionName, arguments);
                    parents.add(functionStmt);
                    break;
                case "call":
                    String[] functionCall = parseFunctionCall(line);
                    String functionCallName = functionCall[0];
                    String[] functionCallArgs = functionCall[1].split(",");
                   
                    CallStmt functionCallStmt = new CallStmt(lineNumber, functionCallName, functionCallArgs);
                    parents.get(parents.size() - 1).addStatement(functionCallStmt);
                    break;
                default:
                    if(variableNames.contains(words[0]) && nextWord(line, words[0].length() + 1).equals("equals")){
                        String assignment = parseAssignment(line, words[0]);
                        
                        String varValueA = assignment;
                        AssignmentStmt assignmentStmt = new AssignmentStmt(lineNumber, words[0], varValueA);
                        parents.get(parents.size() - 1).addStatement(assignmentStmt);
                        
                    } else {
                        System.out.println("Error: Unknown statement at line " + lineNumber + ":" + line);
                      
                        System.exit(0);
                    }
                    
                

            }
        }


        

        return parents.get(0);
    }

     private String parseElseIf(String line, String id) {
        int i = 0;
        boolean readingBracket = true;
        boolean readingElse = false;
        boolean readingId = false;
        boolean readingCondition = false;

        String rString = "";
        String condition = "";
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == ' ' && !readingCondition) {
                if(rString.equals("}") && readingBracket){
                    readingBracket = false;
                    readingElse = true;
                    rString = "";
                } else if(rString.equals("else") && readingElse){
                    readingElse = false;
                    readingId = true;
                    rString = "";
                } else if (readingId && rString.equals(id)) {
                    readingId = false;
                    readingCondition = true;
                    rString = "";
                } else if(readingBracket && rString.equals("}else")){
                    readingBracket = false;
                    readingId = true;
                    rString = "";
                }
            } else if(c == '{'){
                //Check if last char of rString is a space
                if(rString.charAt(rString.length()-1) == ' '){
                    rString = rString.substring(0, rString.length()-1);
                }
                condition = rString;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

        return condition;

    }


    private String parseAssignment(String line, String varName){
        int i = 0;
        boolean readingName = true;
        boolean readingValue = false;
        boolean readingString = false;
        String rString = "";
        String varValue = "";

        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
            } else
            if(c == ' ' && !readingString){
                if(readingName){
                    readingName = false;
                    
                    rString = "";
                } else if(!readingName && !readingValue && rString.equals("equals")){
                    readingValue = true;
                    rString = "";
                } 
            } else if(c == '.' && !readingString && readingValue){
                varValue = rString;
                rString = "";
                readingValue = false;
            } 
            else{
                rString += c;
            }
            i++;
        }

        return varValue;

    }

    private String[] parseFunctionCall(String line){
       int i = 0;
        boolean readingId = true;
        boolean readingName = false;
        boolean readingArgs = false;
        boolean readingArgName = false;
        boolean readingString = false;

        String rString = "";
        String functionName = "";
        String args = "";
        String argName = "";

        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else
            if(c == ' ' && !readingString){
                if(rString.equals("call") && readingId){
                    readingId = false;
                    readingName = true;
                    rString = "";
                } else if(readingName){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgName = true;
                    rString = "";
                } 
            } else if(readingName && c == '('){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgName = true;
                    rString = "";
            } 
            else if(c == ','){
                argName = rString;
                args += argName + ",";
                argName = "";
                
               
                readingArgName = true;
                rString = "";
            } else if(c == ')'){
                argName = rString;
                args += argName;
               
                readingArgName = false;
                rString = "";
            } 
            else {
                if((c != ' ' || c != '(' || c != ')') && !readingString){
                    rString += c;
                } 
                if(readingString){
                    rString += c;
                }
                
            }
            i++;
        }

        String[] functionCall = {functionName, args};
        return functionCall;
    }

    private String[] parseFunction(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingName = false;
        boolean readingArgs = false;
        boolean readingArgType = false;
        boolean readingArgName = false;

        String rString = "";
        String functionName = "";
        String args = "";
        String argType = "";
        String argName = "";

        while(i < line.length()){
            char c = line.charAt(i);
            if(c == ' '){
                if(rString.equals("function") && readingId){
                    readingId = false;
                    readingName = true;
                    rString = "";
                } else if(readingName){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgType = true;
                    rString = "";
                } else if(readingArgType){
                    argType = rString;
                    readingArgType = false;
                    readingArgName = true;
                    rString = "";
                } 
            } else if(readingName && c == '('){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgType = true;
                    rString = "";
            } 
            else if(c == ','){
                argName = rString;
                args += argType + ":" + argName + ",";
                argName = "";
                argType = "";
                readingArgType = true;
                readingArgName = false;
                rString = "";
            } else if(c == ')'){
                argName = rString;
                if(!argName.equals("")){
                    args += argType + ":" + argName;
                }
               
                readingArgType = false;
                readingArgName = false;
                rString = "";
            } 
            else {
                if(c != ' ' || c != '(' || c != ')'){
                    rString += c;
                }
                
            }
            i++;
        }

        String[] functionDeclaration = {functionName, args};
        return functionDeclaration;

    }

    private String[] parseMutation(String line, String type){
        int i = 0;
        boolean readingType = true;
        boolean readingChange = false;
        boolean readingVar = false;
        boolean readingString = false;

        String rString = "";
        String varName = "";
        String change = "";

        while(i < line.length()){
            char c = line.charAt(i);
            if(c =='"'){
                readingString = !readingString;
            } else if(c == ' ' && !readingString){
                if(rString.equals(type) && readingType){
                    readingType = false;
                    readingChange = true;
                    rString = "";
                } else if(readingChange && nextWord(line, i+1).equals("to")){
                    readingChange = false;
                    change = rString;
                    readingVar = true;
                    rString = "";
                } else if(varName.equals("") && readingVar && rString.equals("to")){
                    rString = "";
                }
            } else if(c == '.'){
                varName = rString;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

        String[] returnArray = {varName, change};
        return returnArray;


    }

    private String nextWord(String line, int i){
        String rString = "";
        while(i < line.length()){
            char c = line.charAt(i);
            if(c == ' '){
                return rString;
            } else {
                rString += c;
            }
            i++;
        }
        return rString;
    }

    private String parseReturn (String line){
        int i = 0;
        boolean readingId = true;

        String rString = "";
        String returnVal = "";

        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == ' ' && !readingId) {
                if (readingId && rString.equals("return")) {
                    readingId = false;
                    rString = "";
                }
            } else if(c == '.'){
                returnVal = rString;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

        return returnVal;
    }

    private String parseLoop(String line, String id) {
        int i = 0;
        boolean readingId = true;
        boolean readingCondition = false;

        String rString = "";
        String condition = "";
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == ' ' && !readingCondition) {
                if (readingId && rString.equals(id)) {
                    readingId = false;
                    readingCondition = true;
                    rString = "";
                }
            } else if(c == '{'){
                //Check if last char of rString is a space
                if(rString.charAt(rString.length()-1) == ' '){
                    rString = rString.substring(0, rString.length()-1);
                }
                condition = rString;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

        return condition;

    }

    private String[] parseDeclaration(String line) {
        int i = 0;
        boolean readingId = true;
        boolean readingType = false;
        boolean readingName = false;
        boolean readingValue = false;
        boolean readingString = false;
        
        
        String varName = "";
        String varType = "";
        String varValue = "";
        String rString = "";
        while (i < line.length()) {
            char c = line.charAt(i);
            
            
             if(c == ' ' && !readingValue){
                if(readingId && rString.equals("let")){
                    
                    readingId = false;
                    readingType = true;
                    rString = "";
                } else if(readingType){
                    readingType = false;
                    varType= rString;
                    readingName = true;
                    rString = "";
                } else if(readingName){
                    readingName = false;
                    varName = rString;
                    rString = "";
                } 
                else if(rString.equals("equal")){
                    readingValue = true;
                    rString = "";

                }
            } else if(c == '"'){
                if(!readingString){
                    readingString = true;
                    rString += c;
                } else {
                    readingString = false;
                    rString += c;
                }
            } else if(c == '.' && !readingString){
                varValue = rString;
            }else {
                rString += c;
                
            }
            i++;
        }

        variableNames.add(varName);

        
        return new String[]{varName, varType, varValue};

    }

    private VarType getType(String type) {
        switch (type) {
            case "String":
                return VarType.STRING;
            case "int":
                return VarType.INT;
            case "float":
                return VarType.FLOAT;
            case "bool":
                return VarType.BOOL;
            default:
                return null;
        }
    }

    private MutationType getMutationType(String type){
        switch(type){
            case "add":
                return MutationType.ADD;
            case "subtract":
                return MutationType.SUBTRACT;
            case "multiply":
                return MutationType.MULTIPLY;
            case "divide":
                return MutationType.DIVIDE;
            default:
                return null;
        }
    }
}
