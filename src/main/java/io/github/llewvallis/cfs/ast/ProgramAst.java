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
public final class ProgramAst extends AstNode {

  @Getter
  private final List<FunctionAst> functions;

  public ProgramAst(List<FunctionAst> functions) {
    this.functions = new ArrayList<>(functions);
  }

  public FunctionAst getFunction(String name) {
    for (FunctionAst function : functions) {
      if (function.getName().getContent().equals(name)) {
        return function;
      }
    }

    return null;
  }

  @Override
  public List<AstNode> getChildren() {
    return new ArrayList<>(functions);
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Program");

    for (var function : functions) {
      node.addEdge(function.graphviz(builder));
    }

    return node;
  }
}
