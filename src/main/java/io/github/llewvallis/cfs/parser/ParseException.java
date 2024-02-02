package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.token.Token;
import lombok.Getter;

public class ParseException extends Exception {

  @Getter private final String expected;

  @Getter private final Token actual;

  public ParseException(String expected, Token actual) {
    this.expected = expected;
    this.actual = actual;
  }
}
