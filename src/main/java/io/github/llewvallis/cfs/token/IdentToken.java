package io.github.llewvallis.cfs.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IdentToken extends Token {

  @Getter private final String content;

  public IdentToken(String content) {
    this.content = content;
  }
}
