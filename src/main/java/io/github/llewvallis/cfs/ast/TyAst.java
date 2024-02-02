package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class TyAst extends Ast permits IntTyAst {
  public TyAst(Span span) {
    super(span);
  }
}
