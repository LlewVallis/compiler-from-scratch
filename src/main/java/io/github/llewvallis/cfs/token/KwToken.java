package io.github.llewvallis.cfs.token;

public abstract sealed class KwToken extends Token permits KwIntToken, KwReturnToken {}
