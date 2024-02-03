package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.FunctionAst;
import io.github.llewvallis.cfs.ast.VarExprAst;
import io.github.llewvallis.cfs.reporting.ErrorReporter;
import io.github.llewvallis.cfs.reporting.UndeclaredNameError;

/**
 * Some AST nodes reference declared identifiers, e.g. the expression {@code a = 42}. This pass
 * links those references to the corresponding declaration and reports any uses of undeclared
 * variables.
 */
public class ResolveNames extends AnalysisPass {

  private final CollectNames collected;

  public ResolveNames(ErrorReporter reporter, CollectNames collected) {
    super(reporter);
    this.collected = collected;
  }

  @Override
  public void visitVariableExpr(VarExprAst ast) {
    var function = ast.findAncestor(FunctionAst.class);
    var decl = collected.getVariable(function.getName(), ast.getName());
    if (decl == null) report(new UndeclaredNameError(ast.getName()));
    ast.setDecl(decl);

    super.visitVariableExpr(ast);
  }
}
