package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.List;
import lombok.*;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class AssignmentExprAst extends ExprAst {

  @Getter private final IdentAst variable;

  @Getter private final ExprAst value;

  @Getter @Setter private VarDeclAst decl = null;

  public AssignmentExprAst(Span span, IdentAst variable, ExprAst value) {
    super(span);
    this.variable = variable;
    this.value = value;
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitAssignmentExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(variable, value);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Assignment");

    node.addEdge(variable.graphviz(builder), "Variable");
    node.addEdge(value.graphviz(builder), "Value");

    return node;
  }
}
