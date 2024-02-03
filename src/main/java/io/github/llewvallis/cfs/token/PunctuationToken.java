package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;

public abstract sealed class PunctuationToken extends Token
    permits AndAndToken,
        CloseBraceToken,
        CloseParenToken,
        ColonToken,
        CommaToken,
        EqualsToken,
        MinusToken,
        OpenBraceToken,
        OpenParenToken,
        OrOrToken,
        PlusToken,
        QuestionToken,
        SemicolonToken,
        SlashToken,
        StarToken {

  public PunctuationToken(Span span) {
    super(span);
  }
}
