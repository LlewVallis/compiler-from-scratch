package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class LValueExprAst extends ExprAst permits VarExprAst {

  public LValueExprAst(Span span) {
    super(span);
  }
}
