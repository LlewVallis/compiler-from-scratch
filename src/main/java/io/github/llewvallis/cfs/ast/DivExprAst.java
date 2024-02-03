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
public final class DivExprAst extends RValueExprAst {

  @Getter private final RValueExprAst lhs;

  @Getter private final RValueExprAst rhs;

  public DivExprAst(Span span, ExprAst lhs, ExprAst rhs) {
    super(span);
    this.lhs = RValueExprAst.ensure(lhs);
    this.rhs = RValueExprAst.ensure(rhs);
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitDivExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(lhs, rhs);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("/");
    node.addEdge(lhs.graphviz(builder), "LHS");
    node.addEdge(rhs.graphviz(builder), "RHS");
    return node;
  }
}
