package javish;

import java.util.ArrayList;
import java.util.List;


import javish.Elements.MutationType;
import javish.Variables.VarType;

public class Statements {
    enum StmtType {
        FUNCTION, IF, WHILE, FOR, RETURN, CALL, ASSIGNMENT, DECLARATION, VARIABLE, MUTATION, END, ELSE, CLASS, ELSEIF
    }

    StmtType type;
    int line;
    boolean isBlock = false;
    List<Statements> body = new ArrayList<>();

    public List<Statements> getBody() {
        return body;
    }

    public int getLine() {
        return line;
    }

    public StmtType getType() {
        return type;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void addStatement(Statements stmt) {
        body.add(stmt);
    }
}

class ClassStmt extends Statements {
    
    public ClassStmt(int line) {
        this.line = line;
        
        this.type = StmtType.CLASS;
        this.isBlock = true;
    }

    public int getLine() {
        return line;
    }
}

class FunctionStmt extends Statements {
    String name;
    Argument[] args;
    
    public FunctionStmt(int line,String name, Argument[] args) {
        this.line = line;
        this.name = name;
        this.args = args;

        this.type = StmtType.FUNCTION;
        this.isBlock = true;
    }

    public String getName() {
        return name;
    }

    public Argument[] getArgs() {
        return args;
    }

    public int getLine() {
        return line;
    }
 
}

class IfStmt extends Statements {
    String condition;
    
    
    public IfStmt(int line,String condition) {
        this.line = line;
        this.condition = condition;
        
        this.type = StmtType.IF;
        this.isBlock = true;
    }

    public String getCondition() {
        return condition;
    }
    public int getLine() {
        return line;
    }


}

class WhileStmt extends Statements {
    String condition;

    public WhileStmt(int line,String condition) {
        this.line = line;
        this.condition = condition;

        this.type = StmtType.WHILE;
        this.isBlock = true;
    }
     public int getLine() {
        return line;
    }

    public String getCondition() {
        return condition;
    }
}

class ForStmt extends Statements {
    String condition;
    public ForStmt(int line,String condition) {
        this.line = line;
        this.condition = condition;
        this.type = StmtType.FOR;
        this.isBlock = true;
    }
     public int getLine() {
        return line;
    }

    public String getCondition() {
        return condition;
    }
}

class ReturnStmt extends Statements {
    String value;
    public ReturnStmt(int line,String value) {
        this.line = line;
        this.value = value;
        this.type = StmtType.RETURN;
    }

    public String getValue() {
        return value;
    }
     public int getLine() {
        return line;
    }
}

class CallStmt extends Statements {
    String name;
    String[] params;
    public CallStmt(int line,String name, String[] params) {
        this.line = line;
        this.name = name;
        this.params = params;
        this.type = StmtType.CALL;
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }

     public int getLine() {
        return line;
    }
}

class AssignmentStmt extends Statements {
    String name;
    String value;
    public AssignmentStmt(int line,String name, String value) {
        this.line = line;
        this.name = name;
        this.value = value;
        this.type = StmtType.ASSIGNMENT;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

     public int getLine() {
        return line;
    }
}

class DeclarationStmt extends Statements {
    String name;
    VarType varType;
    String value;
    public DeclarationStmt(int line,String name, VarType varType, String value) {
        this.line = line;
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.type = StmtType.DECLARATION;
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

     public int getLine() {
        return line;
    }
}

class VariableStmt extends Statements {
    String name;
    public VariableStmt(int line,String name) {
        this.line = line;
        this.name = name;
        this.type = StmtType.VARIABLE;
    }
}

class MutationStmt extends Statements {
    String varName;
    String value;
    MutationType mutationType;
    public MutationStmt(int line,String varName, String value, MutationType mutationType) {
        this.line = line;
        this.varName = varName;
        this.value = value;
        this.mutationType = mutationType;
        this.type = StmtType.MUTATION;
    }

    public String getVarName() {
        return varName;
    }

    public String getValue() {
        return value;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

     public int getLine() {
        return line;
    }
    
}

class EndBlockStmt extends Statements {
    public EndBlockStmt(int line) {
        this.line = line;
        this.type = StmtType.END;
    }

     public int getLine() {
        return line;
    }
}

class ElseStmt extends Statements {
    public ElseStmt(int line) {
        this.line = line;
        this.type = StmtType.ELSE;
    }

     public int getLine() {
        return line;
    }
}

class ElseIfStmt extends Statements {
    String condition;
    public ElseIfStmt(int line,String condition) {
        this.line = line;
        this.condition = condition;
        this.type = StmtType.ELSEIF;
    }

    public String getCondition() {
        return condition;
    }

     public int getLine() {
        return line;
    }
}


