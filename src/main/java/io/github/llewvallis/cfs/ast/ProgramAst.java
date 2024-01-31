package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AnalysisException;
import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ProgramAst extends Ast {

  @Getter private final List<FunctionAst> functions;

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
  public void accept(AstVisitor visitor) throws AnalysisException {
    visitor.visitProgram(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return functions;
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
