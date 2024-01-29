package io.github.llewvallis.cfs.ast;

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IdentAst extends AstNode {

  private final String content;

  public IdentAst(String content) {
    this.content = content;
  }

  @Override
  public List<AstNode> getChildren() {
    return Collections.emptyList();
  }
}
