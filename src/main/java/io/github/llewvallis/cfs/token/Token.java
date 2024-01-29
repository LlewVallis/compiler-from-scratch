package io.github.llewvallis.cfs.token;

public abstract sealed class Token
    permits EofToken, IdentToken, IntLiteralToken, KwToken, PunctuationToken {

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
