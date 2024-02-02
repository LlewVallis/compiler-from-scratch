package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class CloseBraceToken extends PunctuationToken {

  public CloseBraceToken(Span span) {
    super(span);
  }
}
