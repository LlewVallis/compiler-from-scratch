package io.github.llewvallis.cfs.parser;

import lombok.Getter;

public class LexException extends Exception {

  @Getter private final int position;

  public LexException(int position, String message) {
    super(message);
    this.position = position;
  }
}
