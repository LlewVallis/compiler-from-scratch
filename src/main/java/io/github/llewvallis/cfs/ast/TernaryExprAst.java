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
public final class TernaryExprAst extends RValueExprAst {

  @Getter private final RValueExprAst condition;

  @Getter private final RValueExprAst trueCase;

  @Getter private final RValueExprAst falseCase;

  public TernaryExprAst(Span span, ExprAst condition, ExprAst trueCase, ExprAst falseCase) {
    super(span);
    this.condition = RValueExprAst.ensure(condition);
    this.trueCase = RValueExprAst.ensure(trueCase);
    this.falseCase = RValueExprAst.ensure(falseCase);
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitTernaryExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(condition, trueCase, falseCase);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("?:");

    node.addEdge(condition.graphviz(builder), "Condition");
    node.addEdge(trueCase.graphviz(builder), "True");
    node.addEdge(falseCase.graphviz(builder), "False");

    return node;
  }
}
