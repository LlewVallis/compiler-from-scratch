package io.github.llewvallis.cfs.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IntLiteralToken extends Token {

  @Getter private final int value;

  public IntLiteralToken(int value) {
    this.value = value;
  }
}
