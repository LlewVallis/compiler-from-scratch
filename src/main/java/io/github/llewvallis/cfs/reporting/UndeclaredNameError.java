package io.github.llewvallis.cfs.reporting;

import io.github.llewvallis.cfs.ast.IdentAst;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class UndeclaredNameError extends CompileError {

  private final IdentAst ast;

  public UndeclaredNameError(IdentAst ast) {
    super(ast.getSpan(), "undeclared name");
    this.ast = ast;
  }
}
