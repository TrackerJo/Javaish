package com.trackerjo.javaish;



import java.util.ArrayList;
import java.util.List;

import com.trackerjo.javaish.JavaishVal.JavaishType;
import com.trackerjo.javaish.Statements.RobotType;


public class Element {
    public enum ElementType {
        PLUS, MINUS, DIVIDE, MULTIPLY, FLOAT, INTEGER, VARIABLE, FUNCTION, EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, STRING, BOOL, AND, OR, NOT, EXPRESSION, CAST, SHOWINPUTBOX, LIST, LISTVAL, ARRAYLENGTH, RobotActionElmt
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
        } else if(type == ElementType.CAST){
            CastElmt cast = (CastElmt) this;
            return "CAST(" + cast.castType.toString() + ", " + cast.element.toString() + ")";
        } else if(type == ElementType.SHOWINPUTBOX){
            ShowInputBoxElmt showInputBox = (ShowInputBoxElmt) this;
            return "SHOWINPUTBOX(" + showInputBox.value.toString() + ")";
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

    public float getValue(){
        return value;
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

    public int getValue(){
        return value;
    }

}

class VariableElmt extends Element {
    public String name;

    public VariableElmt(String name) {
        type = ElementType.VARIABLE;
        this.name = name;
       
    }

    public String toString(){
        return "VAR(" + name + ")";
    }

    public String getName(){
        return name;
    }

  
}

class FunctionElmt extends Element {
    public String name;
    public Expression[] params;

    public FunctionElmt(String name, Expression[] params) {
        type = ElementType.FUNCTION;
        this.name = name;
        this.params = params;
    }

    public String getName(){
        return name;
    }

    public Expression[] getParams(){
        return params;
    }

    public String toString(){
        String str = name + "(";
        for (Expression expression : params) {
            str += expression.toString() + ", ";
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

    public String getValue(){
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

    public boolean getValue(){
        return value;
    }
}

class NotElmt extends Element {
  Expression expression;
    public NotElmt(Expression expression) {
        type = ElementType.NOT;
        this.expression = expression;
    }

    public String toString(){
        return "!" + expression.toString();
    }

    public Expression getExpression(){
        return expression;
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

    public Expression getExpression(){
        return expression;
    }
}

class CastElmt extends Element {
    public JavaishType castType;
    public Expression element;

    public CastElmt(JavaishType type, Expression element) {
       
        this.type = ElementType.CAST;
        this.castType = type;
        this.element = element;
    }

    public String toString(){
        return  "CAST(" + castType.toString() + ", " + element.toString() + ")";
    }

    public JavaishType getCastType(){
        return castType;
    }

    public Expression getExpression(){
        return element;
    }
}

class ShowInputBoxElmt extends Element {
    Expression value;
    public ShowInputBoxElmt(Expression value) {

        this.value = value;
        this.type = ElementType.SHOWINPUTBOX;
    }

    public Expression getValue() {
        return value;
    }

}

class ListElmt extends Element{
    List<Expression> list;
    JavaishType listType;
    public ListElmt(List<Expression> list, JavaishType listType){
        this.list = list;
        this.type = ElementType.LIST;
        this.listType = listType;
    }

    public List<Expression> getList(){
        return list;
    }

    public JavaishType getListType(){
        return listType;
    }

    public String getListTypeString(){
       switch (listType) {
        case FLOAT:
            return "float";
            
        case INT:
            return "int";
        
        case STRING:
            return "String";
        case BOOLEAN:
            return "boolean";
        default:
            break;
       }
         return "";
    }

    public String toString(){
        String str = "[";
        for (Expression expression : list) {
            str += expression.toString() + ", ";
        }
        str = str.substring(0, str.length() - 2);
        str += "]";
        return str;
    }
}

class ListValElmt extends Element{
    String listName;
    Expression index;
    public ListValElmt(String listName, Expression index){
        this.listName = listName;
        this.index = index;
        this.type = ElementType.LISTVAL;
    }

    public String getListName(){
        return listName;
    }

    public Expression getIndex(){
        return index;
    }

    public String toString(){
        return listName + "[" + index.toString() + "]";
    }
}

class ArrayLengthElmt extends Element{
    String arrayName;
    public ArrayLengthElmt(String arrayName){
        this.arrayName = arrayName;
        this.type = ElementType.ARRAYLENGTH;
    }

    public String getArrayName(){
        return arrayName;
    }

    public String toString(){
        return arrayName + ".length";
    }
}

class RobotActionElmt extends Element{
    RobotType action;
    List<Expression> params = new ArrayList<Expression>();
    public RobotActionElmt(RobotType action, List<Expression> params){
        this.action = action;
        this.type = ElementType.RobotActionElmt;
        this.params = params;
    }

    public RobotType getAction(){
        return action;
    }

    public String toString(){
        return action + "(" + params.toString() + ")";
    }

    public List<Expression> getParams(){
        return params;
    }
}