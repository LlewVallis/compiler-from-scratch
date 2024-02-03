package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.CallExprAst;
import io.github.llewvallis.cfs.reporting.CallArityError;
import io.github.llewvallis.cfs.reporting.ErrorReporter;

public class CheckCallArity extends AnalysisPass {

  protected CheckCallArity(ErrorReporter reporter) {
    super(reporter);
  }

  @Override
  public void visitCallExpr(CallExprAst ast) {
    var decl = ast.getFunctionDecl();

    if (decl != null) {
      var expected = decl.getParams().size();
      var actual = ast.getArgs().size();

      if (expected != actual) {
        report(new CallArityError(ast.getSpan(), expected, actual));
      }
    }

    super.visitCallExpr(ast);
  }
}
