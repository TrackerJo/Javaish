package com.trackerjo.javaish;

import java.util.Map;

// plus(5, 4)
public class InterpreterV2 {
    public JavaishValV2 interpret(Map<String, JavaishValV2> context, ASTNode expression) {
        switch (expression) {
            case BinOp (ASTNode leftNode, ASTNode rightNode, Operator op) -> {
                JavaishValV2 left = interpret(context, leftNode);
                JavaishValV2 right = interpret(context, rightNode);
                switch (op) {
                    case ADD -> {
                        if (left instanceof JavaishInt (Integer val1) && right instanceof JavaishInt (Integer val2)) {
                            return new JavaishInt(val1 + val2);
                        } else if (left instanceof JavaishString (String val1) && right instanceof JavaishString (String val2)) {
                                return new JavaishString(val1 + val2);
                        } else if (left instanceof JavaishFloat (Float val1) && right instanceof JavaishFloat (Float val2)) {
                            return new JavaishFloat(val1 + val2);
                        } else {
                            throw new RuntimeException("Unsupported types for operator +: " + left + " and " + right);
                        }
                    }
                    case SUBTRACT -> {
                        // return new JavaishVal(left.getValue() - right.getValue());
                        throw new RuntimeException("This one's on you lil bro");
                    }
                    case MULTIPLY -> {
                        // return new JavaishVal(left.getValue() * right.getValue());
                        throw new RuntimeException("This one's on you lil bro");
                    }
                    case DIVIDE -> {
                        // return new JavaishVal(left.getValue() / right.getValue());
                        throw new RuntimeException("This one's on you lil bro");
                    }
                    default -> throw new RuntimeException("Unknown operator: " + op);
                }
            }
            default ->
                throw new RuntimeException("Unknown expression: " + expression);
        }
    }
}

