package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;

public abstract sealed class AstNode
    permits BlockAst, ExprAst, FunctionAst, IdentAst, ParamAst, ProgramAst, StmtAst, TyAst {

  public abstract List<AstNode> getChildren();

  public abstract GraphvizNode graphviz(GraphvizBuilder builder);

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
