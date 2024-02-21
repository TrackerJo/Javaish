package com.trackerjo.javaish;



public class Error {
    
    public static void UnexpectedStmt(String stmt, int lineNumber) {
        throw new RuntimeException("Error: Unknown statement at line " + lineNumber + ":" + stmt);
        //System.exit(0);
    }

    public static void UnexpectedElmt(String token, int lineNumber, int columnNumber) {
        throw new RuntimeException("Error: Unknown element at line " + lineNumber + " column " + columnNumber + ": " +  token);

    }

    public static void ReturnTypeMismatch(String functionName, String expected, String got, int lineNumber) {
        throw new RuntimeException("Error: Return type mismatch at line " + lineNumber + ": Function " + functionName + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void InvalidForLoop(int lineNumber) {
       
        throw new RuntimeException("Invalid for loop at line " + lineNumber);
       // System.exit(0);
    }

    public static void TypeMismatch(String expected, String got, int lineNumber) {
        throw new RuntimeException("Error: Type mismatch at line " + lineNumber + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void InvalidType(String type, int lineNumber) {
        throw new RuntimeException("Error: Invalid type at line " + lineNumber + ": " + type);
        //System.exit(0);
    }

    public static void UnclosedString(int lineNumber) {
        throw new RuntimeException("Error: Unclosed string at line " + lineNumber);
        //System.exit(0);
    }

    public static void InvalidVariableName(String name, int lineNumber) {
        throw new RuntimeException("Error: Invalid variable name at line " + lineNumber + ": " + name);
        //System.exit(0);
    }

    public static void InvalidFunctionName(String name, int lineNumber) {
        throw new RuntimeException("Error: Invalid function name at line " + lineNumber + ": " + name);
        //System.exit(0);

    }

   

    public static void UnableToParse(String type, int lineNumber, String goal) {
        throw new RuntimeException("Error: Unable to parse " + type + " to " + goal + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void VariableNotDeclared(String name, int lineNumber) {
        throw new RuntimeException("Error: Variable \"" + name + "\" not declared at line " + lineNumber);
        //System.exit(0);
    }

    public static void FunctionNotDeclared(String name, int lineNumber) {
        throw new RuntimeException("Error: Function " + name + " not declared at line " + lineNumber);
        //System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber) {
        throw new RuntimeException("Error: Invalid function call at line " + lineNumber + ": " + name);
        //System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber, String expected, String got) {
        throw new RuntimeException("Error: Invalid function call at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void CantPerformMutation(String type, int lineNumber) {
        throw new RuntimeException("Error: Can't perform mutation on " + type + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void FunctionAlreadyExists(String name) {
        throw new RuntimeException("Error: Variable or Function " + name + " already exists!");
        //System.exit(0);
    }

    public static void ArgumentLengthMismatch(String name, int lineNumber, int expected, int got) {
        throw new RuntimeException("Error: Argument length mismatch at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void ArgumentTypeMismatch(String name, int lineNumber, String expected, String got) {
        throw new RuntimeException("Error: Argument type mismatch at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void FunctionNotGlobal(String name, int lineNumber) {
        throw new RuntimeException("Error: Function " + name + " not global at line " + lineNumber);
        //System.exit(0);
    }

    public static void CantPerformOperation(String operation, String type, int lineNumber) {
        throw new RuntimeException("Error: Can't perform operation " + operation + " on " + type + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void IndexOutOfBounds(int index, int lineNumber, int arrayLength) {
        throw new RuntimeException("Error: Index bigger than array length at line " + lineNumber + ": Got:" + index + ", Max: " + (arrayLength - 1));
       // System.exit(0);
    }

    public static void UnclosedParenthesis(int lineNumber, int columnNumber) {
        throw new RuntimeException("Error: Unclosed parenthesis at line " + lineNumber + " column " + columnNumber);
       // System.exit(0);
    }

    public static void MissingPeriod(int lineNumber) {
        throw new RuntimeException("Error: Missing period at end of statement at line " + lineNumber);
      //  System.exit(0);
    }

    public static void UnclosedBracket(int lineNumber, int columnNumber) {
        throw new RuntimeException("Error: Unclosed bracket at line " + lineNumber + " column " + columnNumber);
       // System.exit(0);
    }

    public static void ListEmpty(int lineNumber, String listName) {
        throw new RuntimeException("Error: List " + listName + " empty at line " + lineNumber);
       // System.exit(0);
    }

     public static void FunctionAlreadyExists(String name, int lineNumber) {
        
        throw new RuntimeException("Function " + name + " already exists!");
       // System.exit(0);
    }


    public static void VariableAlreadyExists(String name, int lineNumber) {
        
        throw new RuntimeException("Variable " + name + " already exists!");
        //System.exit(0);
    }

    public static void PythonForWhenTranslator(String msg, int lineNumber) {
        
        throw new RuntimeException("Unable to translate to Python: " + msg + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void RobotNotImported(int lineNumber) {
        
        throw new RuntimeException("Robot not imported at line " + lineNumber);
        //System.exit(0);
    }

    public static void MissingClosingParenthesis(int lineNumber) {
        
        throw new RuntimeException("Missing closing parenthesis at line " + lineNumber);
        //System.exit(0);
    }

    public static void InvalidRobotAction(String type, int lineNumber) {
        
        throw new RuntimeException("Invalid robot action " + type + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void FunctionHasNoReturn(String name, int lineNumber) {
        
        throw new RuntimeException("Function \"" + name + "\" has no return at line " + lineNumber);
        //System.exit(0);
    }

    public static void ReservedName(String name, int lineNumber) {
        
        throw new RuntimeException("Reserved name \"" + name + "\" at line " + lineNumber);
        //System.exit(0);
    }

    
}
