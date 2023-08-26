package javaish;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javaish.JavaishVal.JavaishType;
import javaish.Statements.MutationType;

public class Interpreter {
   int lineNumber = 0;
   
   
    Variables globalVariables;
    enum Operator {
        PLUS, MINUS, DIVIDE, MULTIPLY, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL
    }
    public Interpreter( Variables variables){
        
        this.globalVariables = variables;
    }

    public JavaishVal interpretFunction(List<Statements> statements,  Argument[] args,  Expression[] params, String name, boolean isGlobal){
        Variables localVariables = new Variables();
        
        
        if(args != null && params != null) { 
            if(args.length != params.length){
                Error.ArgumentLengthMismatch(name,lineNumber,args.length, params.length);
                return null;
            }
            
            for (int i = 0; i < params.length; i++) {
                Expression param = params[i];
                JavaishVal val = evalExpression(param, localVariables, isGlobal);
                Argument arg = args[i];
                if(arg.getType() != val.getType()){
                    Error.ArgumentTypeMismatch(name, lineNumber, arg.getType().toString(), val.typeString());
                    return null;
                }
                localVariables.addVariable(arg.getName(), arg.getType(), val);
                
            }
        }

        JavaishVal returnVal = interpretBody(statements, localVariables, isGlobal);
        if(returnVal != null){
            return returnVal;
        }
        return null;

        
        
    }

    private JavaishVal interpretBody(List<Statements> statements,Variables funcVariables, boolean isGlobal){
        Result pastResult = new Result(false);  
        Variables localVariables = new Variables(funcVariables);
        Return returnVal = new Return(false, null);
        

        for (Statements statement : statements) {
            if(returnVal.hasReturn()){
                return returnVal.getValue();
            }
            interpretStmt(statement, localVariables, isGlobal, pastResult, returnVal);
        }
        if(returnVal.hasReturn()){
            return returnVal.getValue();
        }
        return null;
    }

    private void interpretStmt(Statements stmt, Variables localVariables, boolean isGlobal, Result pastResult, Return returnVal){
       // System.out.println("Interpreting Stmt Type: " + stmt.getType());
        lineNumber = stmt.getLine();
        switch (stmt.getType()) {
            case ASSIGNMENT:
                AssignmentStmt assignment = (AssignmentStmt) stmt;
                evalAssignment(assignment, localVariables, isGlobal);

                
                break;
            case CALL:
                CallStmt call = (CallStmt) stmt;
                evalCall(call);
                break;
            case DECLARATION:
                DeclarationStmt declaration = (DeclarationStmt) stmt;
                evalDeclaration(declaration, localVariables, isGlobal);
                break;
            case ELSE:
                ElseStmt elseStmt = (ElseStmt) stmt;
                evalElse(elseStmt, localVariables, pastResult);
                break;
            case ELSEIF:
                ElseIfStmt elseifStmt = (ElseIfStmt) stmt;
                evalElseIf(elseifStmt, localVariables, pastResult, isGlobal);
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
                evalIf(ifStmt, localVariables, isGlobal);
                break;
            case WHILE:
                WhileStmt whileStmt = (WhileStmt) stmt;
                evalWhile(whileStmt);
                break;
            case FOREACH:
                ForEachStmt foreachStmt = (ForEachStmt) stmt;
                evalForEach(foreachStmt);
                break;
            case FORWHEN:
                ForWhenStmt forwhenStmt = (ForWhenStmt) stmt;
                evalForWhen(forwhenStmt);
                break;
            case PRINT:
                PrintStmt printStmt = (PrintStmt) stmt;
                evalPrint(printStmt, localVariables, isGlobal);
                break;
            case SHOWMSGBOX:
                ShowMsgBoxStmt showMsgBoxStmt = (ShowMsgBoxStmt) stmt;
                evalShowMsgBox(showMsgBoxStmt, localVariables, isGlobal);
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
                case BOOL:
                    BoolElmt bool = (BoolElmt) elmt;
                    JavaishBoolean valB = new JavaishBoolean(bool.getValue());
                    

                    break;
                case CAST:
                    CastElmt cast = (CastElmt) elmt;
                    JavaishVal val = evalExpression(cast.element, localVariables, isGlobal);
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
                                    } 
                                } catch (Exception e) {
                                    Error.UnableToParse("String", lineNumber, val.typeString());
                                }
                            }
                            break;
                        default:
                            break;
                        
                    }
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
                    total = performOperation(operation, total, newVal);
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
                case NOT:
                    NotElmt not = (NotElmt) elmt;
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
                    JavaishVal valFunc = interpretFunction(body, args, params, function.getName(), false);
                    if(isComp){
                        compVal = performOperation(operation, compVal, valFunc);
                    } else {
                        total = performOperation(operation, total, valFunc);
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
        JavaishVal value = evalExpression(declaration.getValue(), localVariables, isGlobal);
        if(type != value.getType()){
            if(type == JavaishType.FLOAT && value.getType() == JavaishType.INT){
                if(isGlobal){
                 globalVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()));
                } else {
                    localVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()));
                }
                //System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
                return;
            }
          //  System.out.println("Type Mismatch");
            Error.TypeMismatch(type.toString(), value.typeString(), lineNumber);
            return;
        }
        if(isGlobal){
            globalVariables.addVariable(declaration.getName(), type, value);
        } else {
            localVariables.addVariable(declaration.getName(), type, value);
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
        
        globalVariables.addFunction(name, body, args);



    }

    private void evalCall(CallStmt call){
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
        interpretFunction(body, args, params, name, false);

    }

    private void evalIf(IfStmt ifStmt, Variables localVariables, boolean isGlobal){
        Expression condition = ifStmt.getCondition();
        List<Statements> body = ifStmt.getBody();
        JavaishBoolean result = (JavaishBoolean) evalExpression(condition, localVariables, isGlobal);
        if(result.getValue() == true){
            interpretBody(body, localVariables, false);
        }

    }

    private void evalElse(ElseStmt elseStmt, Variables localVariables, Result pastResult){
        List<Statements> body = elseStmt.getBody();
        if(pastResult.getResult() == true){
            return;
        }
        interpretBody(body, localVariables, false);

    }

    private void evalElseIf(ElseIfStmt elseifStmt, Variables localVariables, Result pastResult, Boolean isGlobal){
        Expression condition = elseifStmt.getCondition();
        List<Statements> body = elseifStmt.getBody();
        JavaishBoolean result = (JavaishBoolean) evalExpression(condition, localVariables, isGlobal);
        if(pastResult.getResult() == true){
            return;
        }
        if(result.getValue() == true){
            pastResult.setResult(true);
            interpretBody(body, localVariables, false);
        }

    }

    private void evalPrint(PrintStmt printStmt, Variables localVariables, Boolean isGlobal){
        Expression expression = printStmt.getValue();
        JavaishVal value = evalExpression(expression, localVariables, isGlobal);
        if(value == null){
            return;
        }
        System.out.println(value.getValue());

    }

    private void evalWhile(WhileStmt whileStmt){

    }

    private void evalForEach(ForEachStmt foreachStmt){

    }

    private void evalForWhen(ForWhenStmt forwhenStmt){

    }

    private void evalShowMsgBox(ShowMsgBoxStmt showMsgStmt, Variables localVariables, Boolean isGlobal){
        Expression expression = showMsgStmt.getValue();
        JavaishVal value = evalExpression(expression, localVariables, isGlobal);
        if(value == null){
            return;
        }
        JOptionPane.showMessageDialog(null, value.getValue());
    }

    private void evalReturn(ReturnStmt returnStmt, Variables localVariables, Return returnVal, Boolean isGlobal){
        Expression expression = returnStmt.getValue();
        JavaishVal value = null;
        if(returnStmt.hasReturn()){
            value = evalExpression(expression, localVariables, isGlobal);
            
        }
        returnVal.setHasReturn(true);
        returnVal.setValue(value);
        
    }

    private void evalMutation(MutationStmt mutationStmt, Variables localVariables, Boolean isGlobal){
        MutationType type = mutationStmt.getMutationType();
        String name = mutationStmt.getVarName();
        JavaishVal value = evalExpression(mutationStmt.getValue(), localVariables, isGlobal);

        JavaishVal variable = globalVariables.getVariableValue(name);
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

class Result {
    boolean pastResult;

    public Result(boolean pastResult) {
        this.pastResult = pastResult;
    }
    
    public boolean getResult(){
        return pastResult;
    }

    public void setResult(boolean pastResult){
        this.pastResult = pastResult;
    }
}

class Return {
    boolean hasReturn;
    JavaishVal value;

    public Return(boolean hasReturn, JavaishVal value) {
        this.hasReturn = hasReturn;
        this.value = value;
    }

    public boolean hasReturn(){
        return hasReturn;
    }

    public JavaishVal getValue(){
        return value;
    }

    public void setHasReturn(boolean hasReturn){
        this.hasReturn = hasReturn;
    }

    public void setValue(JavaishVal value){
        this.value = value;
    }
}