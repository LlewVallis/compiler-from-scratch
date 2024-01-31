package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.AssignmentExprAst;
import io.github.llewvallis.cfs.ast.FunctionAst;
import io.github.llewvallis.cfs.ast.IdentAst;
import io.github.llewvallis.cfs.ast.VarExprAst;

/**
 * Some AST nodes reference declared identifiers, e.g. the expression {@code a = 42}. This pass
 * links those references to the corresponding declaration and reports any uses of undeclared
 * variables.
 */
public class ResolveNames implements AstVisitor {

  private final CollectNames collected;

  public ResolveNames(CollectNames collected) {
    this.collected = collected;
  }

  private void throwUndeclared(IdentAst ident) throws AnalysisException {
    throw new AnalysisException("use of undeclared variable " + ident);
  }

  @Override
  public void visitAssignmentExpr(AssignmentExprAst ast) throws AnalysisException {
    var function = ast.findAncestor(FunctionAst.class);
    var decl = collected.getVariable(function.getName(), ast.getVariable());
    if (decl == null) throwUndeclared(ast.getVariable());
    ast.setDecl(decl);

    AstVisitor.super.visitAssignmentExpr(ast);
  }

  @Override
  public void visitVariableExpr(VarExprAst ast) throws AnalysisException {
    var function = ast.findAncestor(FunctionAst.class);
    var decl = collected.getVariable(function.getName(), ast.getName());
    if (decl == null) throwUndeclared(ast.getName());
    ast.setDecl(decl);

    AstVisitor.super.visitVariableExpr(ast);
  }
}
