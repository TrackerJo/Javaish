package com.trackerjo.javaish;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.MutationType;



public class Interpreter {
   int lineNumber = 0;
   
   
   
    Variables globalVariables;
    enum Operator {
        PLUS, MINUS, DIVIDE, MULTIPLY, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, REMOVEALLFROM, REMOVEAT, REMOVEFROM
    }
    public Interpreter( Variables variables){
        
        this.globalVariables = variables;
   
    }

    public JavaishVal interpretFunction(List<Statements> statements,  Argument[] args,  JavaishVal[] params, String name, boolean isGlobal){
        Variables localVariables = new Variables();
        //System.out .println("Interpreting Function: " + name);
        
        
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
                    if(val.getType() == JavaishType.LIST ){
                        //Check if list type is correct and if so add list to local variables using localVariables.addList
                        if( val instanceof JavaishListVal){
                            JavaishListVal listVal = (JavaishListVal) val;
                            JavaishList list = listVal.getValue();
                            if(listVal.getValue().getType() != arg.getType()){
                                Error.ArgumentTypeMismatch(name, lineNumber, arg.getType().toString(), val.typeString());
                                return null;
                            }
                            localVariables.addList(arg.getName(), list.getType(),list, lineNumber);
                            continue;
                        }

                    }
                    Error.ArgumentTypeMismatch(name, lineNumber, arg.getType().toString(), val.typeString());
                    return null;
                }
                localVariables.addVariable(arg.getName(), arg.getType(), val, lineNumber);
                
            }
        }
        Return returnVal = new Return(false, null);
        interpretBody(statements, localVariables, isGlobal, returnVal);
        if(returnVal.hasReturn()){
            return returnVal.getValue();
        }
        return null;

        
        
    }

    private JavaishVal interpretBody(List<Statements> statements,Variables funcVariables, boolean isGlobal, Return returnVal){
        Result pastResult = new Result(false);  
        Variables localVariables = funcVariables.clone();

        

        for (Statements statement : statements) {
        
            if(returnVal.hasReturn()){
                // System.out.println("InSIDE Return: " + returnVal.getValue().getValue());
                
                return returnVal.getValue();
            }
            interpretStmt(statement, localVariables, isGlobal, pastResult, returnVal);
            
            
        }
        // System.out.println("Should RETURNING: " +returnVal.hasReturn());
        if(returnVal.hasReturn()){
            // System.out.println("RETURNING: " + returnVal.getValue().getValue());
            return returnVal.getValue();
        }
        return null;
    }

    private void interpretStmt(Statements stmt, Variables localVariables, boolean isGlobal, Result pastResult, Return returnVal){
       // System.out.println("Interpreting Stmt Type: " + stmt.getType());
        lineNumber = stmt.getLine();
        switch (stmt.getType()) {
            case COMMENT:
                break;
            case ASSIGNMENT:
                AssignmentStmt assignment = (AssignmentStmt) stmt;
                evalAssignment(assignment, localVariables, isGlobal);

                
                break;
            case CALL:
                CallStmt call = (CallStmt) stmt;
                evalCall(call, localVariables, isGlobal);
                break;
            case DECLARATION:
                DeclarationStmt declaration = (DeclarationStmt) stmt;
                evalDeclaration(declaration, localVariables, isGlobal);
                break;
            case ELSE:
                ElseStmt elseStmt = (ElseStmt) stmt;
                evalElse(elseStmt, localVariables, pastResult, returnVal);
                break;
            case ELSEIF:
                ElseIfStmt elseifStmt = (ElseIfStmt) stmt;
                evalElseIf(elseifStmt, localVariables, pastResult, isGlobal, returnVal);
                break;
            case MUTATION:
                MutationStmt mutationStmt = (MutationStmt) stmt;
                evalMutation(mutationStmt, localVariables, isGlobal);
                break;
            case RETURN:
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                evalReturn(returnStmt, localVariables, returnVal, isGlobal);
                break;
            case FUNCTION:
                FunctionStmt function = (FunctionStmt) stmt;
                evalFunction(function, localVariables, isGlobal);
                break;
            case IF:
                IfStmt ifStmt = (IfStmt) stmt;
                evalIf(ifStmt, localVariables, isGlobal, pastResult, returnVal);
                break;
            case WHILE:
                WhileStmt whileStmt = (WhileStmt) stmt;
                evalWhile(whileStmt, localVariables, isGlobal);
                break;
            case FOREACH:
                ForEachStmt foreachStmt = (ForEachStmt) stmt;
                evalForEach(foreachStmt, localVariables, returnVal, isGlobal);
                break;
            case FORWHEN:
                ForWhenStmt forwhenStmt = (ForWhenStmt) stmt;
                evalForWhen(forwhenStmt, localVariables, isGlobal);
                break;
            case PRINT:
                PrintStmt printStmt = (PrintStmt) stmt;
                evalPrint(printStmt, localVariables, isGlobal);
                break;
            case SHOWMSGBOX:
                ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) stmt;
                evalShowMsgBox(showMsgBoxStmt, localVariables, isGlobal);
                break;
            case REMOVEAT:
                RemoveAtStmt removeAtStmt = (RemoveAtStmt) stmt;
                evalRemoveAt(removeAtStmt, localVariables, isGlobal);
                break;
            case REMOVEFROM:
                RemoveFromStmt removeFromStmt = (RemoveFromStmt) stmt;
                evalRemoveFrom(removeFromStmt, localVariables, isGlobal);
                break;
            case REMOVEALLFROM:
                RemoveAllFromStmt removeAllFromStmt = (RemoveAllFromStmt) stmt;
                evalRemoveAllFrom(removeAllFromStmt, localVariables, isGlobal);
                break;
            case SET:
                SetStmt setStmt = (SetStmt) stmt;
                evalSet(setStmt, localVariables, isGlobal);
                break;

            default:
                break;
        }

    }

    private JavaishVal evalExpression(Expression expression, Variables localVariables, boolean isGlobal){
        JavaishVal total = null;
        Operator operation = null;
        Operator comparison = null;
        JavaishVal compVal = null;
        boolean isComp = false;
        
        
        for(Element elmt : expression.getElements()){
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
                            if(!(val instanceof JavaishInt)){
                                try {
                                    if(val instanceof JavaishFloat){
                                       val = new JavaishInt(Math.round(((JavaishFloat) val).getValue()));
                                    } else if(val instanceof JavaishString){
                                        val =  new JavaishInt(Integer.parseInt(((JavaishString) val).getValue()));
                                    } 
                                } catch (Exception e) {
                                    Error.UnableToParse("Int", lineNumber, val.typeString());
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
                    
                    JavaishVal valFunc = interpretFunction(body, args, paramValsArr, function.getName(), false);
                    if(isComp){
                        compVal = performOperation(operation, compVal, valFunc);
                    } else {
                        total = performOperation(operation, total, valFunc);
                    }
                    break;
                case SHOWINPUTBOX:
                    ShowInputBoxElmt showInputBox = (ShowInputBoxElmt) elmt;
                    JavaishString input = evalShowInputBox(showInputBox, localVariables, isGlobal);
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
                  //  System.out.println("EQUAL: " + ((JavaishString) left).getValue() + " " + ((JavaishString) right).getValue());
                    if(((JavaishString) left).getValue().equals(((JavaishString) right).getValue())){
                     //   System.out.println("EQUAL: TRUE");
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

    private void evalSet(SetStmt set, Variables localVariables, boolean isGlobal){
        String name = set.getName();
        JavaishVal value = evalExpression(set.getValue(), localVariables, isGlobal);
        ListValElmt listVal = set.getListVal();
        JavaishVal index = evalExpression(listVal.getIndex(), localVariables, isGlobal);
        JavaishInt indexInt = null;
        if(index instanceof JavaishInt){
            indexInt = (JavaishInt) index;
        } else {
            Error.TypeMismatch("Int", index.typeString(), lineNumber);
            return;
        }

        if(localVariables.isVariable(name)){
            JavaishVal val = localVariables.getVariableValue(name);
            if(val instanceof JavaishListVal){
               localVariables.setListValue(name, indexInt, value, lineNumber);
                return;
            }
            Error.TypeMismatch("List", val.typeString(), lineNumber);
            return;
        } else if(globalVariables.isVariable(name)){
            JavaishVal val = globalVariables.getVariableValue(name);
            if(val instanceof JavaishListVal){
                globalVariables.setListValue(name, indexInt, value, lineNumber);
                 return;
            }
            Error.TypeMismatch("List", val.typeString(), lineNumber);
            return;
        }


    }

        

    private void evalAssignment(AssignmentStmt assignment, Variables localVariables, boolean isGlobal){
        String name = assignment.getName();
        JavaishVal value = evalExpression(assignment.getValue(), localVariables, isGlobal);
        if(localVariables.isVariable(name)){
            localVariables.setVariableValue(name, value, lineNumber);
            return;
        }
        globalVariables.setVariableValue(name, value, lineNumber);
       

    }

    private void evalDeclaration(DeclarationStmt declaration,Variables localVariables ,boolean isGlobal){
        JavaishType type = declaration.getVarType();
       // System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + declaration.getValue());
        JavaishVal value = evalExpression(declaration.getValue(), localVariables, isGlobal);

        if(type != value.getType()){
            if(type == JavaishType.FLOAT && value.getType() == JavaishType.INT){
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
                    if(isGlobal){
                        globalVariables.addList(declaration.getName(), type, list, lineNumber);
                    } else {
                        localVariables.addList(declaration.getName(), type, list, lineNumber);
                    }
                    return;
                }
            }
          //  System.out.println("Type Mismatch");
            Error.TypeMismatch(type.toString(), value.typeString(), lineNumber);
            return;
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
        //System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
    }

    private void evalFunction(FunctionStmt function, Variables localVariables, boolean isGlobal){
        String name = function.getName();
        Argument[] args = function.getArgs();
        List<Statements> body = function.getBody();
        if(!isGlobal){
            Error.FunctionNotGlobal(name, lineNumber);
            return;
        }
        
        globalVariables.addFunction(name, body, args, lineNumber);



    }

    private void evalCall(CallStmt call, Variables localVariables, boolean isGlobal){
        String name = call.getName();
        Expression[] params = call.getParams();
        if(params == null){
            params = new Expression[0];
        }
        if(params[0] == null){
            params = new Expression[0];
        }
        if(globalVariables.functionExists(name) == false){
            Error.FunctionNotDeclared(name, lineNumber);
            return;
        }
        Argument[] args = globalVariables.getFunctionArgs(name);
        List<Statements> body = globalVariables.getFunctionBody(name);
        if(args == null){
            Error.FunctionNotDeclared(name, lineNumber);
            return;
        }
        List<JavaishVal> paramVals = new ArrayList<JavaishVal>();
        //System.out .println("PARAMS: " + params.length);
        for (Expression param : params) {
            //System.out .println("PARAM: " + param);
            paramVals.add(evalExpression(param, localVariables, isGlobal));
        }
        JavaishVal[] paramValsArr = paramVals.toArray(new JavaishVal[paramVals.size()]);

        interpretFunction(body, args, paramValsArr, name, false);

    }

    private void evalIf(IfStmt ifStmt, Variables localVariables, boolean isGlobal, Result pastResult, Return cReturn){
        Expression condition = ifStmt.getCondition();
        List<Statements> body = ifStmt.getBody();
        JavaishBoolean result = (JavaishBoolean) evalExpression(condition, localVariables, isGlobal);
        if(result.getValue() == true){
            pastResult.setResult(true);
            interpretBody(body, localVariables, false, cReturn);
           
        } else {
            pastResult.setResult(false);
        }

    }

    private void evalElse(ElseStmt elseStmt, Variables localVariables, Result pastResult, Return cReturn){
        List<Statements> body = elseStmt.getBody();
        if(pastResult.getResult() == true){
            return;
        }
        interpretBody(body, localVariables, false, cReturn);

    }

    private void evalElseIf(ElseIfStmt elseifStmt, Variables localVariables, Result pastResult, boolean isGlobal, Return cReturn){
        Expression condition = elseifStmt.getCondition();
        List<Statements> body = elseifStmt.getBody();
        JavaishBoolean result = (JavaishBoolean) evalExpression(condition, localVariables, isGlobal);
        //System.out.println("EVAL ELSE IF: " + result.getValue() + " PASSED: " + pastResult.getResult());
        if(pastResult.getResult() == true){
            return;
        }
        if(result.getValue() == true){
           
            pastResult.setResult(true);
            interpretBody(body, localVariables, false, cReturn);
        }

    }

    private void evalPrint(PrintStmt printStmt, Variables localVariables, boolean isGlobal){
        Expression expression = printStmt.getValue();
        JavaishVal value = evalExpression(expression, localVariables, isGlobal);
        if(value == null){
            return;
        }
        System.out.println(value.getValue());

    }

    private void evalWhile(WhileStmt whileStmt, Variables localVariables, boolean isGlobal){
        Expression condition = whileStmt.getCondition();
        JavaishVal result = evalExpression(condition, localVariables, isGlobal);
        if(result == null){
            return;
        }
        while(((JavaishBoolean) result).getValue() == true){
            interpretBody(whileStmt.getBody(), localVariables, false, new Return(false, null));
            result = evalExpression(condition, localVariables, isGlobal);
            if(result == null){
                return;
            }
        }

    }

    private void evalForEach(ForEachStmt foreachStmt, Variables localVariables,Return returnVal, boolean isGlobal){
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
            JavaishStringList stringList = (JavaishStringList) list;
            List<JavaishString> listVals = stringList.getList();
            if(listVals == null){
                Error.ListEmpty(lineNumber, listName);
                return;
            }
            //Create temp variable
            if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.STRING, new JavaishString(""), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.STRING, new JavaishString(""), lineNumber);
            }
            for (JavaishString listValI : listVals) {
                if(localVariables.isVariable(tempVarName)){
                    localVariables.setVariableValue(tempVarName, listValI, lineNumber);
                } else {
                    globalVariables.setVariableValue(tempVarName, listValI, lineNumber);
                }
                Return newReturn = new Return(false, null);
                interpretBody(foreachStmt.getBody(), localVariables, isGlobal, newReturn);
               // System.out.println("RETURN VAL: " + newReturn.hasReturn());
                if(newReturn.hasReturn()){
                    
                    returnVal.setHasReturn(true);
                    returnVal.setValue(newReturn.getValue());
                }
            }

        } else if(list.getType() == JavaishType.BOOLEANLIST){
            JavaishBooleanList booleanList = (JavaishBooleanList) list;
            List<JavaishBoolean> listVals = booleanList.getList();
            if(listVals == null){
                Error.ListEmpty(lineNumber, listName);
                return;
            }
            if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.BOOLEAN, new JavaishBoolean(false), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.BOOLEAN, new JavaishBoolean(false), lineNumber);
            }
            for (JavaishBoolean listValI : listVals) {
                if(localVariables.isVariable(tempVarName)){
                    localVariables.setVariableValue(tempVarName, listValI, lineNumber);
                } else {
                    globalVariables.setVariableValue(tempVarName, listValI, lineNumber);
                }
                interpretBody(foreachStmt.getBody(), localVariables, false, new Return(false, null));
            }
        } else if(list.getType() == JavaishType.INTLIST){
            JavaishIntList intList = (JavaishIntList) list;
            List<JavaishInt> listVals = intList.getList();
            if(listVals == null){
                Error.ListEmpty(lineNumber, listName);
                return;
            }
            if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            }
            for (JavaishInt listValI : listVals) {
                if(localVariables.isVariable(tempVarName)){
                    localVariables.setVariableValue(tempVarName, listValI, lineNumber);
                } else {
                    globalVariables.setVariableValue(tempVarName, listValI, lineNumber);
                }
                interpretBody(foreachStmt.getBody(), localVariables, false, new Return(false, null));
            }
        } else if(list.getType() == JavaishType.FLOATLIST){
            JavaishFloatList floatList = (JavaishFloatList) list;
            List<JavaishFloat> listVals = floatList.getList();
            if(listVals == null){
                Error.ListEmpty(lineNumber, listName);
                return;
            }
            if(isGlobal){
                globalVariables.addVariable(tempVarName, JavaishType.FLOAT, new JavaishFloat(0), lineNumber);
            } else {
                localVariables.addVariable(tempVarName, JavaishType.FLOAT, new JavaishFloat(0), lineNumber);
            }
            for (JavaishFloat listValI : listVals) {
                if(localVariables.isVariable(tempVarName)){
                    localVariables.setVariableValue(tempVarName, listValI, lineNumber);
                } else {
                    globalVariables.setVariableValue(tempVarName, listValI, lineNumber);
                }
                interpretBody(foreachStmt.getBody(), localVariables, false, new Return(false, null));
            }
        } else {
            Error.TypeMismatch("List", listVal.typeString(), lineNumber);
            return;
        }

    }

    private void evalForWhen(ForWhenStmt forwhenStmt, Variables localVariables, boolean isGlobal){
        String incVarName = forwhenStmt.getIncVar();
        if(isGlobal){
            if(!globalVariables.isVariable(incVarName)){
                globalVariables.addVariable(incVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            }
        } else {
            
            if(!localVariables.isVariable(incVarName) && !globalVariables.isVariable(incVarName)){
                localVariables.addVariable(incVarName, JavaishType.INT, new JavaishInt(0), lineNumber);
            }
        }
        Expression condition = forwhenStmt.getCondition();
        JavaishVal result = evalExpression(condition, localVariables, isGlobal);
        if(result == null){
            return;
        }
        while(((JavaishBoolean) result).getValue() == true){
            interpretBody(forwhenStmt.getBody(), localVariables, false, new Return(false, null));
            JavaishVal incVal = null;
            if(isGlobal){
                incVal = globalVariables.getVariableValue(incVarName);
            } else {
                //Check if variable is local or global
                if(localVariables.isVariable(incVarName)){
                    incVal = localVariables.getVariableValue(incVarName);
                } else {
                    incVal = globalVariables.getVariableValue(incVarName);
                }
            }
            if(incVal == null){
                return;
            }
            
            Expression incExpression = forwhenStmt.getIncrement();
            JavaishVal incVal2 = evalExpression(incExpression, localVariables, isGlobal);
            JavaishVal incResult =  performOperation(Operator.PLUS, incVal, incVal2);
            if(incResult.getType() != JavaishType.INT){
                Error.TypeMismatch("Int", incResult.typeString(), lineNumber);
                return;
            }
            JavaishInt incInt = (JavaishInt) incResult;
            if(isGlobal){
                globalVariables.setVariableValue(incVarName, incInt, lineNumber);
            } else {
                //Check if variable is local or global
                if(localVariables.isVariable(incVarName)){
                    localVariables.setVariableValue(incVarName, incInt, lineNumber);
                } else {
                    globalVariables.setVariableValue(incVarName, incInt, lineNumber);
                }
            }
            result = evalExpression(condition, localVariables, isGlobal);
            if(result == null){
                return;
            }
        }
    }

    private void evalShowMsgBox(ShowMsgBoxStmt showMsgStmt, Variables localVariables, boolean isGlobal){
        Expression expression = showMsgStmt.getValue();
        JavaishVal value = evalExpression(expression, localVariables, isGlobal);
        if(value == null){
            return;
        }
        JOptionPane.showMessageDialog(null, value.getValue());
    }

    private JavaishString evalShowInputBox(ShowInputBoxElmt showInputBoxElmt, Variables localVariables, boolean isGlobal){
        JavaishVal value = null;
        Expression expression = showInputBoxElmt.getValue();
        if(expression != null){
            value = evalExpression(expression, localVariables, isGlobal);
        }
        String input = JOptionPane.showInputDialog(value.getValue());
        return new JavaishString(input);

    }

    private void evalReturn(ReturnStmt returnStmt, Variables localVariables, Return returnVal, boolean isGlobal){
        Expression expression = returnStmt.getValue();
        JavaishVal value = null;
        if(returnStmt.hasReturn()){
            value = evalExpression(expression, localVariables, isGlobal);
            
        }
        returnVal.setHasReturn(true);
        returnVal.setValue(value);
        
    }

    private void evalMutation(MutationStmt mutationStmt, Variables localVariables, boolean isGlobal){
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
        
        
            
        JavaishVal newVal = performOperation(mutationTypeToOperator(type), variable, value);
                if(localVariables.isVariable(name)){
        localVariables.setVariableValue(name, newVal, lineNumber);
        return;
        }
        globalVariables.setVariableValue(name, newVal, lineNumber);
    
  

    }

    private void evalRemoveAt(RemoveAtStmt removeAtStmt, Variables localVariables, boolean isGlobal){
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

    private void evalRemoveFrom(RemoveFromStmt removeFromStmt, Variables localVariables, boolean isGlobal){
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
            //System.out.println("LIST: " + list.listString());

            if(localVariables.isVariable(name)){
                
                localVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
                return;
            }
            globalVariables.setVariableValue(name, new JavaishListVal(list), lineNumber);
            return;
        }
    }

    private void evalRemoveAllFrom(RemoveAllFromStmt removeAllFromStmt, Variables localVariables, boolean isGlobal){
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
           // System.out.println("LIST: " + list.listString());

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
                   // System.out.println("REMOVE FROM: " + intVal.getType() + " " + intListVal.size());
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





}
