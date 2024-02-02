package io.github.llewvallis.cfs.reporting;

import lombok.Getter;

/**
 * Rather than throwing an exception, we usually want to store compilation errors in a list of be
 * nicely displayed later.
 */
public abstract class CompileError {

  @Getter private final Span span;

  @Getter private final String message;

  public CompileError(Span span, String message) {
    this.span = span;
    this.message = message;
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int hashCode();

  @Override
  public abstract String toString();
}
