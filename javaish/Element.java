package javaish;

import java.util.ArrayList;
import java.util.List;

import javaish.Variables.VarType;

public class Element {
    enum ElementType {
        PLUS, MINUS, DIVIDE, MULTIPLY, FLOAT, INTEGER, VARIABLE, FUNCTION, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, STRING, BOOL, AND, OR, NOT, EXPRESSION, CAST
    }

    ElementType type;

    public ElementType getType(){
        
        return type;
    }
    
    public String typeString(){
        if(type == ElementType.EXPRESSION){
            ExpressionElmt expression = (ExpressionElmt) this;
            Element[] elements = expression.expression.getElements();
            String str = "EXPRESSION(";
            for (Element element : elements) {
                str += element.typeString() + " ";
            }
            str = str.substring(0, str.length() - 1);
            str += ")";
            return str;
        }
        return type.toString();
    }
}

class AndElmt extends Element {
    public AndElmt() {
        type = ElementType.AND;
    }

    public String toString(){
        return "&&";
    }

}

class OrElmt extends Element {
    public OrElmt() {
        type = ElementType.OR;
    }

    public String toString(){
        return "||";
    }

   
}

class PlusElmt extends Element {
    public PlusElmt() {
        type = ElementType.PLUS;
    }

    public String toString(){
        return "+";
    }

}

class MinusElmt extends Element {
    public MinusElmt() {
        type = ElementType.MINUS;
    }

    public String toString(){
        return "-";
    }

}

class DivideElmt extends Element {
    public DivideElmt() {
        type = ElementType.DIVIDE;
    }

    public String toString(){
        return "/";
    }

}

class MultiplyElmt extends Element {
    public MultiplyElmt() {
        type = ElementType.MULTIPLY;
    }

    public String toString(){
        return "*";
    }

}

class FloatElmt extends Element {
    public float value;

    public FloatElmt(float value) {
        type = ElementType.FLOAT;
        this.value = value;
    }

    public String toString(){
        return Float.toString(value);
    }

}

class IntElmt extends Element {
    public int value;

    public IntElmt(int value) {
        type = ElementType.INTEGER;
        this.value = value;
    }

    public String toString(){
        return Integer.toString(value);
    }

}

class VariableElmt extends Element {
    public String name;

    public VariableElmt(String name) {
        type = ElementType.VARIABLE;
        this.name = name;
       
    }

    public String toString(){
        return name;
    }

  
}

class FunctionElmt extends Element {
    public String name;
    public List<Element[]> args;

    public FunctionElmt(String name, List<Element[]> args) {
        type = ElementType.FUNCTION;
        this.name = name;
        this.args = args;
    }

    public String toString(){
        String str = name + "(";
        for (Element[] argA : args) {
            String argStr = "";
            for (Element arg : argA){
                argStr += arg.toString() + " ";
            }
            str += argStr + ", ";
        }
        str = str.substring(0, str.length() - 2);
        str += ")";
        return str;
    }


   
}


class EqualElmt extends Element {
    public EqualElmt() {
        type = ElementType.EQUAL;
    }

    public String toString(){
        return "==";
    }

}

class NotEqualElmt extends Element {
    public NotEqualElmt() {
        type = ElementType.NOT_EQUAL;
    }

    public String toString(){
        return "!=";
    }

}

class LessThanElmt extends Element {
    public LessThanElmt() {
        type = ElementType.LESS_THAN;
    }

    public String toString(){
        return "<";
    }

}

class GreaterThanElmt extends Element {
    public GreaterThanElmt() {
        type = ElementType.GREATER_THAN;
    }

    public String toString(){
        return ">";
    }
}

class LessThanEqualElmt extends Element {
    public LessThanEqualElmt() {
        type = ElementType.LESS_THAN_EQUAL;
    }

    public String toString(){
        return "<=";
    }

}

class GreaterThanEqualElmt extends Element {
    public GreaterThanEqualElmt() {
        type = ElementType.GREATER_THAN_EQUAL;
    }

    public String toString(){
        return ">=";
    }
}

class StringElmt extends Element {
    public String value;

    public StringElmt(String value) {
        type = ElementType.STRING;
        this.value = value;
    }

    public String toString(){
        return value;
    }
}

class BoolElmt extends Element {
    public boolean value;

    public BoolElmt(boolean value) {
        type = ElementType.BOOL;
        this.value = value;
    }

    public String toString(){
        return Boolean.toString(value);
    }
}

class NotElmt extends Element {
    public NotElmt() {
        type = ElementType.NOT;
    }

    public String toString(){
        return "!";
    }
}

class ExpressionElmt extends Element {
    public Expression expression;

    public ExpressionElmt(Expression expression) {
        type = ElementType.EXPRESSION;
        this.expression = expression;
    }

    public String toString(){
        return  "EXPRESSION(" + expression.toString() + ")";
    }
}

class CastElmt extends Element {
    public VarType castType;
    public Element element;

    public CastElmt(VarType type, Element element) {
       
        this.type = ElementType.CAST;
        this.castType = type;
        this.element = element;
    }

    public String toString(){
        return  "CAST(" + type.toString() + ", " + element.toString() + ")";
    }
}



