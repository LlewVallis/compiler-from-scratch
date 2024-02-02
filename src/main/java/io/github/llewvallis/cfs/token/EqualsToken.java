package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class EqualsToken extends PunctuationToken {

  public EqualsToken(Span span) {
    super(span);
  }
}
