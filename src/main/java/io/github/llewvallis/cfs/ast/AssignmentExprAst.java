package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class AssignmentExprAst extends ExprAst {

  @Getter private final IdentAst variable;

  @Getter private final ExprAst value;

  @Getter @Setter private VarDeclAst decl = null;

  public AssignmentExprAst(IdentAst variable, ExprAst value) {
    this.variable = variable;
    this.value = value;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitAssignmentExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return List.of(value, value);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Assignment");

    node.addEdge(variable.graphviz(builder), "Variable");
    node.addEdge(value.graphviz(builder), "Value");

    return node;
  }
}
