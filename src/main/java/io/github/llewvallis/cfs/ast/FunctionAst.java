package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class FunctionAst extends AstNode {

  @Getter
  private final IdentAst name;

  @Getter
  private final List<ParamAst> parameters;

  @Getter
  private final TyAst returnTy;

  @Getter
  private final BlockAst body;

  public FunctionAst(IdentAst name, List<ParamAst> parameters, TyAst returnTy, BlockAst body) {
    this.name = name;
    this.parameters = new ArrayList<>(parameters);
    this.returnTy = returnTy;
    this.body = body;
  }

  @Override
  public List<AstNode> getChildren() {
    var results = new ArrayList<AstNode>();
    results.add(name);
    results.addAll(parameters);
    results.add(returnTy);
    results.add(body);
    return results;
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Function");

    node.addEdge(name.graphviz(builder), "Name");

    for (var param : parameters) {
      node.addEdge(param.graphviz(builder), "Parameter");
    }

    node.addEdge(returnTy.graphviz(builder), "Return Type");
    node.addEdge(body.graphviz(builder), "Body");

    return node;
  }
}
