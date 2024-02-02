package io.github.llewvallis.cfs.parser;

import io.github.llewvallis.cfs.reporting.Span;

/** Used by the parser to build spans by tracking how much input has been used. */
public class SpanTracker {

  private final TokenStream tokens;
  private final int start;

  public SpanTracker(TokenStream tokens) {
    this.tokens = tokens;
    start = tokens.getNextPosition();
  }

  public Span finish() {
    var end = tokens.getLastPosition();
    return new Span(start, Math.max(start, end));
  }
}
