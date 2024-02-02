package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class StmtAst extends Ast
    permits ExprStmtAst, ReturnStmtAst, VarDeclStmtAst {
  public StmtAst(Span span) {
    super(span);
  }
}
