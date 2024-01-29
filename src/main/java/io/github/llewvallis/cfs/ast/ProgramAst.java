package io.github.llewvallis.cfs.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class ProgramAst extends AstNode {

  private final List<FunctionAst> functions;

  public ProgramAst(List<FunctionAst> functions) {
    this.functions = new ArrayList<>(functions);
  }

  @Override
  public List<AstNode> getChildren() {
    return Collections.unmodifiableList(functions);
  }
}
