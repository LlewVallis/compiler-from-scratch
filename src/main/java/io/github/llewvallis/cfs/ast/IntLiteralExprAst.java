package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntLiteralExprAst extends ExprAst {

  @Getter private final int value;

  public IntLiteralExprAst(Span span, int value) {
    super(span);
    this.value = value;
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitIntLiteralExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return builder.newNode(Integer.toString(value));
  }
}
