package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntTyAst extends TyAst {

  public IntTyAst(Span span) {
    super(span);
  }

  @Override
  public void accept(AstVisitor visitor) {
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
