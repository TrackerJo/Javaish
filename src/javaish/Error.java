package javaish;



public class Error {
    
    public static void UnexpectedStmt(String stmt, int lineNumber) {
        System.err.println("Error: Unknown statement at line " + lineNumber + ":" + stmt);
        //System.exit(0);
    }

    public static void UnexpectedElmt(String token, int lineNumber, int columnNumber) {
        System.err.println("Error: Unknown element at line " + lineNumber + " column " + columnNumber + ": " +  token);
        //System.exit(0);
    }

    public static void TypeMismatch(String expected, String got, int lineNumber) {
        System.err.println("Error: Type mismatch at line " + lineNumber + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void InvalidType(String type, int lineNumber) {
        System.err.println("Error: Invalid type at line " + lineNumber + ": " + type);
        //System.exit(0);
    }

    public static void UnclosedString(int lineNumber) {
        System.err.println("Error: Unclosed string at line " + lineNumber);
        //System.exit(0);
    }

    public static void VariableAlreadyExists(String name) {
        System.err.println("Error: Variable " + name + " already exists!");
        //System.exit(0);
    }

    public static void UnableToParse(String type, int lineNumber, String goal) {
        System.err.println("Error: Unable to parse " + type + " to " + goal + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void VariableNotDeclared(String name, int lineNumber) {
        System.err.println("Error: Variable " + name + " not declared at line " + lineNumber);
        //System.exit(0);
    }

    public static void FunctionNotDeclared(String name, int lineNumber) {
        System.err.println("Error: Function " + name + " not declared at line " + lineNumber);
        //System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber) {
        System.err.println("Error: Invalid function call at line " + lineNumber + ": " + name);
        //System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber, String expected, String got) {
        System.err.println("Error: Invalid function call at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void CantPerformMutation(String type, int lineNumber) {
        System.err.println("Error: Can't perform mutation on " + type + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void FunctionAlreadyExists(String name) {
        System.err.println("Error: Function " + name + " already exists!");
        //System.exit(0);
    }

    public static void ArgumentLengthMismatch(String name, int lineNumber, int expected, int got) {
        System.err.println("Error: Argument length mismatch at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void ArgumentTypeMismatch(String name, int lineNumber, String expected, String got) {
        System.err.println("Error: Argument type mismatch at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        //System.exit(0);
    }

    public static void FunctionNotGlobal(String name, int lineNumber) {
        System.err.println("Error: Function " + name + " not global at line " + lineNumber);
        //System.exit(0);
    }

    public static void CantPerformOperation(String operation, String type, int lineNumber) {
        System.err.println("Error: Can't perform operation " + operation + " on " + type + " at line " + lineNumber);
        //System.exit(0);
    }

    public static void IndexOutOfBounds(int index, int lineNumber, int arrayLength) {
        System.err.println("Error: Index bigger than array length at line " + lineNumber + ": Got:" + index + ", Max: " + (arrayLength - 1));
       // System.exit(0);
    }

    public static void UnclosedParenthesis(int lineNumber, int columnNumber) {
        System.err.println("Error: Unclosed parenthesis at line " + lineNumber + " column " + columnNumber);
       // System.exit(0);
    }

    public static void MissingPeriod(int lineNumber) {
        System.err.println("Error: Missing period at end of statement at line " + lineNumber);
      //  System.exit(0);
    }

    public static void UnclosedBracket(int lineNumber, int columnNumber) {
        System.err.println("Error: Unclosed bracket at line " + lineNumber + " column " + columnNumber);
       // System.exit(0);
    }

    public static void ListEmpty(int lineNumber, String listName) {
        System.err.println("Error: List " + listName + " empty at line " + lineNumber);
       // System.exit(0);
    }
}
