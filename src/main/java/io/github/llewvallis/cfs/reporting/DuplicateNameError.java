package io.github.llewvallis.cfs.reporting;

import io.github.llewvallis.cfs.ast.IdentAst;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class DuplicateNameError extends CompileError {

  private final IdentAst ast;

  public DuplicateNameError(IdentAst ast) {
    super(ast.getSpan(), "duplicate declaration");
    this.ast = ast;
  }
}
