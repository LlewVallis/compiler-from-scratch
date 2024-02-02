package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.reporting.CompileError;
import io.github.llewvallis.cfs.reporting.ErrorReporter;

/** Base class for analysis passes that handles reporting errors. */
public abstract class AnalysisPass implements AstVisitor {

  private final ErrorReporter reporter;

  protected AnalysisPass(ErrorReporter reporter) {
    this.reporter = reporter;
  }

  protected void report(CompileError error) {
    reporter.report(error);
  }
}
