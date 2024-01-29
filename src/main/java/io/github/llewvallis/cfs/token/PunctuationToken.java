package io.github.llewvallis.cfs.token;

public abstract sealed class PunctuationToken extends Token
    permits CloseBraceToken,
        CloseParenToken,
        CommaToken,
        OpenBraceToken,
        OpenParenToken,
        SemicolonToken {}
