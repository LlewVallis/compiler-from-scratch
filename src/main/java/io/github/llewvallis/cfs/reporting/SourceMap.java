package io.github.llewvallis.cfs.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of a source file capable of querying for information about lines, rather than
 * just dealing with offsets.
 */
public class SourceMap {

  /** The index on which each line starts. */
  private final List<Integer> lines = new ArrayList<>();

  private final String source;

  public SourceMap(String source) {
    this.source = source;

    lines.add(0);

    for (var i = 0; i < source.length(); i++) {
      if (source.charAt(i) == '\n') {
        lines.add(i + 1);
      }
    }
  }

  public record LineCol(int line, int col) {

    @Override
    public String toString() {
      return (line + 1) + ":" + (col + 1);
    }
  }

  public LineCol getLineCol(int index) {
    for (var line = lines.size() - 1; line >= 0; line--) {
      var start = lines.get(line);
      if (start <= index) {
        return new LineCol(line, index - start);
      }
    }

    throw new AssertionError();
  }

  public String getLineContent(int line) {
    var start = lines.get(line);
    var end = line + 1 < lines.size() ? lines.get(line + 1) : source.length();
    return source.substring(start, end).trim();
  }
}
