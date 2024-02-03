package io.github.llewvallis.cfs.reporting;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class CallArityError extends CompileError {

  private final int expected;
  private final int actual;

  public CallArityError(Span span, int expected, int actual) {
    super(span, "required " + expected + " arguments but found " + actual);
    this.expected = expected;
    this.actual = actual;
  }
}
