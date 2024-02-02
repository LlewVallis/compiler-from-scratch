package io.github.llewvallis.cfs.reporting;

import io.github.llewvallis.cfs.token.Token;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class ParseError extends CompileError {

  private final String expected;
  private final Token actual;

  public ParseError(String expected, Token actual) {
    super(actual.getSpan(), "expected " + expected);
    this.expected = expected;
    this.actual = actual;
  }
}
