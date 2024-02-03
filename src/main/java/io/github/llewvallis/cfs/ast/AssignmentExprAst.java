package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.List;
import lombok.*;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class AssignmentExprAst extends RValueExprAst {

  @Getter private final LValueExprAst lhs;

  @Getter private final RValueExprAst rhs;

  public AssignmentExprAst(Span span, LValueExprAst lhs, ExprAst rhs) {
    super(span);
    this.lhs = lhs;
    this.rhs = RValueExprAst.ensure(rhs);
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitAssignmentExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(lhs, rhs);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Assignment");
    node.addEdge(lhs.graphviz(builder), "LHS");
    node.addEdge(rhs.graphviz(builder), "RHS");
    return node;
  }
}
