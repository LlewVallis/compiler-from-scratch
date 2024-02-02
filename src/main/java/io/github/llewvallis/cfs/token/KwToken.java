package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class KwToken extends Token permits KwIntToken, KwReturnToken {

  public KwToken(Span span) {
    super(span);
  }
}
