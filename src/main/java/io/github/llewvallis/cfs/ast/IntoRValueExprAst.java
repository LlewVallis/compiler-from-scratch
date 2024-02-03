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
public final class IntoRValueExprAst extends RValueExprAst {

  @Getter private final LValueExprAst lValue;

  public IntoRValueExprAst(Span span, LValueExprAst lValue) {
    super(span);
    this.lValue = lValue;
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitIntoRValueExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(lValue);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Into RValue");
    node.addEdge(lValue.graphviz(builder));
    return node;
  }
}
