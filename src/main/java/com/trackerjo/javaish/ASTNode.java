package com.trackerjo.javaish;

import java.util.List;

import com.trackerjo.javaish.JavaishVal.JavaishType;

public sealed interface ASTNode permits BinOp, Application, Mutation, Declaration, Block {
    // public static final String tag = "";
}

enum Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

record BinOp(ASTNode left, ASTNode right, Operator op) implements ASTNode {}
record Application(ASTNode function, List<ASTNode> arguments) implements ASTNode {}

// final class BinOp implements ASTNode {
//     String tag = "BinOp";
//     ASTNode left;
//     ASTNode right;
//     String op;
// }

// final class Application implements ASTNode {
//     String tag = "Application";
//     ASTNode function;
//     List<ASTNode> arguments;
// }

final class Mutation implements ASTNode {
    String tag = "Mutation";
    String mutationType; // + | - | * | /
    ASTNode left;
    ASTNode right;
}

final class Declaration implements ASTNode {
    String tag = "Declaration";
    String name; // + | - | * | /
    JavaishType type;
    ASTNode left;
    ASTNode right;
}

final class Block implements ASTNode {
    String tag = "Block";
    List<ASTNode> statements;
}