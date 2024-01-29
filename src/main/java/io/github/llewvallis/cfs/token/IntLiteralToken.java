package io.github.llewvallis.cfs.token;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntLiteralToken extends Token {

  private final int value;

  public IntLiteralToken(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
