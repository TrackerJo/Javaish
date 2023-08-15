package javish;

import java.util.ArrayList;
import java.util.List;

import javish.Variables.VarType;

public class Elements {
    enum ElementTypes {
        FUNCTION, IF, WHILE, FOR, RETURN, CALL, ASSIGNMENT, DECLARATION, VARIABLE, MUTATION, END, ELSE, MAIN
    }

    enum MutationType {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    ElementTypes type;
    int line;
    boolean isBlock = false;
    List<Elements> body = new ArrayList<>();

    public ElementTypes getType() {
        return type;
    }

    public List<Elements> getBody() {
        return body;
    }
    
    
}

class Main extends Elements {
    
    public Main(int line) {
        this.line = line;
        
        this.type = ElementTypes.MAIN;
        this.isBlock = true;
    }
}

class Function extends Elements {
    String name;
    Argument[] args;
    
    public Function(int line,String name, Argument[] args) {
        this.line = line;
        this.name = name;
        this.args = args;
        
        this.type = ElementTypes.FUNCTION;
        this.isBlock = true;
    }

    public String getName() {
        return name;
    }

    public Argument[] getArgs() {
        return args;
    }
 
}

class If extends Elements {
    String condition;
   
    Elements elseE;
    public If(int line,String condition, Elements elseE) {
        this.line = line;
        this.condition = condition;
       
        this.elseE = elseE;
        this.type = ElementTypes.IF;
        this.isBlock = true;
    }

    public Elements getElseE() {
        return elseE;
    }

    public String getCondition() {
        return condition;
    }
}

class While extends Elements {
    String condition;
    
    public While(int line,String condition) {
        this.line = line;
        this.condition = condition;
        
        this.type = ElementTypes.WHILE;
        this.isBlock = true;
    }
}

class For extends Elements {
    String condition;
    List<Elements> body;
    public For(int line,String condition) {
        this.line = line;
        this.condition = condition;
        
        this.type = ElementTypes.FOR;
        this.isBlock = true;
    }
}

class Return extends Elements {
    String value;
    public Return(int line,String value) {
        this.line = line;
        this.value = value;
        this.type = ElementTypes.RETURN;
    }

    public String getValue() {
        return value;
    }
}

class Call extends Elements {
    String name;
    String[] params;
    public Call(int line,String name, String[] params) {
        this.line = line;
        this.name = name;
        this.params = params;
        this.type = ElementTypes.CALL;
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }
}

class Assignment extends Elements {
    String name;
    String value;
    public Assignment(int line,String name, String value) {
        this.line = line;
        this.name = name;
        this.value = value;
        this.type = ElementTypes.ASSIGNMENT;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}

class Declaration extends Elements {
    String name;
    VarType varType;
    String value;
    public Declaration(int line,String name, VarType varType, String value) {
        this.line = line;
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.type = ElementTypes.DECLARATION;
    }

    public String getName() {
        return name;
    }

    public VarType getVarType() {
        return varType;
    }

    public String getValue() {
        return value;
    }
}

class Variable extends Elements {
    String name;
    public Variable(int line,String name) {
        this.line = line;
        this.name = name;
        this.type = ElementTypes.VARIABLE;
    }
}

class Mutation extends Elements {
    String varName;
    String value;
    MutationType mutationType;
    public Mutation(int line,String varName, String value, MutationType mutationType) {
        this.line = line;
        this.varName = varName;
        this.value = value;
        this.mutationType = mutationType;
        this.type = ElementTypes.MUTATION;
    }
    
    public MutationType getMutationType() {
        return mutationType;
    }
    public String getValue() {
        return value;
    }
    public String getVarName() {
        return varName;
    }
}

class EndBlock extends Elements {
    public EndBlock(int line) {
        this.line = line;
        this.type = ElementTypes.END;
    }

     public int getLine() {
        return line;
    }
}

class Else extends Elements {
    

    public Else(int line) {
        this.line = line;
        this.type = ElementTypes.ELSE;
        
        this.isBlock = true;
    }

     public int getLine() {
        return line;
    }
}


