package io.github.llewvallis.cfs.parser;

public class LexException extends ParseException {

  public LexException() {
    super("unknown token");
  }
}
