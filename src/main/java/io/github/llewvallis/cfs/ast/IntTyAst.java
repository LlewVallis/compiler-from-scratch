package io.github.llewvallis.cfs.ast;

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntTyAst extends TyAst {

  @Override
  public List<AstNode> getChildren() {
    return Collections.emptyList();
  }
}
