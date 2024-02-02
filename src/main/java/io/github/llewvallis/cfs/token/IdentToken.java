package io.github.llewvallis.cfs.token;

import io.github.llewvallis.cfs.reporting.Span;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public final class IdentToken extends Token {

  @Getter private final String content;

  public IdentToken(Span span, String content) {
    super(span);
    this.content = content;
  }
}
