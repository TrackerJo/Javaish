package com.trackerjo.javaish;



import java.util.ArrayList;
import java.util.List;

import com.trackerjo.javaish.JavaishVal.JavaishType;


public class Statements {
    enum StmtType {
        FUNCTION, IF, WHILE, FOREACH, RETURN, CALL, ASSIGNMENT, DECLARATION, VARIABLE, MUTATION, END, ELSE, CLASS, ELSEIF, FORWHEN, PRINT, SHOWMSGBOX, REMOVEAT, REMOVEFROM, REMOVEALLFROM, COMMENT, IMPORT, ROBOT, SET
    }

    enum MutationType {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    enum RobotType {
        MOVE, SPEAK, STAND, SIT, BLINK, TWINKLE, RANDOMEYES, BATTERY, VOLUME, CROUCH, LYINGBACK, NAME, PICTURE, LYINGBELLY
    }

    StmtType type;
    int line;
    boolean isBlock = false;
    List<Statements> body = new ArrayList<>();
    List<String> variableNames = new ArrayList<>();

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

    public void addVariableName(String name) {
        variableNames.add(name);
    }

    public boolean containsVariable(String name) {
        return variableNames.contains(name);
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
    Expression condition;
    
    
    public IfStmt(int line,Expression condition) {
        this.line = line;
        this.condition = condition;
        
        this.type = StmtType.IF;
        this.isBlock = true;
    }

    public Expression getCondition() {
        return condition;
    }
    public int getLine() {
        return line;
    }


}

class WhileStmt extends Statements {
    Expression condition;

    public WhileStmt(int line,Expression condition) {
        this.line = line;
        this.condition = condition;

        this.type = StmtType.WHILE;
        this.isBlock = true;
    }
     public int getLine() {
        return line;
    }

    public Expression getCondition() {
        return condition;
    }
}

class ForWhenStmt extends Statements {
    String incVar;
    Expression increment;
    Expression condition;
    public ForWhenStmt(int line,Expression condition, Expression increment, String lineVar) {
        this.line = line;
        this.condition = condition;
        this.increment = increment;
        this.type = StmtType.FORWHEN;
        this.incVar = lineVar;
        this.isBlock = true;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getIncrement() {
        return increment;
    }
    
    public String getIncVar() {
        return incVar;
    }

     public int getLine() {
        return line;
    }
}

class ForEachStmt extends Statements {
    String tempVar;
    String listVar;
    public ForEachStmt(int line,String tempVar, String listVar) {
        this.line = line;
        this.tempVar = tempVar;
        this.listVar = listVar;
        this.type = StmtType.FOREACH;
        this.isBlock = true;
    }

    public String getTempVar() {
        return tempVar;
    }

    public String getListVar() {
        return listVar;
    }

    public int getLine() {
        return line;
    }
}

class ReturnStmt extends Statements {
    Expression value;
    boolean hasReturn; 
    public ReturnStmt(int line,Expression value, boolean hasReturn) {
        this.line = line;
        this.value = value;
        this.type = StmtType.RETURN;
        this.hasReturn = hasReturn;
    }

    public Expression getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

}

class CallStmt extends Statements {
    String name;
    Expression[] params;
    public CallStmt(int line,String name, Expression[] params) {
        this.line = line;
        this.name = name;
        this.params = params;
        this.type = StmtType.CALL;
    }

    public String getName() {
        return name;
    }

    public Expression[] getParams() {
        return params;
    }

     public int getLine() {
        return line;
    }
}

class AssignmentStmt extends Statements {
    String name;
    Expression value;
    public AssignmentStmt(int line,String name, Expression value) {
        this.line = line;
        this.name = name;
        this.value = value;
        this.type = StmtType.ASSIGNMENT;
    }

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

     public int getLine() {
        return line;
    }
}

class DeclarationStmt extends Statements {
    String name;
    JavaishType varType;
    Expression value;
    public DeclarationStmt(int line,String name, JavaishType varType, Expression value) {
        this.line = line;
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.type = StmtType.DECLARATION;
    }
     public String getName() {
        return name;
    }

    public JavaishType getVarType() {
        return varType;
    }

    public Expression getValue() {
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
    Expression value;
    MutationType mutationType;
    public MutationStmt(int line,String varName, Expression value, MutationType mutationType) {
        this.line = line;
        this.varName = varName;
        this.value = value;
        this.mutationType = mutationType;
        this.type = StmtType.MUTATION;
    }

    public String getVarName() {
        return varName;
    }

    public Expression getValue() {
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
    Expression condition;
    public ElseIfStmt(int line,Expression condition) {
        this.line = line;
        this.condition = condition;
        this.type = StmtType.ELSEIF;
    }

    public Expression getCondition() {
        return condition;
    }

     public int getLine() {
        return line;
    }
}

class PrintStmt extends Statements {
    Expression value;
    public PrintStmt(int line,Expression value) {
        this.line = line;
        this.value = value;
        this.type = StmtType.PRINT;
    }

    public Expression getValue() {
        return value;
    }

     public int getLine() {
        return line;
    }
}

class ShowMsgBoxStmt extends Statements {
    Expression value;
    public ShowMsgBoxStmt(int line,Expression value) {
        this.line = line;
        this.value = value;
        this.type = StmtType.SHOWMSGBOX;
    }

    public Expression getValue() {
        return value;
    }

     public int getLine() {
        return line;
    }
}

class RemoveAtStmt extends Statements {
    Expression location;
    String listName;
    
    public RemoveAtStmt(int line, Expression location, String name){
        this.location = location;
        this.listName = name;
        this.type = StmtType.REMOVEAT;
        this.line = line;
    }

    public Expression getLocation() {
        return location;
    }

    public String getListName() {
        return listName;
    }


}

class RemoveFromStmt extends Statements {
    Expression value;
    String listName;

    
    public RemoveFromStmt(int line,Expression value, String name){
        this.value = value;
        this.listName = name;
        this.type = StmtType.REMOVEFROM;
        this.line = line;
    }

    public Expression getValue() {
        return value;
    }

    public String getListName() {
        return listName;
    }
}

class RemoveAllFromStmt extends Statements {
    String listName;
    Expression value;

    
    public RemoveAllFromStmt(int line,String name, Expression value){
        this.listName = name;
        this.type = StmtType.REMOVEALLFROM;
        this.value = value;
        this.line = line;
    }

    public String getListName() {
        return listName;
    }
    
    public Expression getValue() {
        return value;
    }
}

class CommentStmt extends Statements {
    String comment;
    public CommentStmt(int line,String comment) {
        this.line = line;
        this.comment = comment;
        this.type = StmtType.COMMENT;
    }

    public String getComment() {
        return comment;
    }

     public int getLine() {
        return line;
    }
}

class ImportStmt extends Statements {
    String importName;
    public ImportStmt(int line,String importName) {
        this.line = line;
        this.importName = importName;
        this.type = StmtType.IMPORT;
    }

    public String getImportName() {
        return importName;
    }

     public int getLine() {
        return line;
    }
}

class RobotStmt extends Statements {
    RobotType robotType;
    Expression[] value;
    public RobotStmt(int line,RobotType robotType, Expression[] value) {
        this.line = line;
        this.robotType = robotType;
        this.value = value;
        this.type = StmtType.ROBOT;
    }

    public RobotType getRobotType() {
        return robotType;
    }

    public Expression[] getValue() {
        return value;
    }

     public int getLine() {
        return line;
    }
}

class SetStmt extends Statements {
    String listName;
    ListValElmt list;
    Expression value;
    public SetStmt(int line,String name, Expression value, ListValElmt list) {
        this.line = line;
        this.listName = name;
        this.value = value;
        this.type = StmtType.SET;
        this.list = list;
    }

    public String getName() {
        return listName;
    }

    public Expression getValue() {
        return value;
    }

    public ListValElmt getListVal() {
        return list;
    }

     public int getLine() {
        return line;
    }
}