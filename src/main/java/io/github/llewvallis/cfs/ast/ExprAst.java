package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class ExprAst extends Ast
    permits AssignmentExprAst, IntLiteralExprAst, VarExprAst {

  public ExprAst(Span span) {
    super(span);
  }
}
