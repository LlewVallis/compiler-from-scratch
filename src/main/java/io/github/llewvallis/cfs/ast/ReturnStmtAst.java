package io.github.llewvallis.cfs.ast;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ReturnStmtAst extends StmtAst {

  private final ExprAst value;

  public ReturnStmtAst(ExprAst value) {
    this.value = value;
  }

  @Override
  public List<AstNode> getChildren() {
    return List.of(value);
  }
}