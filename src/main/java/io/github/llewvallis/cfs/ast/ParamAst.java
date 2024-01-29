package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ParamAst extends AstNode {

  private final IdentAst name;

  private final TyAst ty;

  public ParamAst(IdentAst name, TyAst ty) {
    this.name = name;
    this.ty = ty;
  }

  @Override
  public List<AstNode> getChildren() {
    return List.of(name, ty);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Parameter");
    node.addEdge(name.graphviz(builder), "Name");
    node.addEdge(ty.graphviz(builder), "Type");
    return node;
  }
}
