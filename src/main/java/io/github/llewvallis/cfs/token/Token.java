package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.Getter;

/**
 * Tokens are represented as subclasses of this class. An enum could have been used instead, but
 * some tokens have extra fields - such as an integer literal's value. Otherwise, these subclasses
 * are very boring.
 *
 * <p>As with ASTs, equality between tokens is mostly present for testing. Spans are ignored when
 * checking for equality.
 */
public abstract sealed class Token
    permits EofToken, IdentToken, IntLiteralToken, KwToken, PunctuationToken {

  @Getter private final Span span;

  public Token(Span span) {
    this.span = span;
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
