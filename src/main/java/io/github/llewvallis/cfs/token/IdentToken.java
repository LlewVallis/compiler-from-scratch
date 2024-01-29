package io.github.llewvallis.cfs.token;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IdentToken extends Token {

  private final String content;

  public IdentToken(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
