package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class RValueExprAst extends ExprAst
    permits AddExprAst,
        AssignmentExprAst,
        CallExprAst,
        DivExprAst,
        IntLiteralExprAst,
        IntoRValueExprAst,
        LogicalAndExprAst,
        LogicalOrExprAst,
        MulExprAst,
        NegExprAst,
        SubExprAst,
        TernaryExprAst {

  public RValueExprAst(Span span) {
    super(span);
  }

  public static RValueExprAst ensure(ExprAst expr) {
    return switch (expr) {
      case LValueExprAst lValue -> new IntoRValueExprAst(lValue.getSpan(), lValue);
      case RValueExprAst rValue -> rValue;
      case null -> null;
    };
  }
}
