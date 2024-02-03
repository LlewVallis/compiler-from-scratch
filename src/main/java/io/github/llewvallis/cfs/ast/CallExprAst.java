package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.ast.analysis.AstVisitor;
import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import io.github.llewvallis.cfs.reporting.Span;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class CallExprAst extends RValueExprAst {

  @Getter private final IdentAst function;

  @Getter private final List<RValueExprAst> args;

  public CallExprAst(Span span, IdentAst function, List<ExprAst> args) {
    super(span);
    this.function = function;
    this.args = args.stream().map(RValueExprAst::ensure).collect(Collectors.toList());
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitCallExpr(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    var children = new ArrayList<Ast>();
    children.add(function);
    children.addAll(args);
    return children;
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Call");

    node.addEdge(function.graphviz(builder), "Function");

    for (var arg : args) {
      node.addEdge(arg.graphviz(builder), "Argument");
    }

    return node;
  }
}
