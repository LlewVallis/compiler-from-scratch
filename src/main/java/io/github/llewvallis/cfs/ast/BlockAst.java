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
public final class BlockAst extends Ast {

  @Getter private final List<StmtAst> stmts;

  public BlockAst(Span span, List<StmtAst> stmts) {
    super(span);
    this.stmts = new ArrayList<>(stmts);
  }

  @Override
  public void accept(AstVisitor visitor) {
    visitor.visitBlock(this);
  }

  @Override
  public List<? extends Ast> getChildren() {
    return stmts;
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    var node = builder.newNode("Block");

    for (var stmt : stmts) {
      node.addEdge(stmt.graphviz(builder));
    }

    return node;
  }
}
