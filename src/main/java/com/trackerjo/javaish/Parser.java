package com.trackerjo.javaish;



import java.util.ArrayList;
import java.util.List;

import com.trackerjo.javaish.Expression.ExpressionReturnType;
import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.MutationType;
import com.trackerjo.javaish.Statements.RobotType;



public class Parser {
    String source;
    Variables variables;
    int lineNumber = 0;
    boolean importedRobot = false;
    ClassStmt classStmt = new ClassStmt(-1);
    List<Function> functions = new ArrayList<>();
    List<Variable> variablesList = new ArrayList<>();

    

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
                CommentStmt commentStmt = new CommentStmt(lineNumber, line);
                parents.get(parents.size() - 1).addStatement(commentStmt);
                continue;
            }
            String[] words = line.split(" ");
            switch (words[0]) {
                case "let":
                   
                    String[] declaration = parseDeclaration(line);
                    String varName = declaration[0];
                    if(!validVarName(varName)){
                        Error.InvalidVariableName(varName, lineNumber);
                    }

                    if(ReservedNames.isReserved(varName)){
                        Error.ReservedName(varName, lineNumber);
                    }

                    if(parents.get(parents.size() - 1).containsVariable(varName)){
                        Error.VariableAlreadyExists(varName, lineNumber);
                    } else {
                        parents.get(parents.size() - 1).addVariableName(varName);
                    }
                    JavaishType varType = getType(declaration[1]);
                    String varValue = declaration[2];
                    System.out.println("Expression: " + varValue);
                    ExpressionReturnType expressionType = ExpressionReturnType.STRING;
                    switch (varType) {
                        case STRING:
                            expressionType = ExpressionReturnType.STRING;
                            
                            break;

                        case INT:
                            expressionType = ExpressionReturnType.INT;
                            break;

                        case FLOAT:
                            expressionType = ExpressionReturnType.FLOAT;
                            break;
                        
                        case BOOLEAN:
                            expressionType = ExpressionReturnType.BOOL;
                            break;
                        case INTLIST:
                            expressionType = ExpressionReturnType.INTLIST;
                            break;
                        case FLOATLIST:
                            expressionType = ExpressionReturnType.FLOATLIST;
                            break;
                        case STRINGLIST:
                            expressionType = ExpressionReturnType.STRINGLIST;
                            break;
                        case BOOLEANLIST:
                            expressionType = ExpressionReturnType.BOOLEANLIST;
                            break;
                        
                        default:
                            break;
                    }
                    int columnVar = line.indexOf(varValue);
                    Expression expression = new Expression(varValue, expressionType, lineNumber, columnVar);
                    JavaishType exprReturnType = expression.typeExpression(expression, lineNumber, variablesList, functions);

                    if(exprReturnType != varType){
                        Error.TypeMismatch(varType.toString(), exprReturnType.toString(), lineNumber);
                    }
                    variablesList.add(new Variable(varName, varType));
                    DeclarationStmt dec = new DeclarationStmt(lineNumber,varName, varType, expression);
                    parents.get(parents.size() - 1).addStatement(dec);
                   
                    break;
                case "}":
                    Statements parent = parents.get(parents.size() - 1);


                    
                    if(words.length > 2  && words[1].equals("else") && words[2].equals("if")){
                        
                        parents.remove(parents.size() - 1);
                        parents.get(parents.size() - 1).addStatement(parent);
                        String condition = parseElseIf(line, "if");
                        int columnIC = line.indexOf(condition);
                        Expression boolExpression = new Expression(condition, ExpressionReturnType.BOOL, lineNumber, columnIC);
                        ElseIfStmt elseIfStmt = new ElseIfStmt(lineNumber, boolExpression);
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
                    int columnC = line.indexOf(condition);
                    System.out.println("Condition: " + condition);
                    Expression boolExpression = new Expression(condition, ExpressionReturnType.BOOL, lineNumber, columnC);
                    Element[] elements = boolExpression.getElements();
                    for (Element element : elements) {
                        System.out.println("Element: " + element.getType());
                        
                    }
                    JavaishType ifReturnType = boolExpression.typeExpression(boolExpression, lineNumber, variablesList, functions);

                    if(ifReturnType != JavaishType.BOOLEAN){
                        System.out.println("IfReturnType: " + lineNumber);
                        Error.TypeMismatch("BOOLEAN", ifReturnType.toString(), lineNumber);
                    }
                    IfStmt ifStmt = new IfStmt(lineNumber, boolExpression);
                    parents.add(ifStmt);
                    break;
                case "}else":
                    Statements parentE = parents.get(parents.size() - 1);
                    parents.remove(parents.size() - 1);
                    parents.get(parents.size() - 1).addStatement(parentE);
                    if(words.length > 1  && words[1].equals("if")){
                        
                       
                        String conditionE = parseElseIf(line, "if");
                        int columnE = line.indexOf(conditionE);
                        Expression boolExpressionE = new Expression(conditionE, ExpressionReturnType.BOOL, lineNumber, columnE);
                        ElseIfStmt elseIfStmt = new ElseIfStmt(lineNumber, boolExpressionE);
                        parents.add(elseIfStmt);
                    } else {
                     parents.add(new ElseStmt(lineNumber));
                    }
                    break;
                case "for":
                    //Get second word
                    if(words[1].equals("when")){
                        String[] forLoop = parseForWhen(line);
                        String forCondition = forLoop[0];
                        String forIncrementVar = forLoop[2];
                        String forIncrementVal = forLoop[1];
                        System.out.println("ForCondition: " + forCondition);
                        int columnFC = line.indexOf(forCondition);
                        int columnFIV = line.indexOf(forIncrementVal);
                        Expression forConditionExpression = new Expression(forCondition, ExpressionReturnType.STRING, lineNumber, columnFC);
                        Expression forIncrementExpression = new Expression(forIncrementVal, ExpressionReturnType.NUMBER, lineNumber, columnFIV);
                        JavaishType forConReturnType = forConditionExpression.typeExpression(forConditionExpression, lineNumber, variablesList, functions);

                        if(forConReturnType != JavaishType.BOOLEAN){
                            Error.TypeMismatch("BOOLEAN", forConReturnType.toString(), lineNumber);
                        }

                        JavaishType forIncReturnType = forIncrementExpression.typeExpression(forIncrementExpression, lineNumber, variablesList, functions);
                        if(forIncReturnType != JavaishType.INT && forIncReturnType != JavaishType.FLOAT){
                            Error.TypeMismatch("INT or FLOAT", forIncReturnType.toString(), lineNumber);
                        }
                        ForWhenStmt forStmt = new ForWhenStmt(lineNumber, forConditionExpression, forIncrementExpression, forIncrementVar);
                        parents.add(forStmt); 
                    } else if(words[1].equals("each")){
                        String[] forLoop = parseForEach(line);
                        String forVarName = forLoop[0];
                        String forListName = forLoop[1];
                        //Get type of forListName
                        JavaishType forListType = null;
                        for (Variable variable : variablesList) {
                            if(variable.getName().equals(forListName)){
                                forListType = variable.getType();
                            }
                        }
                        System.out.println("ForListType: " + forListType + " NAME: " + forListName) ;
                        JavaishType forVarType = null;
                        if (forListType == JavaishType.INTLIST) {
                            forVarType = JavaishType.INT;
                        } else if (forListType == JavaishType.FLOATLIST) {
                            forVarType = JavaishType.FLOAT;
                        } else if (forListType == JavaishType.STRINGLIST) {
                            forVarType = JavaishType.STRING;
                        } else if (forListType == JavaishType.BOOLEANLIST) {
                            forVarType = JavaishType.BOOLEAN;
                        } else {
                            Error.InvalidForLoop(lineNumber);
                        }
                        //Add forVarName to variablesList
                        variablesList.add(new Variable(forVarName, forVarType));
                        
                        
                        
                        ForEachStmt forStmt = new ForEachStmt(lineNumber, forVarName, forListName);
                        parents.add(forStmt);
                            
                    } else {
                        Error.InvalidForLoop(lineNumber);
                    }
                      
                    break;
                case "while":
                    String whileCondition = parseLoop(line, "while");
                    int columnW = line.indexOf(whileCondition);
                    Expression whileBoolExpression = new Expression(whileCondition, ExpressionReturnType.BOOL, lineNumber, columnW);
                    JavaishType whileReturnType = whileBoolExpression.typeExpression(whileBoolExpression, lineNumber, variablesList, functions);

                    if(whileReturnType != JavaishType.BOOLEAN){
                        Error.TypeMismatch("BOOLEAN", whileReturnType.toString(), lineNumber);
                    }
                    WhileStmt whileStmt = new WhileStmt(lineNumber, whileBoolExpression);
                    parents.add(whileStmt);
                    break;
                case "return":
                    String returnVal = parseReturn(line);
                    boolean hasReturn = returnVal != "";
                    int columnRe = line.indexOf(returnVal);

                    Expression returnExpression = new Expression(returnVal, ExpressionReturnType.STRING, lineNumber, columnRe);
                    JavaishType returnType = returnExpression.typeExpression(returnExpression, lineNumber, variablesList, functions);
                    FunctionStmt currentFunction = getParentFunction(parents);
                    String functionNameR = currentFunction.getName();
                    //Check if function has a return type
                    JavaishType functionReturnType = JavaishType.VOID;
                    for (Function function : functions) {
                        if(function.getFunctionName().equals(functionNameR)){
                            functionReturnType = function.getReturnType();
                        }
                    }
                    if(functionReturnType != JavaishType.VOID && hasReturn){
                        if(functionReturnType != returnType){
                            Error.ReturnTypeMismatch(functionNameR, functionReturnType.toString(), returnType.toString(), lineNumber);
                        }
                    }
                    //Set function return type
                    for (Function function : functions) {
                        if(function.getFunctionName().equals(functionNameR)){
                            function.setReturnType(returnType);
                        }
                    }
                    ReturnStmt returnStmt = new ReturnStmt(lineNumber, returnExpression, hasReturn);
                    parents.get(parents.size() - 1).addStatement(returnStmt);
                    break;
                case "return.":
                    ReturnStmt returnStmt2 = new ReturnStmt(lineNumber, new Expression("", ExpressionReturnType.STRING, lineNumber, 0), false);
                    parents.get(parents.size() - 1).addStatement(returnStmt2);
                    break;
                case "add":
                    String[] addMutation = parseMutationAS(line, "add");
                    String addVarName = addMutation[0];
                    String addChange = addMutation[1];
                    int columnA = line.indexOf(addChange);
                    Expression addExpression = new Expression(addChange, ExpressionReturnType.NUMBER, lineNumber, columnA);
                    JavaishType addReturnType = addExpression.typeExpression(addExpression, lineNumber, variablesList, functions);
                    JavaishType addVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(addVarName)){
                            addVarType = variable.getType();
                        }
                    }
                    if(addReturnType != addVarType){
                        if(addVarType == JavaishType.INTLIST || addVarType == JavaishType.FLOATLIST || addVarType == JavaishType.STRINGLIST || addVarType == JavaishType.BOOLEANLIST){
                            if(addReturnType != JavaishType.INT && addReturnType != JavaishType.FLOAT && addReturnType != JavaishType.STRING && addReturnType != JavaishType.BOOLEAN){
                                Error.TypeMismatch(addVarType.toString(), addReturnType.toString(), lineNumber);
                            }
                        } else {
                            Error.TypeMismatch(addVarType.toString(), addReturnType.toString(), lineNumber);
                        }

                    }
                    MutationStmt addStmt = new MutationStmt(lineNumber, addVarName, addExpression,MutationType.ADD);
                    parents.get(parents.size() - 1).addStatement(addStmt);
                    break;
                case "subtract":
                    String[] subtractMutation = parseMutationAS(line, "subtract");
                    String subtractVarName = subtractMutation[0];
                    String subtractChange = subtractMutation[1];
                    int columnS = line.indexOf(subtractChange);
                    Expression subtractExpression = new Expression(subtractChange, ExpressionReturnType.NUMBER, lineNumber, columnS);
                    JavaishType subtractReturnType = subtractExpression.typeExpression(subtractExpression, lineNumber, variablesList, functions);
                    JavaishType subtractVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(subtractVarName)){
                            subtractVarType = variable.getType();
                        }
                    }
                    if(subtractReturnType != subtractVarType){
                        Error.TypeMismatch(subtractVarType.toString(), subtractReturnType.toString(), lineNumber);
                    }
                    MutationStmt subtractStmt = new MutationStmt(lineNumber, subtractVarName, subtractExpression,MutationType.SUBTRACT);
                    parents.get(parents.size() - 1).addStatement(subtractStmt);
                    break;
                case "multiply":
                    String[] multiplyMutation = parseMutationMD(line, "multiply");
                    String multiplyVarName = multiplyMutation[1];
                    String multiplyChange = multiplyMutation[0];
                    int columnM = line.indexOf(multiplyChange); 
                    Expression multiplyExpression = new Expression(multiplyChange, ExpressionReturnType.NUMBER, lineNumber, columnM);
                    JavaishType multiplyReturnType = multiplyExpression.typeExpression(multiplyExpression, lineNumber, variablesList, functions);
                    JavaishType multiplyVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(multiplyVarName)){
                            multiplyVarType = variable.getType();
                        }
                    }
                    if(multiplyReturnType != multiplyVarType){
                        Error.TypeMismatch(multiplyVarType.toString(), multiplyReturnType.toString(), lineNumber);
                    }
                    MutationStmt multiplyStmt = new MutationStmt(lineNumber, multiplyVarName, multiplyExpression,MutationType.MULTIPLY);
                    parents.get(parents.size() - 1).addStatement(multiplyStmt);
                    break;
                case "divide":
                    String[] divideMutation = parseMutationMD(line, "divide");
                    String divideVarName = divideMutation[1];
                    String divideChange = divideMutation[0];
                    int columnD = line.indexOf(divideChange);
                    Expression divideExpression = new Expression(divideChange, ExpressionReturnType.NUMBER, lineNumber, columnD);
                    MutationStmt divideStmt = new MutationStmt(lineNumber, divideVarName, divideExpression,MutationType.DIVIDE);
                    JavaishType divideReturnType = divideExpression.typeExpression(divideExpression, lineNumber, variablesList, functions);
                    JavaishType divideVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(divideVarName)){
                            divideVarType = variable.getType();
                        }
                    }
                    if(divideReturnType != divideVarType){
                        Error.TypeMismatch(divideVarType.toString(), divideReturnType.toString(), lineNumber);
                    }
                    parents.get(parents.size() - 1).addStatement(divideStmt);
                    break;
                case "function":
                    String[] functionDeclaration = parseFunction(line);
                    String functionName = functionDeclaration[0];
                    //Check if valid function name
                    if(!validVarName(functionName)){
                        Error.InvalidFunctionName(functionName, lineNumber);
                    }
                    
                    if(ReservedNames.isReserved(functionName)){
                        Error.ReservedName(functionName, lineNumber);
                    }

                    System.out.println("FunctionName: " + functionName);
                    if(parents.get(parents.size() - 1).containsVariable(functionName)){
                        Error.VariableAlreadyExists(functionName, lineNumber);
                    } else {
                        parents.get(parents.size() - 1).addVariableName(functionName);
                    }
                    String[] functionArgs = functionDeclaration[1].split(",");
                    if(functionArgs.length == 1 && functionArgs[0].equals("")){
                        functionArgs = new String[0];
                    }
                    List<Argument> arguments = new ArrayList<>();
                    
                    for(int i = 0; i < functionArgs.length; i++){
                        if(functionArgs[i].isEmpty()){
                            continue;
                        }
                        String[] arg = functionArgs[i].split(":");
                        if(arg.length != 2){
                            //TODO: Make this a proper error
                            throw new RuntimeException("Invalid argument declaration. Full Arg" + functionDeclaration[1] + " Line: " + lineNumber); 
                        }
                        String argName = arg[1];
                        JavaishType argType = getType(arg[0]);
                        arguments.add(new Argument(argType, argName));
                        variablesList.add(new Variable(argName, argType));
                    }

                    Argument[] argumentsArr = arguments.toArray(new Argument[arguments.size()]);
                    
                    FunctionStmt functionStmt = new FunctionStmt(lineNumber, functionName, argumentsArr);
                    functions.add(new Function(functionName));
                    parents.add(functionStmt);
                    break;
                case "removeAll":
                    String[] removeAll = parseRemoveFrom(line, "removeAll");
                    String removeAllVarName = removeAll[0];
                    String removeAllValue = removeAll[1];
                    int columnRAll = line.indexOf(removeAllValue);
                    Expression removeAllExpression = new Expression(removeAllValue, ExpressionReturnType.STRING, lineNumber, columnRAll);
                    JavaishType removeAllReturnType = removeAllExpression.typeExpression(removeAllExpression, lineNumber, variablesList, functions);
                    JavaishType removeAllVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(removeAllVarName)){
                            removeAllVarType = variable.getType();
                        }
                    }
                    if(removeAllVarType == JavaishType.INTLIST && removeAllReturnType != JavaishType.INT){
                        Error.TypeMismatch("INT", removeAllReturnType.toString(), lineNumber);
                    } else if(removeAllVarType == JavaishType.FLOATLIST && removeAllReturnType != JavaishType.FLOAT){
                        Error.TypeMismatch("FLOAT", removeAllReturnType.toString(), lineNumber);
                    } else if(removeAllVarType == JavaishType.STRINGLIST && removeAllReturnType != JavaishType.STRING){
                        Error.TypeMismatch("STRING", removeAllReturnType.toString(), lineNumber);
                    } else if(removeAllVarType == JavaishType.BOOLEANLIST && removeAllReturnType != JavaishType.BOOLEAN){
                        Error.TypeMismatch("BOOLEAN", removeAllReturnType.toString(), lineNumber);
                    }
                    RemoveAllFromStmt removeAllStmt = new RemoveAllFromStmt(lineNumber, removeAllVarName, removeAllExpression);
                    parents.get(parents.size() - 1).addStatement(removeAllStmt);
                    break;
                case "remove":
                    String[] remove = parseRemoveFrom(line, "remove");
                    String removeVarName = remove[0];
                    String removeValue = remove[1];
                    int columnR = line.indexOf(removeValue);
                    Expression removeValExpression = new Expression(removeValue, ExpressionReturnType.STRING, lineNumber, columnR);
                    JavaishType removeReturnType = removeValExpression.typeExpression(removeValExpression, lineNumber, variablesList, functions);
                    JavaishType removeVarType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(removeVarName)){
                            removeVarType = variable.getType();
                        }
                    }
                    if(removeVarType == JavaishType.INTLIST && removeReturnType != JavaishType.INT){
                        Error.TypeMismatch("INT", removeReturnType.toString(), lineNumber);
                    } else if(removeVarType == JavaishType.FLOATLIST && removeReturnType != JavaishType.FLOAT){
                        Error.TypeMismatch("FLOAT", removeReturnType.toString(), lineNumber);
                    } else if(removeVarType == JavaishType.STRINGLIST && removeReturnType != JavaishType.STRING){
                        Error.TypeMismatch("STRING", removeReturnType.toString(), lineNumber);
                    } else if(removeVarType == JavaishType.BOOLEANLIST && removeReturnType != JavaishType.BOOLEAN){
                        Error.TypeMismatch("BOOLEAN", removeReturnType.toString(), lineNumber);
                    }
                    RemoveFromStmt removeStmt = new RemoveFromStmt(lineNumber, removeValExpression, removeVarName);
                    parents.get(parents.size() - 1).addStatement(removeStmt);
                    break;
                    
                case "removeAt":
                    String[] removeAt = parseRemoveAt(line);
                    String removeAtVarName = removeAt[0];
                    String removeAtLocation = removeAt[1];
                    int columnRA = line.indexOf(removeAtLocation);
                    Expression removeAtExpression = new Expression(removeAtLocation, ExpressionReturnType.NUMBER, lineNumber, columnRA);
                    JavaishType removeAtReturnType = removeAtExpression.typeExpression(removeAtExpression, lineNumber, variablesList, functions);
                    if(removeAtReturnType != JavaishType.INT){
                        Error.TypeMismatch("INT", removeAtReturnType.toString(), lineNumber);
                    }
                    RemoveAtStmt removeAtStmt = new RemoveAtStmt(lineNumber, removeAtExpression, removeAtVarName);
                    parents.get(parents.size() - 1).addStatement(removeAtStmt);
                    break;
                case "import":
                    String[] importStmt = parseImport(line);
                    String importName = importStmt[0];
                    ImportStmt importStmt2 = new ImportStmt(lineNumber, importName);
                    parents.get(parents.size() - 1).addStatement(importStmt2);
                    importedRobot = true;
                    break;
                case "robot":
                    if(!importedRobot){
                        Error.RobotNotImported(lineNumber);
                    }
                    FunctionCall robotStmt = parseRobot(line);
                    String robotAction = robotStmt.getFunctionName();
                    ArrayList<String> robotActionArgs = robotStmt.getArgs();
                    
                    Expression[] robotActionArgExpressions = new Expression[robotActionArgs.size()];
                    for(int i = 0; i < robotActionArgs.size(); i++){
                        if(robotActionArgs.get(i).isEmpty()){
                            continue;
                        }
                        String arg = robotActionArgs.get(i);
                       
                        ExpressionReturnType argType = ExpressionReturnType.STRING;
                        int columnArg = line.indexOf(arg);
                        
                        robotActionArgExpressions[i] = new Expression(arg, argType, lineNumber, columnArg);
                    }
                    //Check if robotAction is a valid RobotType
                    
                    RobotType robotType = getRobotType(robotAction);
                    

                    RobotStmt robotStmt2 = new RobotStmt(lineNumber, robotType, robotActionArgExpressions);
                    parents.get(parents.size() - 1).addStatement(robotStmt2);
                    break;
                
                case "set":
                    String[] setStmt = parseSet(line);
                    String setListName = setStmt[0];
                    String setListLocation = setStmt[1];
                    String setListValue = setStmt[2];
                    int columnSL = line.indexOf(setListValue);
                    Expression setListValueExpression = new Expression(setListValue, ExpressionReturnType.STRING, lineNumber, columnSL);
                    JavaishType setListValueReturnType = setListValueExpression.typeExpression(setListValueExpression, lineNumber, variablesList, functions);
                    
                    JavaishType setListType = null;
                    for (Variable variable : variablesList) {
                        if(variable.getName().equals(setListName)){
                            setListType = variable.getType();
                        }
                    }
                    if(setListType == JavaishType.INTLIST && setListValueReturnType != JavaishType.INT){
                        Error.TypeMismatch("INT", setListValueReturnType.toString(), lineNumber);
                    } else if(setListType == JavaishType.FLOATLIST && setListValueReturnType != JavaishType.FLOAT){
                        Error.TypeMismatch("FLOAT", setListValueReturnType.toString(), lineNumber);
                    } else if(setListType == JavaishType.STRINGLIST && setListValueReturnType != JavaishType.STRING){
                        Error.TypeMismatch("STRING", setListValueReturnType.toString(), lineNumber);
                    } else if(setListType == JavaishType.BOOLEANLIST && setListValueReturnType != JavaishType.BOOLEAN){
                        Error.TypeMismatch("BOOLEAN", setListValueReturnType.toString(), lineNumber);
                    }
                    ListValElmt setListLocStmt = new ListValElmt(setListName, new Expression(setListLocation, ExpressionReturnType.NUMBER, lineNumber, 0));
                    SetStmt setStmt2 = new SetStmt(lineNumber, setListName, setListValueExpression, setListLocStmt);
                    parents.get(parents.size() - 1).addStatement(setStmt2);
                    break;

                    
                
                default:
                    if((nextWord(line, words[0].length() + 1).equals("equals") || nextWord(line, words[0].length() + 1).equals("="))){
                        String assignment = parseAssignment(line, words[0]);
                        
                        String varValueA = assignment;
                        int columnVarA = line.indexOf(varValueA);
                        Expression expressionA = new Expression(varValueA, ExpressionReturnType.STRING, lineNumber, columnVarA);
                        JavaishType exprReturnTypeA = expressionA.typeExpression(expressionA, lineNumber, variablesList, functions);
                        JavaishType varTypeA = null;
                        for (Variable variable : variablesList) {
                            if(variable.getName().equals(words[0])){
                                varTypeA = variable.getType();
                            }
                        }
                        System.out.println("VarTypeA: " + exprReturnTypeA);
                        System.out.println("ExprReturnTypeA: " + lineNumber);
                        if(exprReturnTypeA != varTypeA){
                             Error.TypeMismatch(varTypeA.toString(), exprReturnTypeA.toString(), lineNumber);
                        }
                        AssignmentStmt assignmentStmt = new AssignmentStmt(lineNumber, words[0], expressionA);
                        parents.get(parents.size() - 1).addStatement(assignmentStmt);
                        
                    } else if(possibleFunctionName(words[0], parents)){
                        FunctionCall functionCall = parseFunctionCall(line);
                        ////System.out .println("FunctionCall: " + functionCall[0] + " " + functionCall[1]);
                        String functionCallName = functionCall.getFunctionName();
                        //System.out .println("FunctionCallName: " + functionCallName);
                        ArrayList<String> functionCallArgs = functionCall.getArgs();
                        Expression[] functionArgExpressions = new Expression[functionCallArgs.size()];
                        for(int i = 0; i < functionCallArgs.size(); i++){
                            if(functionCallArgs.get(i).isEmpty()){
                                continue;
                            }
                            String arg = functionCallArgs.get(i);
                        
                            ExpressionReturnType argType = ExpressionReturnType.STRING;
                            int columnArg = line.indexOf(arg);
                            
                            functionArgExpressions[i] = new Expression(arg, argType, lineNumber, columnArg);
                            
                        }
                        if(functionCallName.equals("print")){
                            ////System.out .println("ADDING PRINT STMt");
                            ////System.out .println("FunctionCall: " + functionCall[0] + " " + functionCall[1]);
                            if(functionArgExpressions.length != 1){
                            Error.ArgumentLengthMismatch("print", lineNumber, 1, functionArgExpressions.length );
                            }
                            if(functionCall.getArgs().size() == 0){
                                Error.ArgumentLengthMismatch("print", lineNumber, 1, 0 );
                            }
                            PrintStmt printStmt = new PrintStmt(lineNumber, functionArgExpressions[0]);
                            parents.get(parents.size() - 1).addStatement(printStmt);
                            break;
                        } else if(functionCallName.equals("dialog")){
                            if(functionArgExpressions.length != 1){
                            Error.ArgumentLengthMismatch("dialog", lineNumber, 1, functionArgExpressions.length );
                            }
                            if(functionCall.getArgs().size() == 0){
                                Error.ArgumentLengthMismatch("dialog", lineNumber, 1, 0 );
                            }
                            ShowMsgBoxStmt showMsgBoxStmt = new ShowMsgBoxStmt(lineNumber, functionArgExpressions[0]);
                            parents.get(parents.size() - 1).addStatement(showMsgBoxStmt);
                            break;
                        } 
                        CallStmt functionCallStmt = new CallStmt(lineNumber, functionCallName, functionArgExpressions);
                        parents.get(parents.size() - 1).addStatement(functionCallStmt);
                    }
                    else {
                        
                      Error.UnexpectedStmt(line, lineNumber);
                    }
                    
                

            }
        }


        

        return parents.get(0);
    }

    private RobotType getRobotType(String robotType){
        try {
            return RobotType.valueOf(robotType.toUpperCase());
        } catch (IllegalArgumentException e) {
            if(robotType.equals("random_eyes")){
                return RobotType.RANDOMEYES;
            } else if(robotType.equals("lying_back")){
                return RobotType.LYINGBACK;
            } else if(robotType.equals("lying_belly")){
                return RobotType.LYINGBELLY;
            }
            Error.InvalidRobotAction(robotType, lineNumber);
        }
        return null;
    }
    private boolean validVarName(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(!Character.isLetter(c) && c != '_' ){
                if(i>0){
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private String[] parseSet(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingLocation = false;
        boolean readingVar = false;
        boolean readingString = false;
        boolean readPeriod = false;
        boolean readingExpression = false;


        String rString = "";
        String varName = "";
        String location = "";
        String expression = "";
        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c =='"'){
                readingString = !readingString;
                rString += c;
            } else if(c == ' ' && !readingString){
                if(rString.equals("set") && readingId){
                    readingId = false;
                    readingVar = true;
                    rString = "";
                } else if(readingVar && nextWord(line, i+1).equals("sub")){
                    readingVar = false;
                    varName = rString;
                    readingLocation = true;
                    rString = "";
                } else if(location.equals("") && readingLocation && rString.equals("sub")){
                    rString = "";
                } else if(readingLocation && nextWord(line, i+1).equals("to")){
                    readingLocation = false;
                    location = rString;
                    readingExpression = true;
                    rString = "";
                } else if(expression.equals("") && readingExpression && rString.equals("to")){
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && readingExpression && !hasNext){
                expression = rString;
                rString = "";
                readPeriod = true;
            } else {
                rString += c;
            }
            i++;
        }

         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        String[] returnArray = {varName, location, expression};
        return returnArray;


    }

    private FunctionCall parseRobot(String line){
        //Remove robot from line
        String robotLine = line.substring(6);
        //Parse as a function call
        FunctionCall robotStmt = parseFunctionCall(robotLine);
        
        return robotStmt;
    }

    private String[] parseImport(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingName = false;
        boolean readingString = false;
        boolean readPeriod = false;

        String rString = "";
        String importName = "";
        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else
            if(c == ' ' && !readingString){
                if(readingId && rString.equals("import")){
                    readingId = false;
                    readingName = true;
                    rString = "";
                } else if(readingName){
                    importName = rString;
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString){
                readPeriod = true;
                if(readingName){
                    importName = rString;
                    rString = "";
                }
            } 
            else {
                rString += c;
            }
            i++;
        }

        if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }
        System.out.println("ImportName: " + importName);
        return new String[]{importName};
    }

    private String[] parseRemoveFrom(String line, String type){
        int i = 0;
        boolean readingId = true;
        boolean readingValue = false;
        boolean readingVar = false;
        boolean readingString = false;
        boolean readPeriod = false;

        String rString = "";
        String varName = "";
        String value = "";
        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c =='"'){
                readingString = !readingString;
                rString += c;
            } else if(c == ' ' && !readingString){
                if(rString.equals(type) && readingId){
                    readingId = false;
                    readingValue = true;
                    rString = "";
                } else if(readingValue && nextWord(line, i+1).equals("from")){
                    readingValue = false;
                    value = rString;
                    readingVar = true;
                    rString = "";
                } else if(varName.equals("") && readingVar && rString.equals("from")){
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString && !hasNext){
                varName = rString;
                rString = "";
                readPeriod = true;
            } else {
                rString += c;
            }
            i++;
        }
         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        String[] returnArray = {varName, value};
        return returnArray;


    }

    private String[] parseRemoveAt(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingLocation = false;
        boolean readingVar = false;
        boolean readingString = false;
        boolean readPeriod = false;


        String rString = "";
        String varName = "";
        String location = "";
        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c =='"'){
                readingString = !readingString;
                rString += c;
            } else if(c == ' ' && !readingString){
                if(rString.equals("removeAt") && readingId){
                    readingId = false;
                    readingVar = true;
                    rString = "";
                } else if(readingVar && nextWord(line, i+1).equals("sub")){
                    readingVar = false;
                    varName = rString;
                    readingLocation = true;
                    rString = "";
                } else if(location.equals("") && readingLocation && rString.equals("sub")){
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString && !hasNext){
                location = rString;
                rString = "";
                readPeriod = true;
            } else {
                rString += c;
            }
            i++;
        }

         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        String[] returnArray = {varName, location};
        return returnArray;


    }


    private boolean possibleFunctionName(String name, List<Statements> parents){
        System.out.println("Name: " + name);
       //Check if contains parenthesis
         if(name.contains("(")){
            String[] splitName = name.split("\\(");
            //System.out .println("SplitName: " + splitName[0]);
            String functionName = splitName[0];
            if(functionName.equals("print") || functionName.equals("dialog") || functionName.equals("input") || functionName.equals("toString") || functionName.equals("toInt") || functionName.equals("toBool") || functionName.equals("toFloat")){
                return true;
            }
            System.out.println("FunctionName: " + functionName + " " + parents.get(parents.size() - 1).containsVariable(functionName) + " " + functionName.contains(" ") + " " + functionName.length());
            if(functionName.contains(" ") || functionName.length() == 0){
                return false;
            }
            return true;
         } 
            return false;
    }

    private String[] parseForEach(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingEach = false;
        boolean readingVar = false;
        boolean readingList = false;
        boolean readingString = false;

        String rString = "";
        String varName = "";
        String listName = "";

        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else
            if(c == ' ' && !readingString){
                if(readingId && rString.equals("for")){
                   
                    readingId = false;
                    readingEach = true;
                    rString = "";
                } else if(readingEach && rString.equals("each")){
                    readingEach = false;
                    readingVar = true;
                    rString = "";
                }else if(readingVar){
                    readingVar = false;
                    varName = rString;
                    rString = "";
                } else if(rString.equals("in") && !readingList){
                    readingList = true;
                    rString = "";
                } else if(readingList){
                    readingList = false;
                    listName = rString;
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '{' && !readingString && listName.equals("")){
                listName = rString;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

        return new String[]{varName, listName};

    }

    private String[] parseForWhen(String line){
        int i = 0;
        boolean readingId = true;
        boolean readingWhen = false;
        boolean readingCondition = false;
        boolean readingIncrementVar = false;
        boolean readingIncrementVal = false;
        boolean readingString = false;
        boolean readingExpression = false;
        int parenCount = 0;
        String rString = "";
        String condition = "";
        String increment = "";
        String incrementVar = "";
        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else if(!readingString && c == '('){
                parenCount++;
                readingExpression = true;
                rString += c;
            } else if(!readingString && c == ')'){
                parenCount--;
                if(parenCount == 0){
                    readingExpression = false;
                    rString += c;
                } else {
                    rString += c;
                }
            } else if(readingExpression){
                rString += c;
            }
            else if(c == ' ' && !readingString && !readingExpression){
                if(readingId && rString.equals("for")){
                    readingId = false;
                    readingWhen = true;
                    rString = "";
                } else if(readingWhen && rString.equals("when")){
                    readingWhen = false;
                    readingCondition = true;
                    rString = "";
                } else if( readingCondition && nextWord(line, i+1).equals("increment")){
                    readingCondition = false;
                    condition = rString;
                    
                    rString = "";
                } else if(rString.equals("increment")){
                    readingIncrementVar = true;
                    rString = "";
                } else if(readingIncrementVar && nextWord(line, i + 1).equals("by")){
                    readingIncrementVar = false;
                    readingIncrementVal = true;
                    incrementVar = rString;
                    rString = "";
                    i += 3;
                }
                else if(readingIncrementVal){
                    readingIncrementVal = false;
                    increment = rString;
                    
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '{' && !readingString && increment.equals("") && !readingExpression){
                increment = rString;
                rString = "";
            } else if(c == ' ' && readingExpression){
                rString += c;
            } 
            else {
                rString += c;
            }
            i++;
        }

        return new String[]{condition, increment, incrementVar};

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
        boolean readPeriod = false;
        String rString = "";
        String varValue = "";

        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else
            if(c == ' ' && !readingString){
                if(readingName){
                    readingName = false;
                    
                    rString = "";
                } else if(!readingName && !readingValue && (rString.equals("=") || rString.equals("equals"))){
                    readingValue = true;
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString && readingValue && !hasNext){
                varValue = rString;
                rString = "";
                readingValue = false;
                readPeriod = true;
            } 
            else{
                rString += c;
            }
            i++;
        }

        if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        return varValue;

    }

    private FunctionCall parseFunctionCall(String line){
       int i = 0;
        
        boolean readingName = true;
        boolean readingArgs = false;
        boolean readingArgName = false;
        boolean readingString = false;
        boolean readPeriod = false;

        String rString = "";
        String functionName = "";
        ArrayList<String> args = new ArrayList<>();
        String argName = "";

        int functionDepth = 0;

        while(i < line.length()){
            char c = line.charAt(i);
            if(c == '"'){
                readingString = !readingString;
                rString += c;
            } else
            if(c == ' ' && !readingString){
                if(readingName){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgName = true;
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(readingName && c == '('){
                    functionDepth++;
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgName = true;
                    rString = "";
            } else if(c == '(' && !readingString){
                functionDepth++;
                rString += c;
                
            }
            else if(c == ',' && !readingString){
                argName = rString;
                args.add(argName);
                argName = "";
                
               
                readingArgName = true;
                rString = "";
            } else if(c == ')'){
                functionDepth--;
                if(functionDepth == 0){
                    argName = rString;
                    args.add(argName);
                
                    readingArgName = false;
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString){
               readPeriod = true;
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
        if(functionDepth != 0){
            Error.MissingClosingParenthesis(lineNumber);
        }

        if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        System.out.println("FunctionName: " + args);

        FunctionCall functionCall = new FunctionCall(functionName, args);
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
                    //Remove last char of rString if it is a comma
                    
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
                } else if(readingArgName && rString.contains(",")){
                    ////System.out .println("ArgName: " + rString);
                    argName = rString;
                    args += argType + ":" + argName + ",";
                    argName = "";
                    argType = "";
                    readingArgType = true;
                    readingArgName = false;
                    rString = "";
                }
            } else if(readingName && c == '('){
                    functionName = rString;
                    readingName = false;
                    readingArgs = true;
                    readingArgType = true;
                    rString = "";          
            } else if(c == ')'){
                argName = rString;
                if(!argName.equals("")){
                    args += argType + ":" + argName;
                    //System.out .println("ArgName: " + rString);
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

    private String[] parseMutationAS(String line, String type){
        int i = 0;
        boolean readingType = true;
        boolean readingChange = false;
        boolean readingVar = false;
        boolean readingString = false;
        boolean readPeriod = false;

        String id = "to";
        if(type.equals("subtract")){
            id = "from";
        }
        String rString = "";
        String varName = "";
        String change = "";
        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c =='"'){
                readingString = !readingString;
                rString += c;
            } else if(c == ' ' && !readingString){
                if(rString.equals(type) && readingType){
                    readingType = false;
                    readingChange = true;
                    rString = "";
                } else if(readingChange && nextWord(line, i+1).equals(id)){
                    readingChange = false;
                    change = rString;
                    readingVar = true;
                    rString = "";
                } else if(varName.equals("") && readingVar && rString.equals(id)){
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString && !hasNext){
                varName = rString;
                rString = "";
                readPeriod = true;
            } else {
                rString += c;
            }
            i++;
        }

         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        String[] returnArray = {varName, change};
        return returnArray;


    }

    private String[] parseMutationMD(String line, String type){
        int i = 0;
        boolean readingType = true;
        boolean readingChange = false;
        boolean readingVar = false;
        boolean readingString = false;
        boolean readPeriod = false;

        String rString = "";
        String varName = "";
        String change = "";
        while(i < line.length()){
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if(c =='"'){
                readingString = !readingString;
                rString += c;
            } else if(c == ' ' && !readingString){
                if(rString.equals(type) && readingType){
                    readingType = false;
                    readingVar = true;
                    rString = "";
                } else if(readingVar && nextWord(line, i+1).equals("by")){
                    readingVar = false;
                    change = rString;
                    readingChange = true;
                    rString = "";
                } else if(varName.equals("") && readingChange && rString.equals("by")){
                    rString = "";
                } else {
                    rString += c;
                }
            } else if(c == '.' && !readingString && !hasNext){
                varName = rString;
                rString = "";
                readPeriod = true;
            } else {
                rString += c;
            }
            i++;
        }

         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
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
        boolean readPeriod = false;

        String rString = "";
        String returnVal = "";

        while (i < line.length()) {
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 
            if (c == ' ') {
                if (readingId && rString.equals("return")) {
                    readingId = false;
                    rString = "";
                } else{
                    rString += c;
                }
            } else if(c == '.' && !hasNext){
                returnVal = rString;
                readPeriod = true;
                rString = "";
            } else {
                rString += c;
            }
            i++;
        }

         if(!readPeriod){
            Error.MissingPeriod(lineNumber);
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
        boolean readPeriod = false;
        
        String varName = "";
        String varType = "";
        String varValue = "";
        String rString = "";
        while (i < line.length()) {
            char c = line.charAt(i);
            boolean hasNext = i < line.length() - 1;
            char nextChar = ' ';
            if(hasNext){
                nextChar = line.charAt(i + 1);
            } 

            
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
                else if(rString.equals("=") || rString.equals("equal")){
                    readingValue = true;
                    rString = "";

                } else {
                    rString += c;
                }
            } else if(c == '"'){
                if(!readingString){
                    readingString = true;
                    rString += c;
                } else {
                    readingString = false;
                    rString += c;
                }
            } else if(c == '.' && !readingString && (!hasNext || nextChar == ' ')){
                varValue = rString;
                readPeriod = true;
            }else {
                rString += c;
                
            }
            i++;
        }
        if(!readPeriod){
            Error.MissingPeriod(lineNumber);
        }

        variableNames.add(varName);
        if(readingString){
            Error.UnclosedString(lineNumber);
        }

        
        return new String[]{varName, varType, varValue};

    }

    private JavaishType getType(String type) {
        switch (type) {
            case "String":
                return JavaishType.STRING;
            case "int":
                return JavaishType.INT;
            case "float":
                return JavaishType.FLOAT;
            case "bool":
                return JavaishType.BOOLEAN;
            case "String[]":
                return JavaishType.STRINGLIST;
            case "int[]":
                return JavaishType.INTLIST;
            case "float[]":
                return JavaishType.FLOATLIST;
            case "bool[]":
                return JavaishType.BOOLEANLIST;
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

    private FunctionStmt getParentFunction(List<Statements> parents){
        for(int i = parents.size() - 1; i >= 0; i--){
            if(parents.get(i) instanceof FunctionStmt){
                return (FunctionStmt) parents.get(i);
            }
        }
        return null;
    
    }
}

class FunctionCall {
    String functionName;
    ArrayList<String> args;

    public FunctionCall(String functionName, ArrayList<String> args) {
        this.functionName = functionName;
        this.args = args;
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }
}

class Function {
    String functionName;
    JavaishType returnType;

    public Function(String functionName, JavaishType returnType) {
        this.functionName = functionName;
        this.returnType = returnType;
    }

    public Function(String functionName) {
        this.functionName = functionName;
        this.returnType = JavaishType.VOID;
    }

    public String getFunctionName() {
        return functionName;
    }

    public JavaishType getReturnType() {
        return returnType;
    }

    public void setReturnType(JavaishType returnType) {
        this.returnType = returnType;
    }


}

class Variable{
    String name;
    JavaishType type;

    public Variable(String name, JavaishType type){
        this.name = name;
        this.type = type;
    }

    public String getName(){
        return name;
    }

    public JavaishType getType(){
        return type;
    }
}