package io.github.llewvallis.cfs.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class BlockAst extends AstNode {

  private final List<StmtAst> stmts;

  public BlockAst(List<StmtAst> stmts) {
    this.stmts = new ArrayList<>(stmts);
  }

  @Override
  public List<AstNode> getChildren() {
    return Collections.unmodifiableList(stmts);
  }
}
