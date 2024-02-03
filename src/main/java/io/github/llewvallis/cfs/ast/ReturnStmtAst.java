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
public final class ReturnStmtAst extends StmtAst {

  @Getter private final RValueExprAst value;

  public ReturnStmtAst(Span span, ExprAst value) {
    super(span);
    this.value = RValueExprAst.ensure(value);
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitReturnStmt(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(value);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Return");
    node.addEdge(value.graphviz(builder));
    return node;
  }
}
