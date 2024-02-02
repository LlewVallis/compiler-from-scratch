package io.github.llewvallis.cfs.reporting;

/**
 * A span is a start and end index into the source file. A span can represent both a range on the
 * source text, or a single point if its start and end are equal.
 */
public record Span(int start, int end) {

  public static Span point(int position) {
    return new Span(position, position);
  }
}
