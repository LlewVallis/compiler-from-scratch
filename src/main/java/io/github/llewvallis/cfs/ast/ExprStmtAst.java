package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ExprStmtAst extends StmtAst {

  @Getter private final ExprAst expr;

  public ExprStmtAst(Span span, ExprAst expr) {
    super(span);
    this.expr = expr;
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitExprStmt(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(expr);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return expr.graphviz(builder);
  }
}
