package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.Ast;
import io.github.llewvallis.cfs.reporting.ErrorReporter;

/** Orchestrates validation and transformation passes over an AST. */
public class Analyzer {

  private final CollectNames collectNames;

  private final ResolveNames resolveNames;

  public Analyzer(ErrorReporter reporter) {
    collectNames = new CollectNames(reporter);
    resolveNames = new ResolveNames(reporter, collectNames);
  }

  public void analyze(Ast ast) {
    ast.accept(collectNames);
    ast.accept(resolveNames);
  }
}
