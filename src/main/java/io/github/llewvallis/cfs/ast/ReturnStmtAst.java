package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ReturnStmtAst extends StmtAst {

  @Getter private final ExprAst value;

  public ReturnStmtAst(ExprAst value) {
    this.value = value;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
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
