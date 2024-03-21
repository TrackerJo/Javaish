package com.trackerjo.javaish;

import java.util.List;
import java.util.Map;

public class TypeChecker {
    public sealed interface JavaishCheckedType permits StringType, FunctionType, BooleanType, NumberType {}
    public final record StringType() implements JavaishCheckedType {}
    public final record BooleanType() implements JavaishCheckedType {}
    public final record NumberType() implements JavaishCheckedType {}
    public final record FunctionType(List<JavaishCheckedType> args, JavaishCheckedType returnType) implements JavaishCheckedType {}

    public static JavaishCheckedType checkType(Map<String, JavaishCheckedType> context, ASTNode expr) {
        switch (expr) {
            case BinOp(ASTNode leftNode, ASTNode rightNode, Operator op) -> {
                JavaishCheckedType left = checkType(context, leftNode);
                JavaishCheckedType right = checkType(context, rightNode);
                if (op == Operator.ADD) {
                    if (left instanceof StringType && right instanceof StringType) {
                        return new StringType();
                    } else if (left instanceof NumberType && right instanceof NumberType) {
                        return new NumberType();
                    }
                }
                switch (op) {
                    case ADD, SUBTRACT, MULTIPLY, DIVIDE -> {
                        if (left instanceof NumberType && right instanceof NumberType) {
                            return new NumberType();
                        } else {
                            throw new RuntimeException("Unsupported types for operator +: " + left + " and " + right);
                        }
                    }
                }
            }
            case Application(ASTNode function, List<ASTNode> arguments) -> {
                JavaishCheckedType funcType = checkType(context, function);
                if (funcType instanceof FunctionType (var argTypes, var returnType)) {
                    if (argTypes.size() != arguments.size()) {
                        throw new RuntimeException("Wrong number of arguments for function " + function);
                    }
                    for (int i = 0; i < argTypes.size(); i++) {
                        JavaishCheckedType argType = checkType(context, arguments.get(i));
                        if (!argType.equals(argTypes.get(i))) {
                            throw new RuntimeException("Wrong type for argument " + i + " of function " + function);
                        }
                    }
                    return returnType;
                } else {
                    throw new RuntimeException("Cannot call non-function " + function);
                }
            }
            case 
            default -> throw new RuntimeException("Unknown expression: " + expr);
        }
    }
}
