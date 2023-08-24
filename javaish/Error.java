package javaish;

public class Error {
    public static void UnexpectedStmt(String stmt, int lineNumber) {
        System.err.println("Error: Unknown statement at line " + lineNumber + ":" + stmt);
        System.exit(0);
    }

    public static void UnexpectedElmt(String token, int lineNumber, int columnNumber) {
        System.err.println("Error: Unknown element at line " + lineNumber + " column " + columnNumber + ": " +  token);
        System.exit(0);
    }

    public static void TypeMismatch(String expected, String got, int lineNumber) {
        System.err.println("Error: Type mismatch at line " + lineNumber + ": Expected " + expected + ", got " + got);
        System.exit(0);
    }

    public static void InvalidType(String type, int lineNumber) {
        System.err.println("Error: Invalid type at line " + lineNumber + ": " + type);
        System.exit(0);
    }

    public static void UnclosedString(int lineNumber) {
        System.err.println("Error: Unclosed string at line " + lineNumber);
        System.exit(0);
    }

    public static void VariableAlreadyExists(String name) {
        System.err.println("Error: Variable " + name + " already exists!");
        System.exit(0);
    }

    public static void UnableToParse(String type, int lineNumber, String goal) {
        System.err.println("Error: Unable to parse " + type + " to " + goal + " at line " + lineNumber);
        System.exit(0);
    }

    public static void VariableNotDeclared(String name, int lineNumber) {
        System.err.println("Error: Variable " + name + " not declared at line " + lineNumber);
        System.exit(0);
    }

    public static void FunctionNotDeclared(String name, int lineNumber) {
        System.err.println("Error: Function " + name + " not declared at line " + lineNumber);
        System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber) {
        System.err.println("Error: Invalid function call at line " + lineNumber + ": " + name);
        System.exit(0);
    }

    public static void InvalidFunctionCall(String name, int lineNumber, String expected, String got) {
        System.err.println("Error: Invalid function call at line " + lineNumber + ": " + name + ": Expected " + expected + ", got " + got);
        System.exit(0);
    }

    public static void CantPerformMutation(String type, int lineNumber) {
        System.err.println("Error: Can't perform mutation on " + type + " at line " + lineNumber);
        System.exit(0);
    }
}
