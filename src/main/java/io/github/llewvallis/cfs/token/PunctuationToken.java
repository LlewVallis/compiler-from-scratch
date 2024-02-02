package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class PunctuationToken extends Token
    permits CloseBraceToken,
        CloseParenToken,
        CommaToken,
        EqualsToken,
        OpenBraceToken,
        OpenParenToken,
        SemicolonToken {

  public PunctuationToken(Span span) {
    super(span);
  }
}
