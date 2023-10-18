package javaish;



import java.util.ArrayList;
import java.util.List;

import javaish.JavaishVal.JavaishType;

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
    public Expression(Element[] elements, ExpressionReturnType returnType, int line) {
        this.elements = elements;
        this.returnType = returnType;
        this.line = line;
    }

    public Expression(String expression,ExpressionReturnType returnType, int line, int column) {
        this.returnType = returnType;
        this.line = line;
        //Parse Expression
        
        this.elements = parseExpression(expression, column);
    }

    public int getLine() {
        return line;
    }

    private Element[] parseExpression(String expression, int column) {
        if(goal){
            System.out.println("EXPRESSION: " + expression);
        }
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
                        elements.add(new ExpressionElmt(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line)));
                        currentElement = "";
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
                   
                } else if(currentElement.equals("greater") && nextWord(expression, i+1).equals("than")){
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
                    if(currentFunctionName.equals("showInputDialog")){
                        goal = true;
                    }
                    System.out.println("ORIGINAL EXPR: "+expression);
                    System.out.println(iter + " PARSING("+ currentFunctionName + ")FUNCTION ARG: "+currentElement);
                    functionArgs.add(new Expression(parseExpression(currentElement, column + i), ExpressionReturnType.NUMBER, line));

                    currentElement = "";
                    readingFunctionArgs = false;
                    readingFunction = false;
                    if(currentFunctionName.equals("toString") ) {
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
                    
                    } else if(currentFunctionName.equals("showInputDialog")){
                            if(functionArgs.size()!=1){
                                Error.ArgumentLengthMismatch("showInputDialog", getLine(), 1, functionArgs.size()); 
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
        } else if(element.equals("add") || element.equals("+")){
            return new PlusElmt();
        } else if(element.equals("subtract") || element.equals("-")){
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
            if(!Character.isLetter(c)){
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
