package javaish;

import java.util.ArrayList;
import java.util.List;

import javaish.JavaishVal.JavaishType;
import javaish.Statements.MutationType;

public class Interpreter {
   int lineNumber = 0;
   boolean hasReturned = false;
   
    Variables globalVariables;
    enum Operator {
        PLUS, MINUS, DIVIDE, MULTIPLY, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL
    }
    public Interpreter( Variables variables){
        
        this.globalVariables = variables;
    }

    public void interpretFunction(List<Statements> statements,  Argument[] args,  Expression[] params, String name, boolean isGlobal){
        Variables localVariables = new Variables();
        Result pastResult = new Result(false);
        
        if(args != null && params != null) { 
            if(args.length != params.length){
                Error.ArgumentLengthMismatch(name,lineNumber,args.length, params.length);
                return;
            }
            
            for (int i = 0; i < params.length; i++) {
                Expression param = params[i];
                JavaishVal val = evalExpression(param, localVariables);
                Argument arg = args[i];
                if(arg.getType() != val.getType()){
                    Error.ArgumentTypeMismatch(name, lineNumber, arg.getType().toString(), val.typeString());
                    return;
                }
                localVariables.addVariable(arg.getName(), arg.getType(), val);
                
            }
        }

        interpretBody(statements, localVariables, isGlobal, pastResult);

        
        
    }

    private void interpretBody(List<Statements> statements,Variables localVariables, boolean isGlobal, Result pastResult){
        for (Statements statement : statements) {
            if(hasReturned){
                return;
            }
            interpretStmt(statement, localVariables, isGlobal);
        }
    }

    private void interpretStmt(Statements stmt, Variables localVariables, boolean isGlobal){
        System.out.println("Interpreting Stmt Type: " + stmt.getType());
        lineNumber = stmt.getLine();
        switch (stmt.getType()) {
            case ASSIGNMENT:
                AssignmentStmt assignment = (AssignmentStmt) stmt;
                evalAssignment(assignment, localVariables);

                
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
                evalElse(elseStmt);
                break;
            case ELSEIF:
                ElseIfStmt elseifStmt = (ElseIfStmt) stmt;
                evalElseIf(elseifStmt);
                break;
            case MUTATION:
                MutationStmt mutationStmt = (MutationStmt) stmt;
                evalMutation(mutationStmt, localVariables);
                break;
            case RETURN:
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                evalReturn(returnStmt);
                break;
            case FUNCTION:
                FunctionStmt function = (FunctionStmt) stmt;
                evalFunction(function, localVariables, isGlobal);
                break;
            case IF:
                IfStmt ifStmt = (IfStmt) stmt;
                evalIf(ifStmt);
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


            default:
                break;
        }

    }

    private JavaishVal evalExpression(Expression expression, Variables localVariables){
        JavaishVal total = null;
        Operator operation = null;
        
        
        for(Element elmt : expression.getElements()){
            switch (elmt.getType()) {
                case AND:
                    AndElmt and = (AndElmt) elmt;
                    break;
                case BOOL:
                    BoolElmt bool = (BoolElmt) elmt;
                    break;
                case CAST:
                    CastElmt cast = (CastElmt) elmt;
                    JavaishVal val = evalExpression(cast.element, localVariables);
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
                    total = performOperation(operation, total, val);
                    break;
                case DIVIDE:
                    DivideElmt divide = (DivideElmt) elmt;
                    operation = Operator.DIVIDE;
                    break;
                case EQUAL:
                    EqualElmt equal = (EqualElmt) elmt;
                    break;
                case EXPRESSION:
                    ExpressionElmt expressionElmt = (ExpressionElmt) elmt;
                    JavaishVal newVal = evalExpression(expressionElmt.expression, localVariables);
                    total = performOperation(operation, total, newVal);
                    break;
                case FLOAT:
                    FloatElmt floatElmt = (FloatElmt) elmt;
                    JavaishFloat valF = new JavaishFloat(floatElmt.getValue());
                    total = performOperation(operation, total, valF);
                    
                    break;
                case GREATER_THAN:
                    GreaterThanElmt greaterThan = (GreaterThanElmt) elmt;
                    operation = Operator.GREATER_THAN;
                    break;
                case GREATER_THAN_EQUAL:
                    GreaterThanEqualElmt greaterThanEqual = (GreaterThanEqualElmt) elmt;
                    operation = Operator.GREATER_THAN_EQUAL;
                    break;
                case INTEGER:
                    IntElmt integer = (IntElmt) elmt;
                    JavaishInt valI = new JavaishInt(integer.getValue());
                    total = performOperation(operation, total, valI);
                    break;
                case LESS_THAN:
                    LessThanElmt lessThan = (LessThanElmt) elmt;
                    operation = Operator.LESS_THAN;
                    break;
                case LESS_THAN_EQUAL:
                    LessThanEqualElmt lessThanEqual = (LessThanEqualElmt) elmt;
                    operation = Operator.LESS_THAN_EQUAL;
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
                    operation = Operator.NOT_EQUAL;
                    break;
                case OR:
                    OrElmt or = (OrElmt) elmt;

                    break;
                case PLUS:
                    PlusElmt plus = (PlusElmt) elmt;
                    operation = Operator.PLUS;
                    break;
                case STRING:
                    StringElmt string = (StringElmt) elmt;
                    
                    JavaishString valS = new JavaishString(string.getValue());

                    total = performOperation(operation, total, valS);
                    
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
                    total = performOperation(operation, total, valV);
                    break;
            
                default:
                    break;
            }
        }

        return total;
    }

    private JavaishVal performOperation(Operator operation, JavaishVal total, JavaishVal val2){
        JavaishVal result = null;
        if(operation == null){
            System.out.println(val2.getValue() + " SINGLE VAL");
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

    private void evalAssignment(AssignmentStmt assignment, Variables localVariables){
        String name = assignment.getName();
        JavaishVal value = evalExpression(assignment.getValue(), localVariables);
        if(localVariables.isVariable(name)){
            localVariables.setVariableValue(name, value, lineNumber);
            return;
        }
        globalVariables.setVariableValue(name, value, lineNumber);
       

    }

    private void evalDeclaration(DeclarationStmt declaration,Variables localVariables ,boolean isGlobal){
        JavaishType type = declaration.getVarType();
        JavaishVal value = evalExpression(declaration.getValue(), localVariables);
        if(type != value.getType()){
            if(type == JavaishType.FLOAT && value.getType() == JavaishType.INT){
                if(isGlobal){
                 globalVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()));
                } else {
                    localVariables.addVariable(declaration.getName(), type, new JavaishFloat(((JavaishInt) value).getValue()));
                }
                System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
                return;
            }
            System.out.println("Type Mismatch");
            Error.TypeMismatch(type.toString(), value.typeString(), lineNumber);
            return;
        }
        if(isGlobal){
            globalVariables.addVariable(declaration.getName(), type, value);
        } else {
            localVariables.addVariable(declaration.getName(), type, value);
        }
        System.out.println("Declaration: Type:" + declaration.getVarType()+ " Name: "+ declaration.getName() + " Value:" + value.getValue());
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

    private void evalIf(IfStmt ifStmt){

    }

    private void evalElse(ElseStmt elseStmt){

    }

    private void evalElseIf(ElseIfStmt elseifStmt){

    }

    private void evalWhile(WhileStmt whileStmt){

    }

    private void evalForEach(ForEachStmt foreachStmt){

    }

    private void evalForWhen(ForWhenStmt forwhenStmt){

    }

    private void evalReturn(ReturnStmt returnStmt){
        boolean hasReturn = returnStmt.hasReturn();
        
        if(!hasReturn){
            
            hasReturned = true;
            return;
        } 
    }

    private void evalMutation(MutationStmt mutationStmt, Variables localVariables){
        MutationType type = mutationStmt.getMutationType();
        String name = mutationStmt.getVarName();
        JavaishVal value = evalExpression(mutationStmt.getValue(), localVariables);

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