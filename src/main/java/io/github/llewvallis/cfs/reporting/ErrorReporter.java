package io.github.llewvallis.cfs.reporting;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/** Allows reporting compiler errors so they can be queried later. */
public class ErrorReporter {

  @Getter private final List<CompileError> errors = new ArrayList<>();

  public void report(CompileError error) {
    errors.add(error);
  }

  /**
   * Throws an exception if there are any errors. This is useful to call between different
   * compilation stages where one stage requires all previous data to be valid.
   */
  public void assertNoErrors() throws CompileErrorsException {
    if (errors.isEmpty()) return;
    throw new CompileErrorsException(errors);
  }
}
