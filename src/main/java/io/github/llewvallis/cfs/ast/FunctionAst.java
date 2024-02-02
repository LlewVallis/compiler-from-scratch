package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class FunctionAst extends Ast {

  @Getter private final IdentAst name;

  @Getter private final List<VarDeclAst> params;

  @Getter private final TyAst returnTy;

  @Getter private final BlockAst body;

  public FunctionAst(
      Span span, IdentAst name, List<VarDeclAst> params, TyAst returnTy, BlockAst body) {
    super(span);
    this.name = name;
    this.params = new ArrayList<>(params);
    this.returnTy = returnTy;
    this.body = body;
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitFunction(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    var results = new ArrayList<Ast>();
    results.add(name);
    results.addAll(params);
    results.add(returnTy);
    results.add(body);
    return results;
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Function");

    node.addEdge(name.graphviz(builder), "Name");

    for (var param : params) {
      node.addEdge(param.graphviz(builder), "Parameter");
    }

    node.addEdge(returnTy.graphviz(builder), "Return Type");
    node.addEdge(body.graphviz(builder), "Body");

    return node;
  }
}
