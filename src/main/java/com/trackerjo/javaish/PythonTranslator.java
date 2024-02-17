package com.trackerjo.javaish;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.trackerjo.javaish.Element.ElementType;
import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.MutationType;
import com.trackerjo.javaish.Statements.RobotType;



public class PythonTranslator {
    int lineNumber = 0;
    int tabCount = 0;
    List<String> javaLines = new ArrayList<String>();
    List<String> javaImports = new ArrayList<String>();
    boolean usedJOptionPane = false;
    boolean usedList = false;
    boolean usedMessageBox = false;
    boolean usingRobot = false;
    boolean usedRMove = false;
    boolean usedRSpeak = false;
    boolean usedRPosture = false;

    String robotIP;
    String projName;
    Variables globalVariables;


    enum Operator {
        PLUS, MINUS, DIVIDE, MULTIPLY, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, REMOVEALLFROM, REMOVEAT, REMOVEFROM
    }
    public PythonTranslator( Variables variables, String projName){
        
        this.globalVariables = variables;
        this.projName = projName;
   
    }

    public PythonTranslator(Variables variables, String projName, String robotIP){
        this.globalVariables = variables;
        this.projName = projName;
        this.robotIP = robotIP;
    }

    public List<String> getPythonLines(){
        List<String> finalJavaLines = new ArrayList<String>();
         //Add public variable declarations to top of file

       
        //Add imports to top of file
        for (String importString : javaImports) {
            finalJavaLines.add(importString);
        }

        //Add java lines
        for (String javaLine : javaLines) {
            finalJavaLines.add(javaLine);
        }

        return finalJavaLines;
    }

    public JavaishVal interpretFunction(List<Statements> statements,  Argument[] args,  JavaishVal[] params, String name, boolean isGlobal, boolean doTranslate){
        Variables localVariables = new Variables();
        List<String> javaPrinter = javaLines;
        if(!doTranslate){
            javaPrinter = new ArrayList<String>();
        }
        //System.out .println("Interpreting Function: " + name);
        int funcJavaLine = javaLines.size();
        if(!name.equals("$main") && doTranslate){
            System.out.println("Adding function: " + name);
            javaPrinter = javaLines;

           
            String funcLine = addTabCount() + "def " + name + "(";
            for (int i = 0; i < args.length; i++) {
                Argument arg = args[i];
                funcLine += arg.getName();
                if(i != args.length - 1){
                    funcLine += ", ";
                }
            }
            funcLine += "):";
            javaPrinter.add(funcLine);
            tabCount++;
        }
        
        
        if(args != null && params != null) { 
            if(args.length != params.length){
                //System.out .println("Argument length mismatch: " + args);
                Error.ArgumentLengthMismatch(name,lineNumber,args.length, params.length);
                // return null;
                for (Argument arg : args) {
                    //System.out .println("Arg: " + arg.getName() + " " + arg.getType());
                
                }
            }
       
            
            for (int i = 0; i < params.length; i++) {
               
                JavaishVal val = params[i];
                
                Argument arg = args[i];
                if(arg.getType() != val.getType()){
                    Error.ArgumentTypeMismatch(name, lineNumber, arg.getType().toString(), val.typeString());
                    return null;
                }
                localVariables.addVariable(arg.getName(), arg.getType(), val, lineNumber);
                
            }
        }

        System.out.println("Interpreting Function: " + name + " TAB COUNT: " + tabCount);
        JavaishVal returnVal = interpretBody(statements, localVariables, isGlobal, javaPrinter);
        if(!name.equals("$main") && doTranslate){
            tabCount--;
            
            

        }
      
        if(returnVal != null){
            return returnVal;
        }
        return null;

        
        
    }

    private JavaishVal interpretBody(List<Statements> statements,Variables funcVariables, boolean isGlobal, List<String> javaPrinter){
        Result pastResult = new Result(false);  
        Variables localVariables = new Variables(funcVariables);
        Return returnVal = new Return(false, null);
        

        for (Statements statement : statements) {
        
            if(returnVal.hasReturn()){
                return returnVal.getValue();
            }
            interpretStmt(statement, localVariables, isGlobal, pastResult, returnVal, javaPrinter);
            
        }
        if(returnVal.hasReturn()){
            return returnVal.getValue();
        }
        return null;
    }

    private void interpretStmt(Statements stmt, Variables localVariables, boolean isGlobal, Result pastResult, Return returnVal, List<String> javaPrinter){
       // System.out.println("Interpreting Stmt Type: " + stmt.getType());
        lineNumber = stmt.getLine();
        switch (stmt.getType()) {
            case ASSIGNMENT:
                AssignmentStmt assignment = (AssignmentStmt) stmt;

                evalAssignment(assignment, localVariables, isGlobal, javaPrinter);

                
                break;
            case COMMENT:
                CommentStmt comment = (CommentStmt) stmt;
                evalComment(comment, javaPrinter);
                break;
            case CALL:
                CallStmt call = (CallStmt) stmt;
                evalCall(call, localVariables, isGlobal, javaPrinter);
                break;
            case DECLARATION:
                DeclarationStmt declaration = (DeclarationStmt) stmt;
                evalDeclaration(declaration, localVariables, isGlobal, javaPrinter);
                break;
            case ELSE:
                ElseStmt elseStmt = (ElseStmt) stmt;
                evalElse(elseStmt, localVariables, pastResult, javaPrinter);
                break;
            case ELSEIF:
                ElseIfStmt elseifStmt = (ElseIfStmt) stmt;
                evalElseIf(elseifStmt, localVariables, pastResult, isGlobal, javaPrinter);
                break;
            case MUTATION:
                MutationStmt mutationStmt = (MutationStmt) stmt;
                evalMutation(mutationStmt, localVariables, isGlobal, javaPrinter);
                break;
            case RETURN:
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                evalReturn(returnStmt, localVariables, returnVal, isGlobal, javaPrinter);
                break;
            case FUNCTION:
                FunctionStmt function = (FunctionStmt) stmt;
                evalFunction(function, localVariables, isGlobal);
                break;
            case IF:
                IfStmt ifStmt = (IfStmt) stmt;
                evalIf(ifStmt, localVariables, isGlobal, pastResult, javaPrinter);
                break;
            case WHILE:
                WhileStmt whileStmt = (WhileStmt) stmt;
                evalWhile(whileStmt, localVariables, isGlobal, javaPrinter);
                break;
            case FOREACH:
                ForEachStmt foreachStmt = (ForEachStmt) stmt;
                evalForEach(foreachStmt, localVariables, isGlobal, javaPrinter);
                break;
            case FORWHEN:
                ForWhenStmt forwhenStmt = (ForWhenStmt) stmt;
                evalForWhen(forwhenStmt, localVariables, isGlobal, javaPrinter);
                break;
            case PRINT:
                PrintStmt printStmt = (PrintStmt) stmt;
                evalPrint(printStmt, localVariables, isGlobal, javaPrinter);
                break;
            case SHOWMSGBOX:
                ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) stmt;
                evalShowMsgBox(showMsgBoxStmt, localVariables, isGlobal, javaPrinter);
                break;
            case REMOVEAT:
                RemoveAtStmt removeAtStmt = (RemoveAtStmt) stmt;
                evalRemoveAt(removeAtStmt, localVariables, isGlobal, javaPrinter);
                break;
            case REMOVEFROM:
                RemoveFromStmt removeFromStmt = (RemoveFromStmt) stmt;
                evalRemoveFrom(removeFromStmt, localVariables, isGlobal, javaPrinter);
                break;
            case REMOVEALLFROM:
                RemoveAllFromStmt removeAllFromStmt = (RemoveAllFromStmt) stmt;
                evalRemoveAllFrom(removeAllFromStmt, localVariables, isGlobal, javaPrinter);
                break;
            case IMPORT:
                ImportStmt importStmt = (ImportStmt) stmt;
                evalImport(importStmt, javaPrinter);
                break;
            case ROBOT:
                RobotStmt robotStmt = (RobotStmt) stmt;
                evalRobot(robotStmt, localVariables, isGlobal, javaPrinter);
                break;
            default:
                break;
        }

    }

    private void evalRobot(RobotStmt robotStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        RobotType type = robotStmt.getRobotType();
        Expression[] expression = robotStmt.getValue();
       
        switch (type) {
            case MOVE:
                String valM = translateExpression(expression[0], localVariables, isGlobal, javaPrinter);

                if(!usedRMove){
                    javaLines.add("motion = ALProxy(\"ALMotion\", \"" + robotIP + "\", 9559)");
                    javaLines.add("motion.setStiffnesses(\"Body\", 1.0)");
                    usedRMove = true;
                }
                javaLines.add("motion.moveInit()");
                javaLines.add("motion.moveTo(" + valM + ",0,0)");
                
                break;
            case SPEAK:
                String valS = translateExpression(expression[0], localVariables, isGlobal, javaPrinter);

                if(!usedRSpeak){
                    javaLines.add("tts = ALProxy(\"ALTextToSpeech\", \"" + robotIP + "\", 9559)");
                    usedRSpeak = true;
                }
                javaLines.add("tts.say(" + valS + ")");
                break;
            case SIT:
                if(!usedRPosture){
                    javaLines.add("postureService = ALProxy(\"ALRobotPosture\", \"" + robotIP + "\", 9559)");
                    usedRPosture = true;
                }

                javaLines.add("postureService.goToPosture(\"Sit\", .8)");
                break;
            case STAND:
                if(!usedRPosture){
                    javaLines.add("postureService = ALProxy(\"ALRobotPosture\", \"" + robotIP + "\", 9559)");
                    usedRPosture = true;
                }
                javaLines.add("postureService.goToPosture(\"Stand\", .8)");
                break;
            default:
                break;
        }

    }

    private String evalRobotElmt(RobotActionElmt robotStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        RobotType type = robotStmt.getAction();
        Expression[] expression = robotStmt.getParams().toArray(new Expression[robotStmt.getParams().size()]);
       
        switch (type) {
            case MOVE:
                Error.FunctionHasNoReturn("robot move", lineNumber);
                
                break;
            case SPEAK:
            Error.FunctionHasNoReturn("robot move", lineNumber);
                break;
            case SIT:
                if(!usedRPosture){
                    javaLines.add("postureService = ALProxy(\"ALRobotPosture\", \"" + robotIP + "\", 9559)");
                    usedRPosture = true;
                }

                return "postureService.goToPosture(\"Sit\", .8)";

            case STAND:
                if(!usedRPosture){
                    javaLines.add("postureService = ALProxy(\"ALRobotPosture\", \"" + robotIP + "\", 9559)");
                    usedRPosture = true;
                }
                return "postureService.goToPosture(\"Stand\", .8)";

            default:

                break;
        }
        return "";

    }

    private void evalImport(ImportStmt importStmt, List<String> javaPrinter){
        usingRobot = true;
        javaImports.add("from naoqi import ALProxy");
    }

    private JavaishVal evalExpression(Expression expression, Variables localVariables, boolean isGlobal){
        JavaishVal total = null;
        Operator operation = null;
        Operator comparison = null;
        JavaishVal compVal = null;
        boolean isComp = false;
        System.out.println("EVALUATING EXPR: " + expression.toString());
        
        for(Element elmt : expression.getElements()){
            
            System.out.println("EVALUATING ELMT: " + elmt.getType());
            switch (elmt.getType()) {
                case AND:
                    AndElmt and = (AndElmt) elmt;
                    JavaishBoolean result = performComparision(comparison, total, compVal);
                    if(result == null){
                        return null;
                    }
                    if(result.getValue() == false){
                        return new JavaishBoolean(false);
                    }
                    total = null;
                    operation = null;
                    comparison = null;
                    compVal = null;
                    isComp = false;
                    break;
                case NOT:
                    NotElmt not = (NotElmt) elmt;
                    JavaishVal valNot = evalExpression(not.expression, localVariables, isGlobal);
                    if(valNot instanceof JavaishBoolean){
                        JavaishBoolean valB = (JavaishBoolean) valNot;
                        if(valB.getValue() == true){
                            valB = new JavaishBoolean(false);
                        } else {
                            valB = new JavaishBoolean(true);
                        }
                        if(isComp){
                            compVal = performOperation(operation, compVal, valB);
                        } else {
                            total = performOperation(operation, total, valB);
                        }
                    } else {
                        Error.TypeMismatch("Boolean", valNot.typeString(), lineNumber);
                        
                    }
                    break;
                case BOOL:
                    BoolElmt bool = (BoolElmt) elmt;
                    JavaishBoolean valB = new JavaishBoolean(bool.getValue());
                    if(isComp){
                        compVal = performOperation(operation, compVal, valB);
                    } else {
                        total = performOperation(operation, total, valB);
                    }
                    

                    break;
                case CAST:
                    CastElmt cast = (CastElmt) elmt;
                    JavaishVal val = evalExpression(cast.element, localVariables, isGlobal);
                    //System.out .println("Cast: " + cast.castType + " " + val.typeString());
                    switch (cast.castType) {
                        case FLOAT:
                            if(!(val instanceof JavaishFloat)){
                                try {
                                    if(val instanceof JavaishInt){
                                        val = new JavaishFloat(((JavaishInt) val).getValue());
                                    } else if(val instanceof JavaishString){
                                        val = new JavaishFloat(Float.parseFloat(((JavaishString) val).getValue()));
                                    } 
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    Error.UnableToParse("Float", lineNumber, val.typeString());
                                }
                            }
                            break;
                        case INT:
                            System.out .println("Parsing INT " + val.typeString());

                            if(!(val instanceof JavaishInt)){
                                try {
                                    if(val instanceof JavaishFloat){
                                       val = new JavaishInt(Math.round(((JavaishFloat) val).getValue()));
                                    } else if(val instanceof JavaishString){
                                        System.out .println("Parsing INT " + ((JavaishString) val).getValue());
                                        val =  new JavaishInt(Integer.parseInt(((JavaishString) val).getValue()));
                                    } 
                                } catch (Exception e) {
                                    Error.UnableToParse(val.typeString(), lineNumber, "Int");
                                }
                            }
                            break;
                        case STRING:
                            if(!(val instanceof JavaishString)){
                                try {
                                    if(val instanceof JavaishFloat){
                                        val = new JavaishString(Float.toString(((JavaishFloat) val).getValue()));
                                    } else if(val instanceof JavaishInt){
                                        val = new JavaishString(Integer.toString(((JavaishInt) val).getValue()));
                                    }  else if(val instanceof JavaishBoolean){
                                        val = new JavaishString(Boolean.toString(((JavaishBoolean) val).getValue()));
                                    }
                                } catch (Exception e) {
                                    Error.UnableToParse("String", lineNumber, val.typeString());
                                }
                            }
                            break;
                        case BOOLEAN:
                            if(!(val instanceof JavaishBoolean)){
                                try {
                                    if(val instanceof JavaishString){
                                        val = new JavaishBoolean(Boolean.parseBoolean(((JavaishString) val).getValue()));
                                    } 
                                } catch (Exception e) {
                                    Error.UnableToParse("Boolean", lineNumber, val.typeString());
                                }
                            }
                            break;
                        default:
                            break;
                        
                    }
                    //System.out .println("NEW CAST: " + val.typeString());
                    if(isComp){
                        compVal = performOperation(operation, compVal, val);
                    } else {
                        total = performOperation(operation, total, val);
                    }
                    break;
                case DIVIDE:
                    DivideElmt divide = (DivideElmt) elmt;
                    operation = Operator.DIVIDE;
                    break;
                case EQUAL:
                    EqualElmt equal = (EqualElmt) elmt;
                    comparison = Operator.EQUAL;
                    isComp = true;
                    break;
                case EXPRESSION:
                    ExpressionElmt expressionElmt = (ExpressionElmt) elmt;
                    JavaishVal newVal = evalExpression(expressionElmt.expression, localVariables, isGlobal);
                    if(isComp){
                        compVal = performOperation(operation, compVal, newVal);
                    } else {
                        total = performOperation(operation, total, newVal);
                    }
                    break;
                case FLOAT:
                    FloatElmt floatElmt = (FloatElmt) elmt;
                    JavaishFloat valF = new JavaishFloat(floatElmt.getValue());
                    if(isComp){
                        compVal = performOperation(operation, compVal, valF);
                    } else {
                        total = performOperation(operation, total, valF);
                    }
                    
                    break;
                case GREATER_THAN:
                    GreaterThanElmt greaterThan = (GreaterThanElmt) elmt;
                    comparison = Operator.GREATER_THAN;
                    isComp = true;
                    break;
                case GREATER_THAN_EQUAL:
                    GreaterThanEqualElmt greaterThanEqual = (GreaterThanEqualElmt) elmt;
                    comparison = Operator.GREATER_THAN_EQUAL;
                    isComp = true;
                    break;
                case INTEGER:
                    IntElmt integer = (IntElmt) elmt;
                    JavaishInt valI = new JavaishInt(integer.getValue());
                    if(isComp){
                        compVal = performOperation(operation, compVal, valI);
                    } else {
                        total = performOperation(operation, total, valI);
                    }
                    break;
                case LESS_THAN:
                    LessThanElmt lessThan = (LessThanElmt) elmt;
                    comparison = Operator.LESS_THAN;
                    isComp = true;
                    break;
                case LESS_THAN_EQUAL:
                    LessThanEqualElmt lessThanEqual = (LessThanEqualElmt) elmt;
                    comparison = Operator.LESS_THAN_EQUAL;
                    isComp = true;
                    break;
                case MINUS:
                    MinusElmt minus = (MinusElmt) elmt;
                    operation = Operator.MINUS;
                    break;
                case MULTIPLY:
                    MultiplyElmt multiply = (MultiplyElmt) elmt;
                    operation = Operator.MULTIPLY;
                    break;
               
                case NOT_EQUAL:
                    NotEqualElmt notEqual = (NotEqualElmt) elmt;
                    comparison = Operator.NOT_EQUAL;
                    isComp = true;
                    break;
                case OR:
                    OrElmt or = (OrElmt) elmt;
                    JavaishBoolean resultO = performComparision(comparison, total, compVal);
                    if(resultO == null){
                        return null;
                    }
                    if(resultO.getValue() == true){
                        return new JavaishBoolean(true);
                    }
                    total = null;
                    operation = null;
                    compVal = null;
                    isComp = false;
                    break;
                case PLUS:
                    PlusElmt plus = (PlusElmt) elmt;
                    operation = Operator.PLUS;
                    break;
                case STRING:
                    StringElmt string = (StringElmt) elmt;
                    
                    JavaishString valS = new JavaishString(string.getValue());
                    if(isComp){
                        
                        compVal = performOperation(operation, compVal, valS);
                    } else {
                        total = performOperation(operation, total, valS);
                    }

                    
                    
                    break;
                
                case VARIABLE:
                    VariableElmt variable = (VariableElmt) elmt;
                    JavaishVal valV = null;
                    if(localVariables.isVariable(variable.getName())){
                        valV = localVariables.getVariableValue(variable.getName());
                    } else if(globalVariables.isVariable(variable.getName())){
                        valV = globalVariables.getVariableValue(variable.getName());
                    } else {
                        Error.VariableNotDeclared(variable.getName(), lineNumber);
                        return null;
                    }
                    if(isComp){
                        compVal = performOperation(operation, compVal, valV);
                    } else {
                        total = performOperation(operation, total, valV);
                    }
                    break;
                case FUNCTION:
                    FunctionElmt function = (FunctionElmt) elmt;
                    List<Statements> body = globalVariables.getFunctionBody(function.getName());
                    Argument[] args = globalVariables.getFunctionArgs(function.getName());
                    Expression[] params = function.getParams();
                    List<JavaishVal> paramVals = new ArrayList<JavaishVal>();
                    for (Expression param : params) {
                        paramVals.add(evalExpression(param, localVariables, isGlobal));
                    }
                    JavaishVal[] paramValsArr = paramVals.toArray(new JavaishVal[paramVals.size()]);
                    JavaishVal valFunc = interpretFunction(body, args, paramValsArr, function.getName(), isGlobal, false);

                    
                    
                    if(isComp){
                        compVal = performOperation(operation, compVal, valFunc);
                    } else {
                        total = performOperation(operation, total, valFunc);
                    }
                    break;
                case SHOWINPUTBOX:
                    ShowInputBoxElmt showInputBox = (ShowInputBoxElmt) elmt;
                    JavaishString input = new JavaishString("0");
                      if(isComp){
                        compVal = performOperation(operation, compVal, input);
                    } else {
                        total = performOperation(operation, total, input);
                    }
                    break;
                    
                    
                case LISTVAL:
                    
                    ListValElmt listVal = (ListValElmt) elmt;
                    JavaishVal index = evalExpression(listVal.getIndex(), localVariables, isGlobal);
                    JavaishInt indexInt = null;
                    if(index instanceof JavaishInt){
                        indexInt = (JavaishInt) index;
                    } else {
                        Error.TypeMismatch("Int", index.typeString(), lineNumber);
                        return null;
                    }
                    JavaishList listValList = null;
                    if(localVariables.isVariable(listVal.getListName())){
                        JavaishVal valListVal = localVariables.getVariableValue(listVal.getListName());
                        JavaishListVal valList = (JavaishListVal) valListVal;
                        listValList = valList.getValue();
                    } else if(globalVariables.isVariable(listVal.getListName())){
                        JavaishVal valListVal = globalVariables.getVariableValue(listVal.getListName());
                        JavaishListVal valList = (JavaishListVal) valListVal;
                        listValList = valList.getValue();
                    } else {
                        Error.VariableNotDeclared(listVal.getListName(), lineNumber);
                        return null;
                    } 
                    int listLength = listValList.getLength();
                    if(indexInt.getValue() >= listLength){
                        Error.IndexOutOfBounds(indexInt.getValue(), lineNumber, listLength);
                        return null;
                    }
                    JavaishVal listValVal = listValList.getValue(indexInt.getValue());
                    if(isComp){
                        compVal = performOperation(operation, compVal, listValVal);
                    } else {
                        total = performOperation(operation, total, listValVal);
                    }
                    break;

                    
                case LIST:
                    
                    ListElmt list = (ListElmt) elmt;
                    JavaishType type = list.getListType();
                    List<Expression> listExpressions = list.getList();
                    List<JavaishVal> listVals = new ArrayList<JavaishVal>();
                    for (Expression listExpression : listExpressions) {
                        listVals.add(evalExpression(listExpression, localVariables, isGlobal));
                    }
                    switch (type) {
                        case INTLIST:
                        
                            List<JavaishInt> intList = new ArrayList<JavaishInt>();
                            for (JavaishVal listValI : listVals) {
                                if(listValI instanceof JavaishInt){
                                    intList.add((JavaishInt) listValI);
                                } else {
                                    Error.TypeMismatch("Int", listValI.typeString(), lineNumber);
                                    return null;
                                }
                            }
                            
                            JavaishIntList intListVal = new JavaishIntList(intList);
                            if(isComp){
                                if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "IntList", lineNumber);
                                } else {
                                    compVal = new JavaishListVal(intListVal);
                                }
                            } else {
                                 if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "IntList", lineNumber);
                                } else {
                                    total = new JavaishListVal(intListVal);
                                }
                            }
                           
                            break;
                        case FLOATLIST:
                            List<JavaishFloat> floatList = new ArrayList<JavaishFloat>();
                            for (JavaishVal listValI : listVals) {
                                if(listValI instanceof JavaishFloat){
                                    floatList.add((JavaishFloat) listValI);
                                } else {
                                    Error.TypeMismatch("Float", listValI.typeString(), lineNumber);
                                    return null;
                                }
                            }
                            
                            JavaishFloatList floatListVal = new JavaishFloatList(floatList);
                            if(isComp){
                                if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "FloatList", lineNumber);
                                } else {
                                    compVal = new JavaishListVal(floatListVal);
                                }
                            } else {
                                 if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "FloatList", lineNumber);
                                } else {
                                    total = new JavaishListVal(floatListVal);
                                }
                            }
                            break;
                        case STRINGLIST:
                            List<JavaishString> stringList = new ArrayList<JavaishString>();
                            for (JavaishVal listValI : listVals) {
                                if(listValI instanceof JavaishString){
                                    stringList.add((JavaishString) listValI);
                                } else {
                                    Error.TypeMismatch("String", listValI.typeString(), lineNumber);
                                    return null;
                                }
                            }
                            
                            JavaishStringList stringListVal = new JavaishStringList(stringList);
                            if(isComp){
                                if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "StringList", lineNumber);
                                } else {
                                    compVal = new JavaishListVal(stringListVal);
                                }
                            } else {
                                 if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "StringList", lineNumber);
                                } else {
                                    total = new JavaishListVal(stringListVal);
                                }
                            }
                            break;
                        case BOOLEANLIST:
                            List<JavaishBoolean> booleanList = new ArrayList<JavaishBoolean>();
                            for (JavaishVal listValI : listVals) {
                                if(listValI instanceof JavaishBoolean){
                                    booleanList.add((JavaishBoolean) listValI);
                                } else {
                                    Error.TypeMismatch("Boolean", listValI.typeString(), lineNumber);
                                    return null;
                                }
                            }
                            
                            JavaishBooleanList booleanListVal = new JavaishBooleanList(booleanList);
                            if(isComp){
                                if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "BooleanList", lineNumber);
                                } else {
                                    compVal = new JavaishListVal(booleanListVal);
                                }
                            } else {
                                 if(operation != null){
                                    Error.CantPerformOperation(operation.toString(), "BooleanList", lineNumber);
                                } else {
                                    total = new JavaishListVal(booleanListVal);
                                }
                            }
                            break;
                        

                        default:
                            break;
                    }

               
                    break;
                case ARRAYLENGTH:
                    ArrayLengthElmt arrayLength = (ArrayLengthElmt) elmt;
                    String arrayName = arrayLength.getArrayName();
                    JavaishVal arrayVal = null;
                    if(localVariables.isVariable(arrayName)){
                        arrayVal = localVariables.getVariableValue(arrayName);
                    } else if(globalVariables.isVariable(arrayName)){
                        arrayVal = globalVariables.getVariableValue(arrayName);
                    } else {
                        Error.VariableNotDeclared(arrayName, lineNumber);
                        return null;
                    }
                    if(arrayVal instanceof JavaishListVal){
                        JavaishListVal listLVal = (JavaishListVal) arrayVal;
                        JavaishList listL = listLVal.getValue();
                        JavaishInt length = new JavaishInt(listL.getLength());
                        if(isComp){
                            compVal = performOperation(operation, compVal, length);
                        } else {
                            total = performOperation(operation, total, length);
                        }
                    } else {
                        Error.TypeMismatch("List", arrayVal.typeString(), lineNumber);
                        return null;
                    }
                    break;
                case RobotActionElmt:
                    
                    if(isComp){
                        compVal = new JavaishBoolean(true);
                    } else {
                        total = new JavaishBoolean(true);
                    }
                    break;
                default:
                    break;
            }
        }
        if(isComp){
            return performComparision(comparison, total, compVal);
        }

        return total;
    }

    private JavaishBoolean performComparision(Operator comparison, JavaishVal left, JavaishVal right){
        JavaishBoolean result = null;
        switch (comparison) {
            case EQUAL:
                if(left.getType() == JavaishType.STRING && right.getType() == JavaishType.STRING){
                    if(((JavaishString) left).getValue().equals(((JavaishString) right).getValue())){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("String", "Number or Bool", lineNumber);
                    return null;
                }
                else if(left.getType() == JavaishType.BOOLEAN && right.getType() == JavaishType.BOOLEAN){
                    if(((JavaishBoolean) left).getValue() == ((JavaishBoolean) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Boolean", "Number or String", lineNumber);
                    return null;
                }
                else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() == ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if((float)(((JavaishInt) left).getValue()) == ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() == (float)(((JavaishInt) right).getValue())){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() == ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                
                break;
            case NOT_EQUAL:
                if(left.getType() == JavaishType.STRING && right.getType() == JavaishType.STRING){
                    if(!(((JavaishString) left).getValue().equals(((JavaishString) right).getValue()))){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("String", "Number or Bool", lineNumber);
                    return null;
                }
                else if(left.getType() == JavaishType.BOOLEAN && right.getType() == JavaishType.BOOLEAN){
                    if(((JavaishBoolean) left).getValue() != ((JavaishBoolean) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Boolean", "Number or String", lineNumber);
                    return null;
                }
                else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() != ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if((float)(((JavaishInt) left).getValue()) != ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() != (float)(((JavaishInt) right).getValue())){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() != ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                
                break;
            case LESS_THAN:
                if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                } 
                if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() < ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if(((JavaishInt) left).getValue() < ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() < ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() < ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                break;
            case GREATER_THAN:
                if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() > ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if(((JavaishInt) left).getValue() > ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() > ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() > ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                break;

            case LESS_THAN_EQUAL:
                if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() <= ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if(((JavaishInt) left).getValue() <= ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() <= ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() <= ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                break;
            case GREATER_THAN_EQUAL:
                if(left.getType() == JavaishType.STRING || right.getType() == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left.getType() == JavaishType.BOOLEAN || right.getType() == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left.getType() == JavaishType.INT && right.getType() == JavaishType.INT){
                    if(((JavaishInt) left).getValue() >= ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.INT && right.getType() == JavaishType.FLOAT){
                    if(((JavaishInt) left).getValue() >= ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else if(left.getType() == JavaishType.FLOAT && right.getType() == JavaishType.INT){
                    if(((JavaishFloat) left).getValue() >= ((JavaishInt) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                } else {
                    if(((JavaishFloat) left).getValue() >= ((JavaishFloat) right).getValue()){
                        result = new JavaishBoolean(true);
                    } else {
                        result = new JavaishBoolean(false);
                    }
                }
                break;

        
            default:
                break;
        }

        return result;
    }

    private String translateExpression(Expression expression, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
       String expr = "";
       
        
        for(Element elmt : expression.getElements()){
            switch (elmt.getType()) {
                case AND:
                    expr += " and ";
                    break;
                case NOT:
                    NotElmt not = (NotElmt) elmt;
                    expr += " not " + translateExpression(not.expression, localVariables, isGlobal, javaPrinter);
                    break;
                case BOOL:
                    BoolElmt bool = (BoolElmt) elmt;
                    boolean valB = bool.getValue();
                    //Capitalize first letter
                    String valBStr = valB ? "True" : "False";
                    expr += valBStr;
                    

                    break;
                case CAST:
                    CastElmt cast = (CastElmt) elmt;
                    JavaishVal val = evalExpression(cast.element, localVariables, isGlobal);
                    String castExpr = translateExpression(cast.element, localVariables, isGlobal, javaPrinter);
                    System.out.println("CastExpr: " + castExpr + " " + val.typeString());
                    //System.out .println("Cast: " + cast.castType + " " + val.typeString());
                    switch (cast.castType) {
                        case FLOAT:
                            if(!(val instanceof JavaishFloat)){
                                if(val instanceof JavaishInt){
                                    expr += "float(" + castExpr + ")";
                                } else if(val instanceof JavaishString){
                                    expr += "float(" + castExpr + ")";
                                }
                            }
                            break;
                        case INT:
                          
                            
                            if(val instanceof JavaishFloat){
                                expr += "int(" + castExpr + ")";
                            } else if(val instanceof JavaishString){
                                
                                expr += "int(" + castExpr + ")";
                            } 
                            
                            
                            break;
                        case STRING:
                           
                            if(!(val instanceof JavaishString)){
                               expr += "str(" + castExpr + ")";
                            }
                            break;
                        case BOOLEAN:
                            if(!(val instanceof JavaishBoolean)){
                                
                                if(val instanceof JavaishString){
                                    expr += "bool(" + castExpr + ")";
                                } 
                                
                            }
                            break;
                        default:
                            break;
                        
                    }
                    System.out.println("CastExpr: " + expr);
                    break;
                case DIVIDE:
                    expr += " / ";
                    
                    break;
                case EQUAL:
                    expr += " == ";
                    break;
                case EXPRESSION:
                    ExpressionElmt expressionElmt = (ExpressionElmt) elmt;
                    expr += translateExpression(expressionElmt.expression, localVariables, isGlobal, javaPrinter);
                    break;
                case FLOAT:
                    FloatElmt floatElmt = (FloatElmt) elmt;
                    JavaishFloat valF = new JavaishFloat(floatElmt.getValue());
                    expr += valF.getValue();
                    
                    break;
                case GREATER_THAN:
                    expr += " > ";
                    break;
                case GREATER_THAN_EQUAL:
                    expr += " >= ";
                    break;
                case INTEGER:
                    IntElmt integer = (IntElmt) elmt;
                    JavaishInt valI = new JavaishInt(integer.getValue());
                    expr += valI.getValue();
                    break;
                case LESS_THAN:
                    expr += " < ";
                    break;
                case LESS_THAN_EQUAL:
                    expr += " <= ";
                    break;
                case MINUS:
                    expr += " - ";
                    break;
                case MULTIPLY:
                    expr += " * ";
                    break;
               
                case NOT_EQUAL:
                    expr += " != ";
                    break;
                case OR:
                    expr += " or ";
                    break;
                case PLUS:
                    expr += " + ";
                    break;
                case STRING:
                    StringElmt string = (StringElmt) elmt;
                    
                    JavaishString valS = new JavaishString(string.getValue());
                    expr += "\"" + valS.getValue() + "\"";
                    
                    
                    break;
                
                case VARIABLE:
                    VariableElmt variable = (VariableElmt) elmt;
                    expr += variable.getName();
                    break;
                case FUNCTION:
                    FunctionElmt function = (FunctionElmt) elmt;
                    List<Statements> body = globalVariables.getFunctionBody(function.getName());
                    Argument[] args = globalVariables.getFunctionArgs(function.getName());
                    Expression[] params = function.getParams();
                    List<JavaishVal> paramVals = new ArrayList<JavaishVal>();
                    String funcExpr = function.getName() + "(";
                    for (Expression param : params) {
                       funcExpr += translateExpression(param, localVariables, isGlobal, javaPrinter) + ", ";
                    }
                    funcExpr = funcExpr.substring(0, funcExpr.length() - 2);
                    funcExpr += ")";
                    expr += funcExpr;
                    break;
                case SHOWINPUTBOX:
                    ShowInputBoxElmt showInputBox = (ShowInputBoxElmt) elmt;
                    String inputLine = evalShowInputBox(showInputBox, localVariables, isGlobal, javaPrinter);
                    expr += inputLine;
                    break;
                case LISTVAL:
                    
                    ListValElmt listVal = (ListValElmt) elmt;
                    String index = translateExpression(listVal.getIndex(), localVariables, isGlobal, javaPrinter);
                    String listName = listVal.getListName();
                    expr += listName + "[" + index + "]";
                    
                    break;

                    
                case LIST:
                    
                    ListElmt list = (ListElmt) elmt;
                    
                    String ltype = typeToString(list.getListType());

                    //Capitalize first letter
                    ltype = ltype.substring(0, 1).toUpperCase() + ltype.substring(1);
                    List<Expression> listExpressions = list.getList();
                    String listExpr = "[";
                    
                   
                    for (Expression listExpression : listExpressions) {
                        listExpr += translateExpression(listExpression, localVariables, isGlobal, javaPrinter) + ", ";
                    }
                    listExpr = listExpr.substring(0, listExpr.length() - 2);
                    listExpr += "]";
                    expr += listExpr;
                   
               
                    break;
                case ARRAYLENGTH:
                    ArrayLengthElmt arrayLength = (ArrayLengthElmt) elmt;
                    String arrayName = arrayLength.getArrayName();
                    expr += "len(" + arrayName + ")";
                    break;
                
                case RobotActionElmt:
                    RobotActionElmt robotAction = (RobotActionElmt) elmt;
                    String action = evalRobotElmt(robotAction, localVariables, isGlobal, javaPrinter);
                    expr += action;
                    break;
                default:
                    break;
            }
        }
        return expr;
    }


    private JavaishVal performOperation(Operator operation, JavaishVal total, JavaishVal val2){
        JavaishVal result = null;
        if(operation == null){
            //System.out.println(val2.getValue() + " SINGLE VAL");
            return val2;
        }
        switch (operation) {
            case PLUS:
                if(total instanceof JavaishString){
                    if(!(val2 instanceof JavaishString)){
                         Error.TypeMismatch("String", val2.typeString(), lineNumber);
                        return null;
                    }
                    result = new JavaishString(((JavaishString) total).getValue() + ((JavaishString) val2).getValue());
                } else if(val2 instanceof JavaishString){
                    if(!(total instanceof JavaishString)){
                        Error.TypeMismatch("String", total.typeString(), lineNumber);
                        return null;
                    } else {
                    result = new JavaishString(((JavaishString) total).getValue() + ((JavaishString) val2).getValue());
                    }
                } else if(total instanceof JavaishFloat){
                    if(val2 instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishFloat) total).getValue() + ((JavaishInt) val2).getValue());
                    } else {
                    result = new JavaishFloat(((JavaishFloat) total).getValue() + ((JavaishFloat) val2).getValue());
                    }
                } else if(val2 instanceof JavaishFloat){
                    if(total instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishInt) total).getValue() + ((JavaishFloat) val2).getValue());
                    } else {
                    result = new JavaishFloat(((JavaishFloat) total).getValue() + ((JavaishFloat) val2).getValue());
                    }
                } else {
                    result = new JavaishInt(((JavaishInt) total).getValue() + ((JavaishInt) val2).getValue());
                }
                
                break;
            case MULTIPLY:
                if(total instanceof JavaishString || val2 instanceof JavaishString){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(total instanceof JavaishFloat){
                    if(val2 instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishFloat) total).getValue() * ((JavaishInt) val2).getValue());
                    } else {
                        result = new JavaishFloat(((JavaishFloat) total).getValue() * ((JavaishFloat) val2).getValue());
                    }
                } else if(val2 instanceof JavaishFloat){
                    if(total instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishInt) total).getValue() * ((JavaishFloat) val2).getValue());
                    } else {
                    result = new JavaishFloat(((JavaishFloat) total).getValue() * ((JavaishFloat) val2).getValue());
                    }
                } else {
                    result = new JavaishInt(((JavaishInt) total).getValue() * ((JavaishInt) val2).getValue());
                }
                break;
            case DIVIDE:
                if(total instanceof JavaishString || val2 instanceof JavaishString){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(total instanceof JavaishFloat){
                    if(val2 instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishFloat) total).getValue() / ((JavaishInt) val2).getValue());
                    } else {
                        result = new JavaishFloat(((JavaishFloat) total).getValue() / ((JavaishFloat) val2).getValue());
                    }
                } else if(val2 instanceof JavaishFloat){
                    if(total instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishInt) total).getValue() / ((JavaishFloat) val2).getValue());
                    } else {
                    result = new JavaishFloat(((JavaishFloat) total).getValue() / ((JavaishFloat) val2).getValue());
                    }
                } else {
                    result = new JavaishInt(((JavaishInt) total).getValue() / ((JavaishInt) val2).getValue());
                }
                break;
            case MINUS:
                if(total instanceof JavaishString || val2 instanceof JavaishString){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                  if(total instanceof JavaishFloat){
                    if(val2 instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishFloat) total).getValue() - ((JavaishInt) val2).getValue());
                    } else {
                        result = new JavaishFloat(((JavaishFloat) total).getValue() - ((JavaishFloat) val2).getValue());
                    }
                } else if(val2 instanceof JavaishFloat){
                    if(total instanceof JavaishInt){
                        result = new JavaishFloat(((JavaishInt) total).getValue() - ((JavaishFloat) val2).getValue());
                    } else {
                    result = new JavaishFloat(((JavaishFloat) total).getValue() - ((JavaishFloat) val2).getValue());
                    }
                } else {
                    result = new JavaishInt(((JavaishInt) total).getValue() - ((JavaishInt) val2).getValue());
                }
                break;
            default:
                break;
        }
        return result;
    }

    private void evalComment(CommentStmt comment, List<String> javaPrinter){
        String lcomment = comment.getComment();
        System.out.println("Comment: " + lcomment + "TABCOUNT: " + tabCount);
        //Replace first two characters with #
        lcomment = lcomment.substring(2);
        javaPrinter.add(addTabCount() + "#" + lcomment);


    }

    private void evalAssignment(AssignmentStmt assignment, Variables localVariables, boolean isGlobal,List<String> javaPrinter){
        String line = "";
        String name = assignment.getName();
        JavaishVal value = evalExpression(assignment.getValue(), localVariables, isGlobal);
        String expr = translateExpression(assignment.getValue(), localVariables, isGlobal, javaPrinter);
        System.out.println("Assignment: Name:" + name + " Value:" + value.getValue() + "TABCOUNT: " + tabCount);
        line = addTabCount() + name + " = " + expr;
        javaPrinter.add(line);
        if(localVariables.isVariable(name)){
            localVariables.setVariableValue(name, value, lineNumber);
            return;
        }
        globalVariables.setVariableValue(name, value, lineNumber);
       

    }

    private void evalDeclaration(DeclarationStmt declaration,Variables localVariables ,boolean isGlobal,List<String> javaPrinter){
        JavaishType type = declaration.getVarType();
        System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + declaration.getValue() + "TABCOUNT: " + tabCount);
        JavaishVal value = evalExpression(declaration.getValue(), localVariables, isGlobal);
        String expr = translateExpression(declaration.getValue(), localVariables, isGlobal, javaPrinter);
        String typeS = "";
        String line = addTabCount();
        int prevTabCount = tabCount;
       
        if(type != value.getType()){
            if(type == JavaishType.FLOAT && value.getType() == JavaishType.INT){
                typeS = "Float";
                line += declaration.getName() + " = " + expr;
                javaPrinter.add(line);
                if(isGlobal){
                 globalVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()), lineNumber);
                } else {
                    localVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()), lineNumber);
                }
                //System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
                return;
            }
            if(value.getType() == JavaishType.LIST){
                JavaishListVal listVal = (JavaishListVal)value;
                JavaishList list = listVal.getValue();
                if(list.getType() == type){
                    String lType = typeToString(list.getType());
                    //Capitalize first letter


                    line += declaration.getName() + " = " + expr;
                    javaPrinter.add(line);
                    if(isGlobal){
                        globalVariables.addList(declaration.getName(), type, list, lineNumber);
                    } else {
                        localVariables.addList(declaration.getName(), type, list, lineNumber);
                    }
                    tabCount = prevTabCount;
                    return;
                }
            }
          //  System.out.println("Type Mismatch");
            Error.TypeMismatch(type.toString(), value.typeString(), lineNumber);
            return;
        }
        if(type == JavaishType.LIST){
            JavaishListVal listVal = (JavaishListVal)value.getValue();
            JavaishList list = listVal.getValue();

            //Capitalize first letter


            line += declaration.getName() + " = " + expr;
            javaPrinter.add(line);
        } else {
            typeS = typeToString(type);
            line += declaration.getName() + " = " + expr;
            javaPrinter.add(line);
        }
        if(isGlobal){
            if(type == JavaishType.LIST){
                    JavaishListVal listVal = (JavaishListVal)value.getValue();
                    globalVariables.addList(declaration.getName(), type, listVal.getValue(), lineNumber);
                
            } else {
                globalVariables.addVariable(declaration.getName(), type, value, lineNumber);
            }
            
        } else {
            if(type == JavaishType.LIST){
                    JavaishListVal listVal = (JavaishListVal)value.getValue();
                    localVariables.addList(declaration.getName(), type, listVal.getValue(), lineNumber);
                
            } else {
                localVariables.addVariable(declaration.getName(), type, value, lineNumber);
             }
        }
        tabCount = prevTabCount;

        //System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
    }

    //TODO Translate Function(USE INTERPRET FUNCTION)
    private void evalFunction(FunctionStmt function, Variables localVariables, boolean isGlobal){
        String name = function.getName();
        Argument[] args = function.getArgs();
        List<Statements> body = function.getBody();
        if(!isGlobal){
            Error.FunctionNotGlobal(name, lineNumber);
            return;
        }
        
        globalVariables.addFunction(name, body, args, lineNumber);
        if(args == null){
            Error.FunctionNotDeclared(name, lineNumber);
            return;
        }
        List<JavaishVal> paramVals = new ArrayList<JavaishVal>();
        for (Argument arg : args) {
            switch (arg.getType()) {
                case STRING:
                    paramVals.add(new JavaishString(""));
                    break;
                case BOOLEAN:
                    paramVals.add(new JavaishBoolean(false));
                    break;
                case INT:
                    paramVals.add(new JavaishInt(0));
                    break;
                case FLOAT:
                    paramVals.add(new JavaishFloat(0));
                    break;
            
                default:
                    break;
            }
        }
        JavaishVal[] params = paramVals.toArray(new JavaishVal[paramVals.size()]);
        interpretFunction(body, args, params, name, false, true);



    }

    
    private void evalCall(CallStmt call, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        String name = call.getName();
        Expression[] params = call.getParams();
      
        String line = addTabCount() + name + "(";
        for (Expression param : params) {
            if(param == null){
                line += "),";
                continue;
            }
            String translatedParam = translateExpression(param, localVariables, isGlobal, javaPrinter);
            
            line += translatedParam + ", ";
        }
        line = line.substring(0, line.length() - 2);
        line += ")";
        javaPrinter.add(line);


    }

    private void evalIf(IfStmt ifStmt, Variables localVariables, boolean isGlobal, Result pastResult, List<String> javaPrinter){
        Expression condition = ifStmt.getCondition();
        List<Statements> body = ifStmt.getBody();
        String conditionString = translateExpression(condition, localVariables, isGlobal,javaPrinter);
        String line = addTabCount() + "if " + conditionString + ":";
        javaPrinter.add(line);
        tabCount++;
        interpretBody(body, localVariables, false, javaPrinter);
        tabCount--;

           
        

    }

    private void evalElse(ElseStmt elseStmt, Variables localVariables, Result pastResult, List<String> javaPrinter){
        List<Statements> body = elseStmt.getBody();
        String line = " else:";
        line = javaPrinter.get(javaPrinter.size() - 1) + line;
        javaPrinter.remove(javaPrinter.size() - 1);
        javaPrinter.add(line);
        tabCount++;
        interpretBody(body, localVariables, false, javaPrinter);
        tabCount--;

        javaPrinter.add(line);

    }

    private void evalElseIf(ElseIfStmt elseifStmt, Variables localVariables, Result pastResult, boolean isGlobal, List<String> javaPrinter){
        Expression condition = elseifStmt.getCondition();
        List<Statements> body = elseifStmt.getBody();
        String condString = translateExpression(condition, localVariables, isGlobal, javaPrinter);
        String line = " else if " + condString + ":";
        line = javaPrinter.get(javaPrinter.size() - 1) + line;
        javaPrinter.remove(javaPrinter.size() - 1);
        javaPrinter.add(line);
        tabCount++;
        interpretBody(body, localVariables, false, javaPrinter);
        tabCount--;
        javaPrinter.add(line);

    }

    private void evalPrint(PrintStmt printStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        Expression expression = printStmt.getValue();
        String expr = translateExpression(expression, localVariables, isGlobal, javaPrinter);
        String line = addTabCount() + "print(" + expr + ")";
        javaPrinter.add(line);
       

    }

    private void evalWhile(WhileStmt whileStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        Expression condition = whileStmt.getCondition();
        String condString = translateExpression(condition, localVariables, isGlobal, javaPrinter);
        String line = addTabCount() + "while " + condString + ":";

        
        javaPrinter.add(line);
        tabCount++;
        interpretBody(whileStmt.getBody(), localVariables, false, javaPrinter);
        tabCount--;

        javaPrinter.add(line);
            
        

    }

    private void evalForEach(ForEachStmt foreachStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        String tempVarName = foreachStmt.getTempVar();
        String listName = foreachStmt.getListVar();
         JavaishVal listVal = null;
        if(localVariables.isVariable(listName)){
            listVal = localVariables.getVariableValue(listName);
        } else if(globalVariables.isVariable(listName)){
            listVal = globalVariables.getVariableValue(listName);
        } else {
            Error.VariableNotDeclared(listName, lineNumber);
            return;
        }
        if(listVal.getType() != JavaishType.LIST){
            Error.TypeMismatch("List", listVal.typeString(), lineNumber);
            return;
        }
       
        JavaishListVal listLVal = (JavaishListVal) listVal;
        JavaishList list = listLVal.getValue();
        
        if(list.getType() == JavaishType.STRINGLIST){
            //Create temp variable
            if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.STRING, new JavaishString(""), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.STRING, new JavaishString(""), lineNumber);
            }
            String line = addTabCount() + "for " + tempVarName + " in " + listName + ":";
            javaPrinter.add(line);
            tabCount++;
            interpretBody(foreachStmt.getBody(), localVariables, false, javaPrinter);
            tabCount--;
            javaPrinter.add(line);


        } else if(list.getType() == JavaishType.BOOLEANLIST){
             if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.BOOLEAN, new JavaishBoolean(false), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.BOOLEAN, new JavaishBoolean(false), lineNumber);
            }
            String line = addTabCount() + "for " + tempVarName + " in " + listName + ":";
            javaPrinter.add(line);
            tabCount++;
            interpretBody(foreachStmt.getBody(), localVariables, false, javaPrinter);
            tabCount--;

            javaPrinter.add(line);
        } else if(list.getType() == JavaishType.INTLIST){
           if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            }
            String line = addTabCount() + "for " + tempVarName + " in " + listName + ":";
            javaPrinter.add(line);
            tabCount++;
            interpretBody(foreachStmt.getBody(), localVariables, false, javaPrinter);
            tabCount--;

            javaPrinter.add(line);
        } else if(list.getType() == JavaishType.FLOATLIST){
             if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.FLOAT, new JavaishFloat(0), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.FLOAT, new JavaishFloat(0), lineNumber);
            }
            String line = addTabCount() + "for " + tempVarName + " in " + listName + ":";
            javaPrinter.add(line);
            tabCount++;
            interpretBody(foreachStmt.getBody(), localVariables, false, javaPrinter);
            tabCount--;

            javaPrinter.add(line);
        } else {
            Error.TypeMismatch("List", listVal.typeString(), lineNumber);
            return;
        }

    }

    private void evalForWhen(ForWhenStmt forwhenStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        Error.PythonForWhenTranslator("Can't convert for when loop to python, must use: for x in [...]", lineNumber);
        
        
    }

    private void evalShowMsgBox(ShowMsgBoxStmt showMsgStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        Expression expression = showMsgStmt.getValue();
        JavaishVal value = evalExpression(expression, localVariables, isGlobal);
        if(value == null){
            return;
        }
       // JOptionPane.showMessageDialog(null, value.getValue());
        if(!usedMessageBox){
            usedMessageBox = true;
            // javaImports.add("import tkMessageBox" );
            // javaImports.add("import Tkinter");
            
        }
        // javaPrinter.add(addTabCount() + "Tkinter.Tk().withdraw()");
        String line = addTabCount() + "print(" + translateExpression(expression, localVariables, isGlobal, javaPrinter) + ")";
        javaPrinter.add(line);
    }

    private String evalShowInputBox(ShowInputBoxElmt showInputBoxElmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        System.out.println("ShowInputBox");
        String value = "";
        Expression expression = showInputBoxElmt.getValue();
        if(expression != null){
            value = translateExpression(expression, localVariables, isGlobal, javaPrinter);
        }

        String inputLine = "input(" + value + ")";
        System.out.println(inputLine);
        return inputLine;


    }

    private void evalReturn(ReturnStmt returnStmt, Variables localVariables, Return returnVal, boolean isGlobal, List<String> javaPrinter){
        Expression expression = returnStmt.getValue();
        JavaishVal value = null;
        String expr = "";
        if(returnStmt.hasReturn()){
            value = evalExpression(expression, localVariables, isGlobal);
            expr = " " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
            
        }
        returnVal.setHasReturn(true);
        returnVal.setValue(value);
        String line = addTabCount() + "return" + expr;
        javaPrinter.add(line);

        
    }

    private void evalMutation(MutationStmt mutationStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        MutationType type = mutationStmt.getMutationType();
        String name = mutationStmt.getVarName();
        JavaishType varType = globalVariables.getVariableType(name);
        JavaishVal value = evalExpression(mutationStmt.getValue(), localVariables, isGlobal);
        if(varType == JavaishType.STRINGLIST || varType == JavaishType.BOOLEANLIST || varType == JavaishType.INTLIST || varType == JavaishType.FLOATLIST){
            
            JavaishList varList = null;
            if(localVariables.isVariable(name)){
                varList = localVariables.getList(name).getValue();
            } else {
                varList = globalVariables.getList(name).getValue();
            }
            if(varList == null){
                Error.VariableNotDeclared(name, lineNumber);
                return;
            }
            JavaishList list = performListOperation(mutationTypeToOperator(type), varList, value, 0);
            String line = addTabCount() + name + ".append(" + translateExpression(mutationStmt.getValue(), localVariables, isGlobal, javaPrinter) + ")";
            javaPrinter.add(line);
            if(localVariables.isVariable(name)){
                
                localVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
                return;
            }
            globalVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
            return;
        }

        JavaishVal variable = null;
        if(localVariables.isVariable(name)){
            variable = localVariables.getVariableValue(name);
        } else {
            variable = globalVariables.getVariableValue(name);
        }
        if(variable == null){
            Error.VariableNotDeclared(name, lineNumber);
            return;
        }
        if(variable.getType() == JavaishType.STRING){
            if(type != MutationType.ADD){
                Error.CantPerformMutation(variable.typeString(), lineNumber);
                return;
            }
        }
        Expression expression = mutationStmt.getValue();
        boolean exprJustOne = false;
        Element[] elements = expression.getElements();
        if(elements.length == 1){
            if(elements[0].getType() == ElementType.INTEGER){
                IntElmt integer = (IntElmt) elements[0];
                if(integer.getValue() == 1){
                    exprJustOne = true;
                }
                
            }
        }
        String line = addTabCount();
     
    
        
        switch (type) {
            case ADD:
                if(exprJustOne){
                    line += name + "++";
                } else {
                    line += name + " += " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                }
                
                break;

            case SUBTRACT:
                if(exprJustOne){
                    line += name + "--";
                } else {
                    line += name + " -= " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                }
                break;

            case MULTIPLY:
                if(exprJustOne){
                    line += name + " *= " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                } else {
                    line += name + " *= " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                }
                break;
            
            case DIVIDE:
                if(exprJustOne){
                    line += name + " /= " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                } else {
                    line += name + " /= " + translateExpression(expression, localVariables, isGlobal, javaPrinter);
                }
                break;
        
            default:
                break;
            
        }
        javaPrinter.add(line);
            
        JavaishVal newVal = performOperation(mutationTypeToOperator(type), variable, value);
                if(localVariables.isVariable(name)){
        localVariables.setVariableValue(name, newVal, lineNumber);
        return;
        }
        globalVariables.setVariableValue(name, newVal, lineNumber);
    
  

    }

    private void evalRemoveAt(RemoveAtStmt removeAtStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        String name = removeAtStmt.getListName();
        JavaishType varType = globalVariables.getVariableType(name);
        JavaishVal index = evalExpression(removeAtStmt.getLocation(), localVariables, isGlobal);
        if(varType == JavaishType.STRINGLIST || varType == JavaishType.BOOLEANLIST || varType == JavaishType.INTLIST || varType == JavaishType.FLOATLIST){
            
            JavaishList varList = null;
            if(localVariables.isVariable(name)){
                varList = localVariables.getList(name).getValue();
            } else {
                varList = globalVariables.getList(name).getValue();
            }
            if(varList == null){
                Error.VariableNotDeclared(name, lineNumber);
                return;
            }
            if(index.getType() != JavaishType.INT){
                Error.TypeMismatch("Int", index.typeString(), lineNumber);
                return;
            }
            int indexVal = ((JavaishInt) index).getValue();
            JavaishList list = performListOperation(Operator.REMOVEAT, varList, null, indexVal);

            String line = addTabCount() + name + ".pop(" + indexVal + ")";
            javaPrinter.add(line);

            if(localVariables.isVariable(name)){
                
                localVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
                return;
            }
            globalVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
            return;
        }
        Error.TypeMismatch("List", varType.toString(), lineNumber);
        return;
    }

    private void evalRemoveFrom(RemoveFromStmt removeFromStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        String name = removeFromStmt.getListName();
        JavaishType varType = globalVariables.getVariableType(name);
        JavaishVal value = evalExpression(removeFromStmt.getValue(), localVariables, isGlobal);
        if(varType == JavaishType.STRINGLIST || varType == JavaishType.BOOLEANLIST || varType == JavaishType.INTLIST || varType == JavaishType.FLOATLIST){
            
            JavaishList varList = null;
            if(localVariables.isVariable(name)){
                varList = localVariables.getList(name).getValue();
            } else {
                varList = globalVariables.getList(name).getValue();
            }
            if(varList == null){
                Error.VariableNotDeclared(name, lineNumber);
                return;
            }
            JavaishList list = performListOperation(Operator.REMOVEFROM, varList, value, 0);
            System.out.println("LIST: " + list.listString());
           
            if(varType == JavaishType.INTLIST){
                String line = addTabCount() + name + ".remove(" + translateExpression(removeFromStmt.getValue(), localVariables, isGlobal, javaPrinter) + ")";
                javaPrinter.add(line);
            } else {
                String line = addTabCount() + name + ".remove(" + translateExpression(removeFromStmt.getValue(), localVariables, isGlobal, javaPrinter) + ")";
                javaPrinter.add(line);
            }
          
            if(localVariables.isVariable(name)){
                
                localVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
                return;
            }
            globalVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
            return;
        }
    }

    private void evalRemoveAllFrom(RemoveAllFromStmt removeAllFromStmt, Variables localVariables, boolean isGlobal, List<String> javaPrinter){
        String name = removeAllFromStmt.getListName();
        JavaishType varType = globalVariables.getVariableType(name);
        JavaishVal value = evalExpression(removeAllFromStmt.getValue(), localVariables, isGlobal);
        if(varType == JavaishType.STRINGLIST || varType == JavaishType.BOOLEANLIST || varType == JavaishType.INTLIST || varType == JavaishType.FLOATLIST){
            
            JavaishList varList = null;
            if(localVariables.isVariable(name)){
                varList = localVariables.getList(name).getValue();
            } else {
                varList = globalVariables.getList(name).getValue();
            }
            if(varList == null){
                Error.VariableNotDeclared(name, lineNumber);
                return;
            }
            JavaishList list = performListOperation(Operator.REMOVEALLFROM, varList, value, 0);
            System.out.println("LIST: " + list.listString());

            String line = addTabCount() + name + " = [i for i in " + name + " if i != " + translateExpression(removeAllFromStmt.getValue(), localVariables, isGlobal, javaPrinter) + "]";
            javaPrinter.add(line);
            if(localVariables.isVariable(name)){
                
                localVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
                return;
            }
            globalVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
            return;
        }
    }

    private JavaishList performListOperation(Operator operation, JavaishList list, JavaishVal val, int index){
        JavaishList result = null;
        switch (operation) {
            case PLUS:
                JavaishType listInnerType = list.getInnerType();
                JavaishType valType = val.getType();
                if(listInnerType != valType){
                    Error.TypeMismatch(listInnerType.toString(), valType.toString(), lineNumber);
                    return null;
                }
                if(list.getType() == JavaishType.BOOLEANLIST){
                    JavaishBooleanList booleanList = (JavaishBooleanList) list;
                    List<JavaishBoolean> booleanListVal = booleanList.getList();
                    JavaishBoolean booleanVal = (JavaishBoolean) val;
                    booleanListVal.add(booleanVal);
                    result = new JavaishBooleanList(booleanListVal);
                    
                } else if(list.getType() == JavaishType.FLOATLIST){
                    JavaishFloatList floatList = (JavaishFloatList) list;
                    List<JavaishFloat> floatListVal = floatList.getList();
                    if(val.getType() == JavaishType.INT){
                        JavaishInt intVal = (JavaishInt) val;
                        floatListVal.add(new JavaishFloat(intVal.getValue()));
                    } else {
                        JavaishFloat floatVal = (JavaishFloat) val;
                        floatListVal.add(floatVal);
                    }
                    result = new JavaishFloatList(floatListVal);
                } else if(list.getType() == JavaishType.INTLIST){
                    JavaishIntList intList = (JavaishIntList) list;
                    List<JavaishInt> intListVal = intList.getList();
                    JavaishInt intVal = (JavaishInt) val;
                    intListVal.add(intVal);
                    result = new JavaishIntList(intListVal);
                } else if(list.getType() == JavaishType.STRINGLIST){
                    JavaishStringList stringList = (JavaishStringList) list;
                    List<JavaishString> stringListVal = stringList.getList();
                    JavaishString stringVal = (JavaishString) val;
                    stringListVal.add(stringVal);
                    result = new JavaishStringList(stringListVal);
                }

                
                break;
            case REMOVEAT:
                if(list.getType() == JavaishType.BOOLEANLIST){
                    JavaishBooleanList booleanList = (JavaishBooleanList) list;
                    List<JavaishBoolean> booleanListVal = booleanList.getList();
                    
                    booleanListVal.remove(index);
                    result = new JavaishBooleanList(booleanListVal);
                    
                } else if(list.getType() == JavaishType.FLOATLIST){
                    JavaishFloatList floatList = (JavaishFloatList) list;
                    List<JavaishFloat> floatListVal = floatList.getList();
                    
                    floatListVal.remove(index);
                    result = new JavaishFloatList(floatListVal);
                } else if(list.getType() == JavaishType.INTLIST){
                    JavaishIntList intList = (JavaishIntList) list;
                    List<JavaishInt> intListVal = intList.getList();
                    
                    intListVal.remove(index);
                    result = new JavaishIntList(intListVal);
                } else if(list.getType() == JavaishType.STRINGLIST){
                    JavaishStringList stringList = (JavaishStringList) list;
                    List<JavaishString> stringListVal = stringList.getList();
                    
                    stringListVal.remove(index);
                    result = new JavaishStringList(stringListVal);
                }
                break;
            case REMOVEFROM:
                if(list.getType() == JavaishType.BOOLEANLIST){
                    JavaishBooleanList booleanList = (JavaishBooleanList) list;
                    List<JavaishBoolean> booleanListVal = booleanList.getList();
                    JavaishBoolean booleanVal = (JavaishBoolean) val;
                    for(int i = 0; i < booleanListVal.size(); i++){
                        if(booleanListVal.get(i).getValue() == booleanVal.getValue()){
                            booleanListVal.remove(i);
                            break;
                        }
                    }
                    result = new JavaishBooleanList(booleanListVal);
                    
                } else if(list.getType() == JavaishType.FLOATLIST){
                    JavaishFloatList floatList = (JavaishFloatList) list;
                    List<JavaishFloat> floatListVal = floatList.getList();
                    JavaishFloat floatVal = null;
                    if(val.getType() == JavaishType.INT){
                        JavaishInt intVal = (JavaishInt) val;
                        floatVal = new JavaishFloat(intVal.getValue());
                        
                    } else {
                        floatVal = (JavaishFloat) val;
                    }
                    for(int i = 0; i < floatListVal.size(); i++){
                        if(floatListVal.get(i).getValue() == floatVal.getValue()){
                            floatListVal.remove(i);
                            break;
                        }
                    }
                    result = new JavaishFloatList(floatListVal);
                } else if(list.getType() == JavaishType.INTLIST){
                    JavaishIntList intList = (JavaishIntList) list;
                    List<JavaishInt> intListVal = intList.getList();
                    JavaishInt intVal = (JavaishInt) val;
                    for(int i = 0; i < intListVal.size(); i++){
                        if(intListVal.get(i).getValue() == intVal.getValue()){
                            intListVal.remove(i);
                            break;
                        }
                    }
                    result = new JavaishIntList(intListVal);
                    System.out.println("REMOVE FROM: " + intVal.getType() + " " + intListVal.size());
                } else if(list.getType() == JavaishType.STRINGLIST){
                    JavaishStringList stringList = (JavaishStringList) list;
                    List<JavaishString> stringListVal = stringList.getList();
                    JavaishString stringVal = (JavaishString) val;
                    for(int i = 0; i < stringListVal.size(); i++){
                        if(stringListVal.get(i).getValue().equals(stringVal.getValue())){
                            stringListVal.remove(i);
                            break;
                        }
                    }
                    result = new JavaishStringList(stringListVal);
                }
                break;
            case REMOVEALLFROM:
                if(list.getType() == JavaishType.BOOLEANLIST){
                    JavaishBooleanList booleanList = (JavaishBooleanList) list;
                    List<JavaishBoolean> booleanListVal = booleanList.getList();
                    JavaishBoolean booleanVal = (JavaishBoolean) val;
                    //Remove all with same val using for loop
                    for(int i = 0; i < booleanListVal.size(); i++){
                        if(booleanListVal.get(i).getValue() == booleanVal.getValue()){
                            booleanListVal.remove(i);
                            i--;
                        }
                    }
                    result = new JavaishBooleanList(booleanListVal);
                    
                } else if(list.getType() == JavaishType.FLOATLIST){
                    JavaishFloatList floatList = (JavaishFloatList) list;
                    List<JavaishFloat> floatListVal = floatList.getList();
                    JavaishFloat floatVal = null;
                    if(val.getType() == JavaishType.INT){
                        JavaishInt intVal = (JavaishInt) val;
                        floatVal = new JavaishFloat(intVal.getValue());
                    } else {
                        floatVal = (JavaishFloat) val;
                    }
                    for(int i = 0; i < floatListVal.size(); i++){
                        if(floatListVal.get(i).getValue() == floatVal.getValue()){
                            floatListVal.remove(i);
                            i--;
                        }
                    }
                    result = new JavaishFloatList(floatListVal);
                } else if(list.getType() == JavaishType.INTLIST){
                    JavaishIntList intList = (JavaishIntList) list;
                    List<JavaishInt> intListVal = intList.getList();
                    JavaishInt intVal = (JavaishInt) val;
                    for(int i = 0; i < intListVal.size(); i++){
                        if(intListVal.get(i).getValue() == intVal.getValue()){
                            intListVal.remove(i);
                            i--;
                        }
                    }
                    result = new JavaishIntList(intListVal);
                } else if(list.getType() == JavaishType.STRINGLIST){
                    JavaishStringList stringList = (JavaishStringList) list;
                    List<JavaishString> stringListVal = stringList.getList();
                    JavaishString stringVal = (JavaishString) val;
                    for(int i = 0; i < stringListVal.size(); i++){
                        if(stringListVal.get(i).getValue().equals(stringVal.getValue())){
                            stringListVal.remove(i);
                            i--;
                        }
                    }
                    result = new JavaishStringList(stringListVal);
                }
                break;
        
            default:
                Error.CantPerformOperation(operation.toString(), list.typeString(),lineNumber);
                break;
        }
        return result;
    }

    private Operator mutationTypeToOperator(MutationType type){
        switch (type) {
            case ADD:
                return Operator.PLUS;
            case SUBTRACT:
                return Operator.MINUS;
            case MULTIPLY:
                return Operator.MULTIPLY;
            case DIVIDE:
                return Operator.DIVIDE;
            default:
                return null;
        }
    }

    private String typeToString(JavaishType value){
        switch (value) {
            case BOOLEAN:
                return "boolean";
            case FLOAT:
                return "float";
            case INT:
                return "int";
            case STRING:
                return "String";
            case LIST:
                return "List";
            case BOOLEANLIST:
                return "boolean";
            case FLOATLIST:
                return "float";
            case INTLIST:
                return "int";
            case STRINGLIST:
                return "String";
            default:
                return null;
        }
    }

    private String addTabCount(){
        String tabs = "";
        for(int i = 0; i < tabCount; i++){
            tabs += "\t";
        }
        return tabs;
    }


}