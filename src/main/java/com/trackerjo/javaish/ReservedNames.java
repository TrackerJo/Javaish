package com.trackerjo.javaish;

public class ReservedNames {
    // Create a list of reserved names
    public static String[] reservedNames = {
       "while", "for", "if", "else", "function", "return", "break", "print", "input", "int", "float", "String", "bool", "true", "false", "toString", "toInt", "toFloat", "toBool", "int[]", "float[]", "String[]", "bool[]", "let", "add", "subtract", "multiply", "divide", "mod", "and", "or", "not", "greater", "less", "than", "equal", "to", "plus", "minus", "remove", "removeAll", "removeAt", "import", "robot", "times", "by", "and", "length", "of"
    };

    public static boolean isReserved(String name) {
        for (String reservedName : reservedNames) {
            if (name.equals(reservedName)) {
                return true;
            }
        }
        return false;
    }
}
