package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntTyAst extends TyAst {

  @Override
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitIntTy(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return builder.newNode("int");
  }
}
