package io.github.llewvallis.cfs.reporting;

import java.util.ArrayList;
import java.util.List;

public class CompileErrorsException extends Exception {

  private final List<CompileError> errors;

  public CompileErrorsException(List<CompileError> errors) {
    super(errors.size() + " compile errors");
    this.errors = new ArrayList<>(errors);

    for (var error : errors) {
      // This exception is logically made up of other individual errors. We map each of these errors
      // into its own exception, and add it as a suppressed exception here. This has the nice effect
      // of including each error in the stack trace too
      addSuppressed(new CompileErrorException(error));
    }
  }

  private static class CompileErrorException extends Exception {

    public CompileErrorException(CompileError error) {
      super(error.getMessage());
    }
  }

  /**
   * Formats all the constituent errors into a single string nicely formatted with ANSI escape
   * codes.
   */
  public String prettyPrint(SourceMap sourceMap) {
    var builder = new StringBuilder();

    for (var error : errors) {
      var lineCol = sourceMap.getLineCol(error.getSpan().start());

      // The "\033[" syntax is for ANSI escape codes

      var tagContent = error.getClass().getSimpleName() + " @ " + lineCol;
      var tag = "\033[1;31m[" + tagContent + "]\033[0m ";
      var emptyTag = " ".repeat(tagContent.length() + 1) + "\033[1;31m]\033[0m ";

      builder.append(tag);
      builder.append(error.getMessage());
      builder.append("\n");

      builder.append(emptyTag);
      builder.append("\033[3m");
      builder.append(sourceMap.getLineContent(lineCol.line()));
      builder.append("\033[0m\n");

      builder.append(emptyTag);
      builder.append(" ".repeat(lineCol.col()));
      builder.append("\033[1;31m^\033[0m\n");
    }

    return builder.toString();
  }
}
