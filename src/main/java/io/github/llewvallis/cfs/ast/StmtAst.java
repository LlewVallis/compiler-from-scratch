package io.github.llewvallis.cfs.ast;

public abstract sealed class StmtAst extends Ast
    permits ExprStmtAst, ReturnStmtAst, VarDeclStmtAst {}
