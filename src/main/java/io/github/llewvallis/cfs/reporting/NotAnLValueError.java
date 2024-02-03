package io.github.llewvallis.cfs.reporting;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class NotAnLValueError extends CompileError {

  public NotAnLValueError(Span span) {
    super(span, "an lvalue is required here");
  }
}
