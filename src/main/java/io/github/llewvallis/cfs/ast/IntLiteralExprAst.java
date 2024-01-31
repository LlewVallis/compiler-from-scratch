package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntLiteralExprAst extends ExprAst {

  @Getter private final int value;

  public IntLiteralExprAst(int value) {
    this.value = value;
  }

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
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
