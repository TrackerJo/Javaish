package com.trackerjo.javaish;



import java.util.ArrayList;
import java.util.List;

import com.trackerjo.javaish.Interpreter.Operator;
import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.RobotType;

public class Expression {
    enum ExpressionReturnType {
        INT,
        FLOAT,
        STRING,
        BOOL,
        NUMBER,
        INTLIST,
        FLOATLIST,
        STRINGLIST,
        BOOLEANLIST,
    
    }
    private int line;
    private boolean goal;
    private ExpressionReturnType returnType;
    private Element[] elements;
    private int startColumn;
    private int column;
    public Expression(Element[] elements, ExpressionReturnType returnType, int line) {
        this.elements = elements;
        this.returnType = returnType;
        this.line = line;
    }

    public Expression(String expression,ExpressionReturnType returnType, int line, int column) {
        this.returnType = returnType;
        this.line = line;
        this.column = column;
        //Parse Expression
        
        this.elements = parseExpression(expression, column);
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    private Element[] parseExpression(String expression, int column) {
        
        System.out.println("EXPRESSION: " + expression);
        
        int iter = 0;
        //TODO: Parse Expression
        int i = 0;
        boolean readingString = false;
        boolean lastReadString = false;
        boolean readingFunction = false;
        boolean readingFunctionName = false;
        boolean readingFunctionArgs = false;
        boolean readingExpression = false;
        boolean readingCast = false;
        boolean readingArray = false;
        boolean readingArrayElmt = false;
        boolean readingArrayElmtArgs = false;
        boolean readingArrayArgExpression = false;
        boolean readingGetArrayLength = false;
        boolean readingRobotAction = false;
        boolean readingNot = false;
        JavaishType castType = null;
        ExpressionReturnType castReturnType = ExpressionReturnType.NUMBER;
        List<Element> elements = new ArrayList<Element>();
        String currentElement = "";
        String currentFunctionName = "";
        String currentFunctionArgs = "";
        String currentArrayName = "";
        int currentExpressionDepth = 0;
        int currentCastDepth = 0;
        int currentFunctionDepth = 0;
        int currentArrayArgDepth = 0;
      
        List<Expression> functionArgs = new ArrayList<Expression>();
        List<Expression> arrayElmts = new ArrayList<Expression>();
        while(i<expression.length()){
            char c = expression.charAt(i);
             char nextChar = ' ';
            if(i+1<expression.length()){
                nextChar = expression.charAt(i+1);
            }
          
           
            if(c=='(' && !readingFunction && !readingString ){
                if(readingExpression && !readingCast && !readingArrayElmtArgs){
                    currentExpressionDepth++;
                    currentElement += c;
                } else if(possibleFunctionName(currentElement) && !readingCast){
                    //System.out .println("READING FUNCTION NAME: " + currentElement);
                    currentFunctionDepth++;
                    readingFunction = true;
                    currentFunctionName = currentElement;
                    currentElement = "";
                    readingFunctionArgs = true;
                } else if(readingCast){
                    currentCastDepth++;
                    currentElement += c;
                } else if(readingArrayElmtArgs){
                    currentArrayArgDepth++;
                    readingArrayArgExpression = true;
                    currentElement += c;
                }
                 
                else {

                    readingExpression = true;
                    
                    currentExpressionDepth++;
                }
            } else if(c==')' && !readingFunction && !readingString){
                if(readingExpression){
                    currentExpressionDepth--;
                    if(currentExpressionDepth==0){
                        
                        readingExpression = false;
                        System.out.println("EXPRESSIONSSSS: " + readingArray);
                        if(!readingArray){
                            elements.add(new ExpressionElmt(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line)));
                            currentElement = "";

                        } 
                        
                    } else {
                        currentElement += c;
                    }
                } else if(readingCast){
                    currentCastDepth--;
                    if(currentCastDepth==0){
                        readingCast = false;
                        elements.add(new CastElmt(castType, new Expression(parseExpression(currentElement, column + i), castReturnType, line)));
                        currentElement = "";
                    } else {
                        currentElement += c;
                    }
                    
                    
                } else if(readingArrayElmtArgs){
                    currentArrayArgDepth--;
                    if(currentArrayArgDepth==0){
                        readingArrayElmtArgs = false;
                        readingArrayArgExpression = false;
                        currentElement += c;
                        System.out.println("ARRAY ARG EXPR: ");
                        System.out.println("ARRAY ARG EXPR: " + currentElement);
                        Expression index = new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line);
                        elements.add(new ListValElmt(currentArrayName, index));
                        currentElement = "";
                    } else {
                        currentElement += c;
                    }
                }
                
                else {
                    currentElement += c;
                }
               
            
            } else if(c=='"' && !readingFunction && !readingExpression && !readingCast && !readingArray){
                if(readingString){
                    readingString = false;
                    
                    if(!readingArray) {
                        elements.add(new StringElmt(currentElement));
                    } 
                    currentElement = "";
                    lastReadString = true;
                    
                }else{
                    readingString = true;
                }
            } else if (c== '"' && readingFunction ){
                if(readingString){
                    readingString = false;
                    currentElement += c;
                    
                }else{
                    readingString = true;
                    currentElement += c;
                }
            }
            else if(c=='!' && !readingString && !readingArrayArgExpression && !readingCast && !readingExpression && !readingFunctionArgs && nextChar != '=' && nextChar != '<' && nextChar != '>'){
                System.out.println("READING NOT");
                readingNot = true;
            }
            else if(c==' '){
                if(readingString || readingExpression || readingCast || readingFunctionArgs || readingArrayArgExpression){
                    currentElement += c;
                } 
                else if(currentElement.equals("robot") && !readingString && !readingFunctionArgs){
                    readingRobotAction = true;
                    currentElement = "";
                }
                else if(currentElement.equals("call") && !readingFunction){
                    //System.out .println("READING FUNCTION");
                    readingFunction = true;
                    currentElement = "";
                    readingFunctionName = true;
                }
                else if(currentElement.equals("not") && nextWord(expression, i+1).equals("equals")){
                    elements.add(new NotEqualElmt());
                    i+=5;
                    currentElement = "";
                   
                } else if (currentElement.equals("not")){
                    readingNot = true;
                    currentElement = "";
                }
                else if(currentElement.equals("greater") && nextWord(expression, i+1).equals("than")){
                    if(nextWord(expression, i+6).equals("or")){
                        elements.add(new GreaterThanEqualElmt());
                        i+=16;
                        currentElement = "";
                    } else {
                        elements.add(new GreaterThanElmt());
                        i+=4;
                        currentElement = "";
                    }
                } else if(currentElement.equals("less") && nextWord(expression, i+1).equals("than")){
                    if(nextWord(expression, i+6).equals("or")){
                        elements.add(new LessThanEqualElmt());
                        i+=16;
                        currentElement = "";
                    } else {
                        elements.add(new LessThanElmt());
                        i+=4;
                        currentElement = "";
                    }
                } else if(nextWord(expression, i+1).equals("sub") && !readingString && !readingFunctionArgs && !readingExpression){
                    readingArrayElmt = true;
                    currentArrayName = currentElement;
                    currentElement = "";
                } else if(currentElement.equals("sub") && !readingString && readingArrayElmt){
                    readingArrayElmtArgs = true;
                    currentElement = "";
                } else if(currentElement.equals("length") ){
                    System.out.println("LENGTH NEXT WORD: " + nextWord(expression, i+1));
                    if(nextWord(expression, i + 1).equals("of")){
                        readingGetArrayLength = true;
                        currentElement = "";
                        
                        i+=3;
                    }
                } else if(readingGetArrayLength){
                    elements.add(new ArrayLengthElmt(currentElement));
                    currentElement = "";
                    readingGetArrayLength = false;
                }
                else if(readingArrayElmtArgs && !readingString && !readingArrayArgExpression){
                    Expression index = new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line);
                    elements.add(new ListValElmt(currentArrayName, index));
                    currentElement = "";
                }
                else{
                    if(currentElement.length()>0 && !currentElement.equals("not") && !readingFunction){
                        //System.out .println("PARSING ELEMENT: "+currentElement);
                        
                        if(readingNot){
                            System.out.println("Adding NOT");
                            elements.add(new NotElmt(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line)));
                            readingNot = false;
                        } else {
                            Element elmt = parseElement(currentElement, column + i);
                            elements.add(elmt);
                        }
                        currentElement = "";
                        lastReadString = false;
                    }
                }
            }
            // else if(readingFunction && readingFunctionName && c == '('){
                
            //     //System.out .println("PARSING FUNCTION NAME: "+currentElement);
            //     readingFunctionName = false;
            //     readingFunctionArgs = true;
            //     currentFunctionName = currentElement;
            //     currentElement = "";
            // } 

            else if(readingFunction && readingFunctionArgs && c == ',' && !readingString){
                //System.out .println("PARSING FUNCTION ARG: "+currentElement);
                
                functionArgs.add(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line));
                currentElement = "";
            } else if(readingFunction && c == ')' && !readingString){

                currentFunctionDepth--;
                if(currentFunctionDepth == 0){
                    iter++;
                    if(currentFunctionName.equals("input")){
                        goal = true;
                    }
                    System.out.println("ORIGINAL EXPR: "+expression);
                    System.out.println(iter + " PARSING("+ currentFunctionName + ")FUNCTION ARG: "+currentElement);
                    functionArgs.add(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line));

                    currentElement = "";
                    readingFunctionArgs = false;
                    readingFunction = false;

                    if(readingRobotAction){
                        System.out.println("ADDING ROBOT ACTION:" + currentFunctionName);
                        try {
                            RobotType.valueOf(currentFunctionName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            Error.InvalidRobotAction(currentFunctionName, getLine());
                        }
                        elements.add(new RobotActionElmt(RobotType.valueOf(currentFunctionName.toUpperCase()), functionArgs));
                        readingRobotAction = false;
                    } else if(currentFunctionName.equals("toString") ) {
                        if(functionArgs.size()!=1){
                            Error.ArgumentLengthMismatch(currentFunctionName,  getLine(), 1,functionArgs.size());
                        }
                        elements.add(new CastElmt(JavaishType.STRING, functionArgs.get(0)));
                        System.out .println("CAST TO STRING: " + functionArgs.get(0).toString());
                    
                    } else if(currentFunctionName.equals("toFloat")){
                            if(functionArgs.size()!=1){
                            Error.ArgumentLengthMismatch(currentFunctionName, getLine(),1, functionArgs.size() );
                        }
                        elements.add(new CastElmt(JavaishType.FLOAT, functionArgs.get(0)));
                    
                    } else if(currentFunctionName.equals("toInt")){
                        if(functionArgs.size()!=1){
                            Error.ArgumentLengthMismatch(currentFunctionName,  getLine(),1, functionArgs.size());
                        }
                        elements.add(new CastElmt(JavaishType.INT, functionArgs.get(0)));
                    } else if(currentFunctionName.equals("toBool")){
                            if(functionArgs.size()!=1){
                            Error.ArgumentLengthMismatch(currentFunctionName, getLine(), 1, functionArgs.size() );
                        }
                        if(readingNot){
                            System.out.println("Adding NOT");
                            Element[] notArgs = {new CastElmt(JavaishType.BOOLEAN, functionArgs.get(0))};
                            elements.add(new NotElmt(new Expression(notArgs, ExpressionReturnType.NUMBER, line)));
                            readingNot = false;
                        } else {
                            elements.add(new CastElmt(JavaishType.BOOLEAN, functionArgs.get(0)));
                        }
                        //elements.add(new CastElmt(JavaishType.BOOLEAN, functionArgs.get(0)));
                    
                    } else if(currentFunctionName.equals("input")){
                            if(functionArgs.size()!=1){
                                Error.ArgumentLengthMismatch("input", getLine(), 1, functionArgs.size()); 
                            }
                            //System.out .println("SHOW INPUT DIALOG ARG: " + functionArgs.get(0).toString());
                            
                            elements.add(new ShowInputBoxElmt(functionArgs.get(0)));
                        }
                    
                    else {
                        //Loop through function args and check if they are valid
                        List<Expression> newFunctionArgs = new ArrayList<Expression>();
                        for (Expression functionArg : functionArgs) {
                            if(functionArg.elements.length != 0){
                                newFunctionArgs.add(functionArg);
                            }
                            
                        }
                        Expression[] functionArgsArray = newFunctionArgs.toArray(new Expression[newFunctionArgs.size()]);
                       
                        Element[] notElements = {new FunctionElmt(currentFunctionName, functionArgsArray)};
                        if(readingNot){
                            System.out.println("Adding NOT");
                            elements.add(new NotElmt(new Expression(notElements, ExpressionReturnType.NUMBER, line)));
                            readingNot = false;
                        } else {
                            elements.add(new FunctionElmt(currentFunctionName, functionArgsArray));
                        }
                        //elements.add(new FunctionElmt(currentFunctionName, functionArgsArray));
                    }
                    
                    currentFunctionName = "";
                    currentFunctionArgs = "";
                    functionArgs = new ArrayList<Expression>();


                } else {
                    currentElement += c;
                }
            } else if(c=='(' && readingFunction && !readingString){
                currentFunctionDepth++;
                currentElement += c;
            } else if(c == '[' && !readingArray && !readingString && !readingFunction){
                if(currentElement.equals("")){
                    readingArray = true;
                    
                }
            } else if(c == ',' && !readingString && readingArray){
                Expression arrayElmt = new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line);
                currentElement = "";
                arrayElmts.add(arrayElmt);
            } else if(c==']' && !readingString && readingArray){
                Expression arrayElmt = new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line);
                arrayElmts.add(arrayElmt);
                elements.add(new ListElmt(arrayElmts, returnTypeToJavaishType(getReturnType())));
                readingArray = false;
                currentElement = "";
                arrayElmts = new ArrayList<Expression>();
            
            } 
            else {
                currentElement += c;
            }

            i++;
        }

        if(currentArrayArgDepth > 0 || currentExpressionDepth > 0 || currentFunctionDepth > 0 || currentCastDepth > 0){
            Error.UnclosedParenthesis(getLine(), column + i);
        }

        if(readingArray){
            Error.UnclosedBracket(getLine(), column + i);
        }

        if(currentElement.length()>0 && !lastReadString){
            
            if(readingArrayElmtArgs){
                  Expression index = new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line);
                    elements.add(new ListValElmt(currentArrayName, index));
                    currentElement = "";
            } else 
            if(readingGetArrayLength){
                elements.add(new ArrayLengthElmt(currentElement));
                currentElement = "";
                readingGetArrayLength = false;
            }else {
                 
                        if(readingNot){
                            System.out.println("Adding NOT");
                            elements.add(new NotElmt(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line)));
                            readingNot = false;
                        } else {
                            Element elmt = parseElement(currentElement, column + i);
                            elements.add(elmt);
                        }
               // elements.add(parseElement(currentElement, column + i));
            }
            
        }

        return elements.toArray(new Element[elements.size()]);

        

       
    }

    private boolean possibleFunctionName(String name){
       //Check if contains parenthesis

            String[] splitName = name.split("\\(");
            String functionName = splitName[0];
            if(functionName.contains(" ") || functionName.length()==0){
                return false;
            }
         
            return true;
    }

    private Element parseElement(String element, int column) {
        if(isInteger(element)){
            return new IntElmt(Integer.parseInt(element));
        }else if(isFloat(element)){
            return new FloatElmt(Float.parseFloat(element));
        }else if(element.equals("true")||element.equals("false")){
            return new BoolElmt(Boolean.parseBoolean(element));
        }else if(element.equals("equals") || element.equals("==")){
            return new EqualElmt();
        } else if(element.equals("!=")){
            return new NotEqualElmt();
        } else if(element.equals(">")){
            return new GreaterThanElmt();
        } else if(element.equals("<")){
            return new LessThanElmt();
        } else if( element.equals(">=")){
            return new GreaterThanEqualElmt();
        } else if(element.equals("<=")){
            return new LessThanEqualElmt();
        } else if(element.equals("and") || element.equals("&&")){
            return new AndElmt();
        } else if(element.equals("or") || element.equals("||")){
            return new OrElmt();
        } else if(element.equals("plus") || element.equals("+")){
            return new PlusElmt();
        } else if(element.equals("minus") || element.equals("-")){
            return new MinusElmt();
        } else if(element.equals("times") || element.equals("*")){
            return new MultiplyElmt();
        } else if(element.equals("divide") || element.equals("/")){
            return new DivideElmt();
        } else if(isVariable(element)){
            return new VariableElmt(element);
        }
        Error.UnexpectedElmt(element, getLine(), column);
        return null;
    }
 
    public JavaishType typeExpression(Expression expression, int lineNumber, List<Variable> variables, List<Function> functions){
        JavaishType total = null;
        Operator operation = null;
        Operator comparison = null;
        JavaishType compVal = null;
        boolean isComp = false;
        
        
        for(Element elmt : expression.getElements()){
            switch (elmt.getType()) {
                case AND:
                    AndElmt and = (AndElmt) elmt;
                    JavaishType result = performComparision(comparison, total, compVal, lineNumber);
                    if(result == null){
                        return null;
                    }
                    return JavaishType.BOOLEAN;
                    
                case NOT:
                    NotElmt not = (NotElmt) elmt;
                    JavaishType valNot = typeExpression(not.getExpression(), lineNumber, variables, functions);
                    if(valNot == JavaishType.BOOLEAN){
                        
                        if(isComp){
                            compVal = performOperation(operation, compVal, JavaishType.BOOLEAN, lineNumber);
                        } else {
                            total = performOperation(operation, total, JavaishType.BOOLEAN, lineNumber);
                        }
                    } else {
                        Error.TypeMismatch("Boolean", valNot.toString(), lineNumber);
                        
                    }
                    break;
                case BOOL:
                    BoolElmt bool = (BoolElmt) elmt;
                    
                    if(isComp){
                        compVal = performOperation(operation, compVal, JavaishType.BOOLEAN, lineNumber);
                    } else {
                        total = performOperation(operation, total, JavaishType.BOOLEAN, lineNumber);
                    }
                    

                    break;
                case CAST:
                    CastElmt cast = (CastElmt) elmt;
                    JavaishType val = typeExpression(cast.getExpression(), lineNumber, variables, functions);
                    //System.out .println("Cast: " + cast.castType + " " + val.typeString());
                    switch (cast.castType) {
                        case FLOAT:
                            if(val != JavaishType.FLOAT){
                                val = JavaishType.FLOAT;
                            }
                            break;
                        case INT:
                            if(val != JavaishType.INT){
                                val = JavaishType.INT;
                            }
                            break;
                        case STRING:
                            if(val != JavaishType.STRING){
                                val = JavaishType.STRING;
                            }
                            break;
                        case BOOLEAN:
                            if(val != JavaishType.BOOLEAN){
                                val = JavaishType.BOOLEAN;
                            }
                            break;
                        default:
                            break;
                        
                    }
                    //System.out .println("NEW CAST: " + val.typeString());
                    if(isComp){
                        compVal = performOperation(operation, compVal, val, lineNumber);
                    } else {
                        total = performOperation(operation, total, val, lineNumber);
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
                    JavaishType newVal = typeExpression(expressionElmt.expression, lineNumber, variables, functions);
                    if(isComp){
                        compVal = performOperation(operation, compVal, newVal, lineNumber);
                    } else {
                        total = performOperation(operation, total, newVal, lineNumber);
                    }
                    break;
                case FLOAT:
                    FloatElmt floatElmt = (FloatElmt) elmt;
                    JavaishType valF = JavaishType.FLOAT;
                    if(isComp){
                        compVal = performOperation(operation, compVal, valF, lineNumber);
                    } else {
                        total = performOperation(operation, total, valF, lineNumber);
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
                    JavaishType valI = JavaishType.INT;
                    if(isComp){
                        compVal = performOperation(operation, compVal, valI, lineNumber);
                    } else {
                        total = performOperation(operation, total, valI, lineNumber);
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
                    JavaishType resultO = performComparision(comparison, total, compVal, lineNumber);
                    if(resultO == null){
                        return null;
                    }
                    return JavaishType.BOOLEAN;
                   

                case PLUS:
                    PlusElmt plus = (PlusElmt) elmt;
                    operation = Operator.PLUS;
                    break;
                case STRING:
                    StringElmt string = (StringElmt) elmt;
                    
                    JavaishType valS = JavaishType.STRING;
                    if(isComp){
                        
                        compVal = performOperation(operation, compVal, valS, lineNumber);
                    } else {
                        total = performOperation(operation, total, valS, lineNumber);
                    }

                    
                    
                    break;
                
                case VARIABLE:
                    VariableElmt variable = (VariableElmt) elmt;
                    JavaishType valV = null;
                    for (Variable var : variables) {
                        if(var.getName().equals(variable.getName())){
                            valV = var.getType();
                        }
                    }
                    if(isComp){
                        compVal = performOperation(operation, compVal, valV, lineNumber);
                    } else {
                        total = performOperation(operation, total, valV, lineNumber);
                    }
                    break;
                case FUNCTION:
                    FunctionElmt function = (FunctionElmt) elmt;
                    String functionName = function.getName();
                    JavaishType valFunc = null;
                    for (Function func : functions) {
                        if(func.getFunctionName().equals(functionName)){
                            valFunc = func.getReturnType();
                        }
                    }
                    if(isComp){
                        compVal = performOperation(operation, compVal, valFunc, lineNumber);
                    } else {
                        total = performOperation(operation, total, valFunc, lineNumber);
                    }
                    break;
                case SHOWINPUTBOX:
                    ShowInputBoxElmt showInputBox = (ShowInputBoxElmt) elmt;
                    JavaishType input = JavaishType.STRING;
                    if(isComp){
                        compVal = performOperation(operation, compVal, input, lineNumber);
                    } else {
                        total = performOperation(operation, total, input, lineNumber);
                    }
                    break;
                case LISTVAL:
                    
                    ListValElmt listVal = (ListValElmt) elmt;
                    String listName = listVal.getListName();
                    JavaishType listValVal = null;
                    for (Variable var : variables) {
                        if(var.getName().equals(listName)){
                            listValVal = var.getType();
                        }
                    }
                    //Convert list type to type
                    if(listValVal == JavaishType.INTLIST){
                        listValVal = JavaishType.INT;
                    } else if(listValVal == JavaishType.FLOATLIST){
                        listValVal = JavaishType.FLOAT;
                    } else if(listValVal == JavaishType.STRINGLIST){
                        listValVal = JavaishType.STRING;
                    } else if(listValVal == JavaishType.BOOLEANLIST){
                        listValVal = JavaishType.BOOLEAN;
                    }
                    if(isComp){
                        compVal = performOperation(operation, compVal, listValVal, lineNumber);
                    } else {
                        total = performOperation(operation, total, listValVal, lineNumber);
                    }
                    break;

                    
                case LIST:
                    
                    ListElmt list = (ListElmt) elmt;
                    JavaishType type = list.getListType();
                    if(isComp){
                        if(operation != null){
                            Error.CantPerformOperation(operation.toString(), type.toString(), lineNumber);

                        }
                        compVal = performOperation(operation, compVal, type, lineNumber);
                    } else {
                        if(operation != null){
                            Error.CantPerformOperation(operation.toString(), type.toString(), lineNumber);
                        }
                        total = performOperation(operation, total, type, lineNumber);
                    }

               
                    break;
                case ARRAYLENGTH:
                    ArrayLengthElmt arrayLength = (ArrayLengthElmt) elmt;
                    JavaishType length = JavaishType.INT;
                    
                    if(isComp){
                        compVal = performOperation(operation, compVal, length, lineNumber);
                    } else {
                        total = performOperation(operation, total, length, lineNumber);
                    }
                    break;
                default:
                    break;
            }
        }
        if(isComp){
            return performComparision(comparison, total, compVal, lineNumber);
        }

        return total;
    }

    private JavaishType performOperation(Operator operation, JavaishType total, JavaishType val2, int lineNumber){
        JavaishType result = null;
        if(val2 == JavaishType.BOOLEAN || total == JavaishType.BOOLEAN){
            Error.TypeMismatch("Number", "Boolean", lineNumber);
        }
        if(operation == null){
            //System.out.println(val2.getValue() + " SINGLE VAL");
            return val2;
        }
        switch (operation) {
            case PLUS:
                if(total == JavaishType.STRING){
                    if(val2 != JavaishType.STRING){
                         Error.TypeMismatch("String", val2.toString(), lineNumber);
                        return null;
                    }
                    result = JavaishType.STRING;
                } else if(val2 == JavaishType.STRING){
                    if(total != JavaishType.STRING){
                        Error.TypeMismatch("String", total.toString(), lineNumber);

                        return null;
                    } else {
                    result = JavaishType.STRING;
                    }
                } else if(total == JavaishType.FLOAT){
                    
                    result = JavaishType.FLOAT;
                    
                } else if(val2 == JavaishType.FLOAT){
                    
                    result = JavaishType.FLOAT;
                    
                } else {
                    result = JavaishType.INT;
                }
                
                
                break;
            case MULTIPLY:
                if(total == JavaishType.STRING || val2 == JavaishType.STRING){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(total == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else if(val2 == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else {
                    result = JavaishType.INT;
                }
                break;
            case DIVIDE:
                if(total == JavaishType.STRING || val2 == JavaishType.STRING){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(total == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else if(val2 == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else {
                    result = JavaishType.INT;
                }
                break;
            case MINUS:
                if(total == JavaishType.STRING || val2 == JavaishType.STRING){
                     Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                else if(total == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else if(val2 == JavaishType.FLOAT){
                    result = JavaishType.FLOAT;
                } else {
                    result = JavaishType.INT;
                }
                break;
            default:
                break;
        }
        return result;
    }

    private JavaishType performComparision(Operator comparison, JavaishType left, JavaishType right, int lineNumber){
        JavaishType result = null;
        switch (comparison) {
            case EQUAL:
                if(left == JavaishType.STRING && right == JavaishType.STRING){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.STRING || right == JavaishType.STRING){
                   if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                        Error.TypeMismatch("String", "Bool", lineNumber);
                    } else {
                        Error.TypeMismatch("String", "Number", lineNumber);
                    }
                    return null;
                }
                else if(left == JavaishType.BOOLEAN && right == JavaishType.BOOLEAN){
                   result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                   if(left == JavaishType.STRING || right == JavaishType.STRING){
                        Error.TypeMismatch("Boolean", "String", lineNumber);
                    } else {
                        Error.TypeMismatch("Boolean", "Number", lineNumber);
                    }
                    return null;
                }
                else if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                }
                
                break;
            case NOT_EQUAL:
                if(left == JavaishType.STRING && right == JavaishType.STRING){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.STRING || right == JavaishType.STRING){
                     if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                            Error.TypeMismatch("String", "Bool", lineNumber);
                      } else {
                            Error.TypeMismatch("String", "Number", lineNumber);
                      }
                      return null;
                }
                else if(left == JavaishType.BOOLEAN && right == JavaishType.BOOLEAN){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                    if(left == JavaishType.STRING || right == JavaishType.STRING){
                        Error.TypeMismatch("Boolean", "String", lineNumber);
                    } else {
                        Error.TypeMismatch("Boolean", "Number", lineNumber);
                    }
                    return null;
                }
                else if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                } 
                
                break;
            case LESS_THAN:
                if(left == JavaishType.STRING || right == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                } 
                if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                }
                break;
            case GREATER_THAN:
                if(left == JavaishType.STRING || right == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                } 
                break;

            case LESS_THAN_EQUAL:
                if(left == JavaishType.STRING || right == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                } 
                break;
            case GREATER_THAN_EQUAL:
                if(left == JavaishType.STRING || right == JavaishType.STRING){
                    Error.TypeMismatch("Number", "String", lineNumber);
                    return null;
                }
                if(left == JavaishType.BOOLEAN || right == JavaishType.BOOLEAN){
                    Error.TypeMismatch("Number", "Boolean", lineNumber);
                    return null;
                }
                //Make both numbers float
                if(left == JavaishType.INT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.INT && right == JavaishType.FLOAT){
                    result = JavaishType.BOOLEAN;
                } else if(left == JavaishType.FLOAT && right == JavaishType.INT){
                    result = JavaishType.BOOLEAN;
                } else {
                    result = JavaishType.BOOLEAN;
                } 
                break;

        
            default:
                break;
        }

        return result;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    //Check if element could be variable name (Only has letters)
    private boolean isVariable(String str) {
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

    public ExpressionReturnType getReturnType() {
        return returnType;
    }

    public Element[] getElements() {
        return elements;
    }

    public String nextWord(String str, int start) {
        String word = "";
        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c==' '){
                return word;
            } else {
                word += c;
            }
        }
        return word;
    }

    public String toString() {
        String str = "";
        for (Element elmt : elements) {
            str += elmt.toString() + " ";
        }
        if(str.length()>0){
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    private JavaishType returnTypeToJavaishType(ExpressionReturnType returnType) {
        switch (returnType) {
            case INT:
                return JavaishType.INT;
            case FLOAT:
                return JavaishType.FLOAT;
            case STRING:
                return JavaishType.STRING;
            case BOOL:
                return JavaishType.BOOLEAN;
            
            case INTLIST:
                return JavaishType.INTLIST;
            case FLOATLIST:
                return JavaishType.FLOATLIST;
            case STRINGLIST:
                return JavaishType.STRINGLIST;
            case BOOLEANLIST:
                return JavaishType.BOOLEANLIST;
            default:
                return null;
        }
    }
    
}
