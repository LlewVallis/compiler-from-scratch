package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.Ast;

/** Runs various validation and transformation passes over an AST. */
public class Analyzer {

  private final CollectNames collectNames = new CollectNames();

  private final ResolveNames resolveNames = new ResolveNames(collectNames);

  public void analyze(Ast ast) throws AnalysisException {
    ast.accept(collectNames);
    ast.accept(resolveNames);
  }
}
