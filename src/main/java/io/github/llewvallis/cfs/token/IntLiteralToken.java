package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntLiteralToken extends Token {

  @Getter private final int value;

  public IntLiteralToken(Span span, int value) {
    super(span);
    this.value = value;
  }
}
