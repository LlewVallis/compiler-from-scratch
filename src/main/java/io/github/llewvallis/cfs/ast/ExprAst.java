package io.github.llewvallis.cfs.ast;

public abstract sealed class ExprAst extends Ast
    permits AssignmentExprAst, IntLiteralExprAst, VarExprAst {}
