package io.github.llewvallis.cfs.reporting;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class TokenError extends CompileError {

  private final int position;

  public TokenError(int position) {
    super(Span.point(position), "invalid token");
    this.position = position;
  }
}
