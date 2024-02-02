package io.github.llewvallis.cfs.ast.analysis;

import io.github.llewvallis.cfs.ast.*;

/**
 * An implementation of the visitor pattern for walking ASTs. Each visitor method has a default
 * implementation that just visits the children of the node. Overriding methods will usually call
 * this super/default implementation to continue walking before or after doing some work.
 */
public interface AstVisitor {

  private void visitChildren(Ast node) {
    for (var child : node.getChildren()) {
      child.accept(this);
    }
  }

  default void visitProgram(ProgramAst ast) {
    visitChildren(ast);
  }

  default void visitFunction(FunctionAst ast) {
    visitChildren(ast);
  }

  default void visitVarDecl(VarDeclAst ast) {
    visitChildren(ast);
  }

  default void visitIntTy(IntTyAst ast) {
    visitChildren(ast);
  }

  default void visitBlock(BlockAst ast) {
    visitChildren(ast);
  }

  default void visitVarDeclStmt(VarDeclStmtAst ast) {
    visitChildren(ast);
  }

  default void visitReturnStmt(ReturnStmtAst ast) {
    visitChildren(ast);
  }

  default void visitExprStmt(ExprStmtAst ast) {
    visitChildren(ast);
  }

  default void visitVariableExpr(VarExprAst ast) {
    visitChildren(ast);
  }

  default void visitAssignmentExpr(AssignmentExprAst ast) {
    visitChildren(ast);
  }

  default void visitIntLiteralExpr(IntLiteralExprAst ast) {
    visitChildren(ast);
  }

  default void visitIdent(IdentAst ast) {
    visitChildren(ast);
  }
}
