package io.github.llewvallis.cfs.token;

/**
 * Tokens are represented as subclasses of this class. An enum could have been used instead, but
 * some tokens have extra fields - such as an integer literal's value. Otherwise, these subclasses
 * are very boring.
 */
public abstract sealed class Token
    permits EofToken, IdentToken, IntLiteralToken, KwToken, PunctuationToken {

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
