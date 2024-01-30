package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class BlockAst extends AstNode {

  @Getter private final List<StmtAst> stmts;

  public BlockAst(List<StmtAst> stmts) {
    this.stmts = new ArrayList<>(stmts);
  }

  @Override
  public List<AstNode> getChildren() {
    return new ArrayList<>(stmts);
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
