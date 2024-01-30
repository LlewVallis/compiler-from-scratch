package io.github.llewvallis.cfs.ast;

import io.github.llewvallis.cfs.graphviz.GraphvizBuilder;
import io.github.llewvallis.cfs.graphviz.GraphvizNode;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IdentAst extends AstNode {

  @Getter private final String content;

  public IdentAst(String content) {
    this.content = content;
  }

  @Override
  public List<AstNode> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public GraphvizNode graphviz(GraphvizBuilder builder) {
    return builder.newNode(content);
  }
}
