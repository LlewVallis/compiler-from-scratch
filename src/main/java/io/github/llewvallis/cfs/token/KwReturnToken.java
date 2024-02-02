package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class KwReturnToken extends KwToken {

  public KwReturnToken(Span span) {
    super(span);
  }
}
