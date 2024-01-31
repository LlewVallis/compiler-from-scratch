package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.*;

/**
 * An implementation of the visitor pattern for walking ASTs. Each visitor method has a default
 * implementation that just visits the children of the node. Overriding methods will usually call
 * this super/default implementation to continue walking before or after doing some work.
 */
public interface AstVisitor {

  private void visitChildren(Ast node) throws AnalysisException {
    for (var child : node.getChildren()) {
      child.accept(this);
    }
  }

  default void visitProgram(ProgramAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitFunction(FunctionAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitVarDecl(VarDeclAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitIntTy(IntTyAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitBlock(BlockAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitVarDeclStmt(VarDeclStmtAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitReturnStmt(ReturnStmtAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitExprStmt(ExprStmtAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitVariableExpr(VarExprAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitAssignmentExpr(AssignmentExprAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitIntLiteralExpr(IntLiteralExprAst ast) throws AnalysisException {
    visitChildren(ast);
  }

  default void visitIdent(IdentAst ast) throws AnalysisException {
    visitChildren(ast);
  }
}
