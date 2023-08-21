package javaish;

import java.util.ArrayList;
import java.util.List;

import javaish.Variables.VarType;

public class Expression {
    enum ExpressionReturnType {
        INT,
        FLOAT,
        STRING,
        BOOL,
        NUMBER
    
    }
    private int line;
    private ExpressionReturnType returnType;
    private Element[] elements;
    public Expression(Element[] elements, ExpressionReturnType returnType, int line) {
        this.elements = elements;
        this.returnType = returnType;
        this.line = line;
    }

    public Expression(String expression,ExpressionReturnType returnType, int line) {
        this.returnType = returnType;
        this.line = line;
        //Parse Expression
        
        this.elements = parseExpression(expression);
    }

    public int getLine() {
        return line;
    }

    private Element[] parseExpression(String expression) {
        System.out.println("PARSING EXPRESSION: "+expression);
        //TODO: Parse Expression
        int i = 0;
        boolean readingString = false;
        boolean lastReadString = false;
        boolean readingFunction = false;
        boolean readingFunctionName = false;
        boolean readingFunctionArgs = false;
        boolean readingExpression = false;
        boolean readingCast = false;
        VarType castType = null;
        ExpressionReturnType castReturnType = ExpressionReturnType.NUMBER;
        List<Element> elements = new ArrayList<Element>();
        String currentElement = "";
        String currentFunctionName = "";
        String currentFunctionArgs = "";
        int currentExpressionDepth = 0;
        List<Element[]> functionArgs = new ArrayList<Element[]>();
        while(i<expression.length()){
            char c = expression.charAt(i);
            if(c=='(' && !readingFunction && !readingString){
                if(readingExpression){
                    currentExpressionDepth++;
                    currentElement += c;
                } else if(currentElement.equals("toString")) {
                    readingCast = true;
                    castType = VarType.STRING;
                    castReturnType = ExpressionReturnType.STRING;
                    currentElement = "";
                } else if(currentElement.equals("toFloat")){
                    readingCast = true;
                    castType = VarType.FLOAT;
                    castReturnType = ExpressionReturnType.FLOAT;
                     currentElement = "";
                } else if(currentElement.equals("toInt")){
                    readingCast = true;
                    castType = VarType.INT;
                     currentElement = "";
                    castReturnType = ExpressionReturnType.INT;
                } else if(currentElement.equals("toBool")){
                    readingCast = true;
                    castType = VarType.BOOL;
                    castReturnType = ExpressionReturnType.BOOL;
                     currentElement = "";
                
                }else {

                    readingExpression = true;
                    
                    currentExpressionDepth++;
                }
            } else if(c==')' && !readingFunction && !readingString ){
                if(readingExpression){
                    currentExpressionDepth--;
                    if(currentExpressionDepth==0){
                        readingExpression = false;
                        elements.add(new ExpressionElmt(new Expression(parseExpression(currentElement), ExpressionReturnType.NUMBER, line)));
                        currentElement = "";
                    } else {
                        currentElement += c;
                    }
                } else if(readingCast){
                    readingCast = false;
                    elements.add(new CastElmt(castType, new ExpressionElmt(new Expression(parseExpression(currentElement), castReturnType, line))));
                    currentElement = "";
                } else {
                    currentElement += c;
                }
            } else if(c=='"' && !readingFunction && !readingExpression && !readingCast){
                if(readingString){
                    readingString = false;
                    
                    elements.add(new StringElmt(currentElement));
                    currentElement = "";
                    lastReadString = true;
                    
                }else{
                    readingString = true;
                }
            } else if (c== '"' && readingFunction){
                if(readingString){
                    readingString = false;
                    currentElement += c;
                    
                }else{
                    readingString = true;
                    currentElement += c;
                }
            }
            else if(c==' '){
                if(readingString || readingExpression || readingCast){
                    currentElement += c;
                } 
                else if(currentElement.equals("call") && !readingFunction){
                    System.out.println("READING FUNCTION");
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
                }
                
                else{
                    if(currentElement.length()>0 && !currentElement.equals("not") && !readingFunction){
                        System.out.println("PARSING ELEMENT: "+currentElement);
                        elements.add(parseElement(currentElement));
                        currentElement = "";
                        lastReadString = false;
                    }
                }
            } else if(readingFunction && readingFunctionName && c == '('){
                System.out.println("PARSING FUNCTION NAME: "+currentElement);
                readingFunctionName = false;
                readingFunctionArgs = true;
                currentFunctionName = currentElement;
                currentElement = "";
            } 
            else if(readingFunction && readingFunctionArgs && c == ',' && !readingString){
                System.out.println("PARSING FUNCTION ARG: "+currentElement);
                functionArgs.add(parseExpression(currentElement));
                currentElement = "";
            } else if(readingFunction && readingFunctionArgs && c == ')' && !readingString){
                System.out.println("PARSING FUNCTION ARG: "+currentElement);
                functionArgs.add(parseExpression(currentElement));
                currentElement = "";
                readingFunctionArgs = false;
                readingFunction = false;
                elements.add(new FunctionElmt(currentFunctionName, functionArgs));
                currentFunctionName = "";
                currentFunctionArgs = "";

            }
            
            else {
                currentElement += c;
            }

            i++;
        }

        if(currentElement.length()>0 && !lastReadString){
            
            elements.add(parseElement(currentElement));
            
        }

        return elements.toArray(new Element[elements.size()]);

        

       
    }

    private Element parseElement(String element) {
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
        } else if(element.equals("multiply") || element.equals("*")){
            return new MultiplyElmt();
        } else if(element.equals("divide") || element.equals("/")){
            return new DivideElmt();
        } else if(isVariable(element)){
            return new VariableElmt(element);
        }
       throw new IllegalArgumentException("Invalid Element: "+element + ". Line: " + getLine());//TODO: make proper exception
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
            str += elmt.typeString() + " ";
        }
        if(str.length()>0){
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
    
}
